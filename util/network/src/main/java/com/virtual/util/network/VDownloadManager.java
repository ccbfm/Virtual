package com.virtual.util.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class VDownloadManager {

    private static final class Singleton {
        private static final VDownloadManager INSTANCE = new VDownloadManager();
    }

    public static VDownloadManager instance() {
        return VDownloadManager.Singleton.INSTANCE;
    }

    private VDownloadManager() {
    }

    private static final String TAG = "VDownloadManager";

    private final HashMap<String, DownloadTask> mDownloadTaskMap = new HashMap<>();

    public void startDownload(@NonNull String path,
                              @NonNull String url,
                              boolean breakpoint,
                              DownloadStatusAdapter downloadStatus) {
        DownloadTask downloadTask = mDownloadTaskMap.get(path);
        if (downloadTask != null) {
            Log.d(TAG, "task is exists " + path);
            return;
        }
        downloadTask = new DownloadTask(path, url, breakpoint, downloadStatus, new DownloadStatusAdapter() {
            @Override
            public void end(boolean complete, String path) {
                super.end(complete, path);
                mDownloadTaskMap.remove(path);
            }

            @Override
            public void error(String path, String error) {
                super.error(path, error);
                mDownloadTaskMap.remove(path);
            }
        });
        mDownloadTaskMap.put(path, downloadTask);
        downloadTask.start();
    }

    public boolean isDownload(@NonNull String path) {
        return mDownloadTaskMap.get(path) != null;
    }

    public void stopDownload(@NonNull String path) {
        DownloadTask downloadTask = mDownloadTaskMap.remove(path);
        if (downloadTask == null) {
            Log.d(TAG, "task is not exists " + path);
            return;
        }
        downloadTask.stopDownload();
    }


    private static final AtomicLong TASK_NUMBER = new AtomicLong();

    private static class DownloadTask extends Thread implements IDownloadStatus {
        private final String path, url;
        private final boolean breakpoint;
        private final DownloadStatusAdapter downloadStatus, endStatus;
        private final Handler mainHandler;
        private boolean isDownload = false;

        public DownloadTask(@NonNull String path,
                            @NonNull String url,
                            boolean breakpoint,
                            DownloadStatusAdapter downloadStatus,
                            DownloadStatusAdapter endStatus) {
            super("download-task-" + TASK_NUMBER.getAndIncrement());
            this.path = path;
            this.breakpoint = breakpoint;
            this.url = url;
            this.downloadStatus = downloadStatus;
            this.endStatus = endStatus;
            this.mainHandler = new Handler(Looper.getMainLooper());
            this.isDownload = true;
        }

        public boolean isDownload() {
            return this.isDownload;
        }

        public void stopDownload() {
            this.isDownload = false;
            this.interrupt();
        }

        @Override
        public void run() {
            InputStream is = null;
            RandomAccessFile randomAccessFile = null;
            try {
                File downFile = new File(this.path);
                long curLen = 0;
                if (downFile.exists()) {
                    if (this.breakpoint) {
                        curLen = downFile.length();
                    } else {
                        Log.d(TAG, this.path + " delete " + downFile.delete());
                    }
                } else {
                    File parentFile = downFile.getParentFile();
                    if (parentFile != null && !parentFile.exists()) {
                        Log.d(TAG, parentFile + " mkdirs " + parentFile.mkdirs());
                    }
                }

                start(this.path);

                URL httpUrl = new URL(this.url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
                httpURLConnection.setRequestProperty("Charset", "UTF-8");
                httpURLConnection.setRequestProperty("RANGE", "bytes=" + curLen + "-");
                httpURLConnection.connect();
                int code = httpURLConnection.getResponseCode();
                Log.d(TAG, this.path + " responseCode " + code);
                if (code >= 200 && code < 300) {
                    long allLen = httpURLConnection.getContentLengthLong() + curLen;
                    if (curLen == allLen) {
                        //已下载字节和文件总字节相等，说明下载已经完成了
                        end(true, this.path);
                        return;
                    }

                    progress(this.path, curLen, allLen);

                    is = httpURLConnection.getInputStream();
                    byte[] buf = new byte[2048];
                    int len;
                    randomAccessFile = new RandomAccessFile(downFile, "rw");
                    randomAccessFile.seek(curLen);
                    while ((len = is.read(buf)) != -1) {
                        if (!isDownload()) {
                            break;
                        }
                        randomAccessFile.write(buf, 0, len);
                        curLen += len;
                        //传递更新信息
                        progress(this.path, curLen, allLen);
                    }
                    randomAccessFile.close();
                    end((curLen == allLen), this.path);
                } else {
                    end(code == 416, this.path);
                }
            } catch (Throwable throwable) {
                error(this.path, throwable.getMessage());
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (Throwable throwable) {
                    error(this.path, throwable.getMessage());
                }
                try {
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (Throwable throwable) {
                    error(this.path, throwable.getMessage());
                }
            }
        }

        @Override
        public void start(String path) {
            if (this.downloadStatus != null) {
                this.mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DownloadTask.this.downloadStatus.start(path);
                    }
                });
            }
        }

        @Override
        public void progress(String path, long curLen, long allLen) {
            if (this.downloadStatus != null) {
                this.mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DownloadTask.this.downloadStatus.progress(path, curLen, allLen);
                    }
                });
            }
        }

        @Override
        public void end(boolean complete, String path) {
            this.isDownload = false;
            if (this.downloadStatus != null) {
                this.mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DownloadTask.this.downloadStatus.end(complete, path);
                        DownloadTask.this.endStatus.end(complete, path);
                    }
                });
            }
        }

        @Override
        public void error(String path, String error) {
            this.isDownload = false;
            if (this.downloadStatus != null) {
                this.mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DownloadTask.this.downloadStatus.error(path, error);
                        DownloadTask.this.endStatus.error(path, error);
                    }
                });
            }
        }
    }

    public interface IDownloadStatus {
        void start(String path);

        void progress(String path, long curLen, long allLen);

        void end(boolean complete, String path);

        void error(String path, String error);
    }

    public static abstract class DownloadStatusAdapter implements IDownloadStatus {
        @Override
        public void start(String path) {

        }

        @Override
        public void progress(String path, long curLen, long allLen) {

        }

        @Override
        public void end(boolean complete, String path) {

        }

        @Override
        public void error(String path, String error) {

        }
    }
}
