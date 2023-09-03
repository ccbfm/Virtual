package com.virtual.util.log;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.DeadObjectException;
import android.util.Log;

import com.virtual.util.log.provider.VLogProvider;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 跨应用输出log记录在本应用目录中
 */

public final class VLogExported {

    public static void init(String packageName) {
        init(providerUri(packageName));
    }

    public static void init(Uri uri) {
        Manager.instance().init(uri);
    }

    public static void d(String tag, String msg) {
        Manager.instance().offer(new LogData(VLogLevel.D, tag, msg));
    }

    public static void i(String tag, String msg) {
        Manager.instance().offer(new LogData(VLogLevel.I, tag, msg));
    }

    public static void w(String tag, String msg) {
        Manager.instance().offer(new LogData(VLogLevel.W, tag, msg));
    }

    public static void e(String tag, String msg) {
        Manager.instance().offer(new LogData(VLogLevel.E, tag, msg));
    }

    public static void e(String tag, String msg, Throwable tr) {
        Manager.instance().offer(new LogData(VLogLevel.E, tag, msg + " Throwable: " + Log.getStackTraceString(tr)));
    }


    public static Uri providerUri(String packageName) {
        String authority = packageName + ".provider.VLogProvider";
        return Uri.parse("content://" + authority + "/" + VLogProvider.Config.PATH_V_LOG);
    }

    private static class LogData {
        @VLogLevel
        private final int level;
        private final String tag;
        private final String msg;

        public LogData(@VLogLevel int level, String tag, String msg) {
            this.level = level;
            this.tag = tag;
            this.msg = msg;
        }
    }

    private static class Manager {
        private Manager() {
        }

        private static final class Singleton {
            private static final Manager INSTANCE = new Manager();
        }

        public static Manager instance() {
            return Manager.Singleton.INSTANCE;
        }

        private final LinkedBlockingQueue<LogData> mLogDataQueue = new LinkedBlockingQueue<>();
        private LogTask mLogTask;
        private Uri mProviderUri;
        private boolean mStop = false;

        public void setStop(boolean stop) {
            mStop = stop;
            if (mLogTask != null) {
                mLogTask.interrupt();
            }
        }

        public void init(Uri uri) {
            mProviderUri = uri;
        }

        private Uri checkProvider(Context context) {
            if (mProviderUri == null) {
                mProviderUri = providerUri(context.getPackageName());
            }
            return mProviderUri;
        }

        public void offer(LogData logData) {
            mLogDataQueue.offer(logData);
            if (mLogTask == null) {
                mLogTask = new LogTask();
                mLogTask.start();
            } else {
                synchronized (mLogDataQueue) {
                    mLogDataQueue.notify();
                }
            }
        }

        private void exportedProvider(@VLogLevel int level, String tag, String msg) {
            Context context = VLogConfig.instance().getContext();
            if (context == null) {
                Log.e("VLogExported", "exportedProvider context is null.");
                return;
            }
            Uri uri = checkProvider(context);
            try (ContentProviderClient client = context.getContentResolver()
                    .acquireUnstableContentProviderClient(uri)) {

                ContentValues values = new ContentValues();
                values.put(VLogProvider.Config.KEY_LEVEL, level);
                values.put(VLogProvider.Config.KEY_TAG, tag);
                values.put(VLogProvider.Config.KEY_MESSAGE, msg);
                client.insert(uri, values);
            } catch (DeadObjectException exception) {
                Log.e("VLogExported", "exportedProvider DeadObjectException.");
            } catch (Throwable throwable) {
                Log.e("VLogExported", "exportedProvider Throwable: " + throwable.getMessage());
            }
        }

        public /*static*/ class LogTask extends Thread {
            @Override
            public void run() {
                try {
                    while (mStop) {
                        synchronized (mLogDataQueue) {
                            if (mLogDataQueue.size() == 0) {
                                mLogDataQueue.wait();
                            }
                        }

                        LogData logData = mLogDataQueue.poll();
                        if (logData != null) {
                            exportedProvider(logData.level, logData.tag, logData.msg);
                        }
                    }
                } catch (Throwable throwable) {
                    Log.e("VLogExported", "LogTask run Throwable ", throwable);
                }
            }
        }
    }

}
