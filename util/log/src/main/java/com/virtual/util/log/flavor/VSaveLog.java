package com.virtual.util.log.flavor;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.virtual.util.log.VLevelString;
import com.virtual.util.log.VLogConfig;
import com.virtual.util.log.VLogLevel;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class VSaveLog extends VBaseLog {
    @VLogLevel
    protected final int mSaveLevel;
    protected final String mSaveRootDir;
    protected final long mRetainedTime;
    protected final Executor mExecutor;
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 5;
    private String mCurrentLogFileName;
    private static final int MAX_ALL_FILE_SIZE = 1024 * 1024 * 100;

    public VSaveLog(@NonNull VLogConfig logConfig) {
        super(logConfig);
        mSaveLevel = logConfig.getSaveLevel();
        mSaveRootDir = logConfig.getSaveRootDir();
        mRetainedTime = logConfig.getRetainedTime();
        mExecutor = new ThreadPoolExecutor(0, 1,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    protected static class SaveTask implements Runnable {
        private final VSaveLog saveLog;
        private final String level;
        private final String tag;
        private final String msg;

        public SaveTask(VSaveLog saveLog, String level, String tag, String msg) {
            this.saveLog = saveLog;
            this.level = level;
            this.tag = tag;
            this.msg = msg;
        }

        @Override
        public void run() {
            String content = this.saveLog.formatLog(this.level, this.tag, this.msg);
            this.saveLog.writeFileFromString(this.saveLog.getOrCreateSaveFile(), content, true);
        }
    }

    private boolean writeFileFromString(File file, String content, boolean append) {
        if (file == null || content == null) return false;
        if (!createOrExistsFile(file)) return false;
        try (FileWriter fileWriter = new FileWriter(file, append)) {
            fileWriter.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean createOrExistsFile(File file) {
        if (file == null) return false;
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean createOrExistsDir(File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    protected boolean checkSave(@VLogLevel int saveLevel) {
        return mSaveLevel <= saveLevel;
    }

    @Override
    protected String formatLog(String level, String tag, String msg) {
        return "[" + getCurrentTime() + "] " + super.formatLog(level, tag, msg) + "\n";
    }

    protected void saveLogToFile(@VLogLevel int level, String tag, String msg) {
        String levelStr = VLevelString.levelString(level);
        mExecutor.execute(new SaveTask(this, levelStr, tag, msg));
    }

    private static String repair0(int num) {
        return repair0(num, 2);
    }

    private static String repair0(int num, int digit) {
        String numStr = num + "";
        if (digit == 3) {
            int len = numStr.length();
            return len == 1 ? ("00" + num) : (len == 2 ? ("0" + num) : numStr);
        }
        return numStr.length() == 1 ? ("0" + num) : numStr;
    }

    protected File getOrCreateSaveFile() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String name = year + "" + repair0(month) + "" + repair0(day) + ".txt";

        if (!TextUtils.equals(mCurrentLogFileName, name)) {
            mCurrentLogFileName = name;
            Log.d("VSaveLog", "getOrCreateSaveFile clearOldLogFile");
            clearOldLogFile();
        }
        File file = new File(mSaveRootDir, name);
        if (file.length() >= MAX_FILE_SIZE) {
            //int hour = calendar.get(Calendar.HOUR_OF_DAY);
            //int minute = calendar.get(Calendar.MINUTE);
            //int second = calendar.get(Calendar.SECOND);
            //int millisecond = calendar.get(Calendar.MILLISECOND);

            //String rename = year + "" + repair0(month) + "" + repair0(day) + "_" + repair0(hour) + "" + repair0(minute) + "" + repair0(second) + "_" + repair0(millisecond, 3) + ".txt";
            String rename = year + "" + repair0(month) + "" + repair0(day) + "_1.txt";
            File reFile = new File(mSaveRootDir, rename);
            if (reFile.exists()) {
                Log.d("VSaveLog", "getOrCreateSaveFile reFile delete " + reFile.delete());
            }
            if (!file.renameTo(reFile)) {
                if (file.delete()) {
                    Log.d("VSaveLog", "getOrCreateSaveFile delete file");
                    file = new File(mSaveRootDir, name);
                }
            }
        }
        return file;
    }

    protected String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        int millisecond = calendar.get(Calendar.MILLISECOND);
        return year + "_" + repair0(month) + "_" + repair0(day) + " " + repair0(hour) + ":" + repair0(minute) + ":" + repair0(second) + "." + repair0(millisecond, 3);
    }

    @Override
    public void d(String tag, String msg) {
        if (checkSave(VLogLevel.D)) {
            saveLogToFile(VLogLevel.D, tag, msg);
        }
    }

    @Override
    public void i(String tag, String msg) {
        if (checkSave(VLogLevel.I)) {
            saveLogToFile(VLogLevel.I, tag, msg);
        }
    }

    @Override
    public void w(String tag, String msg) {
        if (checkSave(VLogLevel.W)) {
            saveLogToFile(VLogLevel.W, tag, msg);
        }
    }

    @Override
    public void e(String tag, String msg) {
        if (checkSave(VLogLevel.E)) {
            saveLogToFile(VLogLevel.E, tag, msg);
        }
    }

    @Override
    public void e(String tag, String msg, Throwable tr) {
        if (checkSave(VLogLevel.E)) {
            saveLogToFile(VLogLevel.E, tag, msg + " Throwable: " + Log.getStackTraceString(tr));
        }
    }


    protected void clearOldLogFile() {
        if (mRetainedTime > 0L) {
            mExecutor.execute(new DeleteLogFileThread(mSaveRootDir, mRetainedTime));
        }
    }

    private static class DeleteLogFileThread extends Thread {
        private final String path;
        private final long retainedTime;

        public DeleteLogFileThread(String path, long retainedTime) {
            super("delete-log-file");
            this.path = path;
            this.retainedTime = retainedTime;
        }

        @Override
        public void run() {
            try {
                File file = new File(this.path);
                if (getFolderSize(file) > MAX_ALL_FILE_SIZE) {
                    Log.d("VSaveLog", "delete rootFile " + deleteFile(file));
                }
                if (file.isDirectory()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis((System.currentTimeMillis() - this.retainedTime));
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    String name = year + "" + repair0(month) + "" + repair0(day);
                    long deleteTime = Long.parseLong(name);
                    LogFileFilter filter = new LogFileFilter(deleteTime);
                    File[] files = file.listFiles(filter);
                    if (files != null) {
                        int size = 0;
                        for (File logFile : files) {
                            if (logFile != null) {
                                if (logFile.delete()) {
                                    size++;
                                }
                            }
                        }
                        Log.d("VSaveLog", "delete logFile size: " + size);
                    }
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        private long getFolderSize(File directory) {
            if (directory == null) {
                return 0L;
            }
            if (directory.isFile()) {
                return directory.length();
            }
            long length = 0;
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        length += file.length();
                    } else {
                        length += getFolderSize(file);
                    }
                }
            }
            return length;
        }

        public static boolean deleteFile(File file) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                if (children == null) {
                    return true;
                }
                for (File child : children) {
                    boolean success = deleteFile(child);
                    if (!success) {
                        return false;
                    }
                }
            }
            return file.delete();
        }
    }

    private static class LogFileFilter implements FileFilter {
        private final long deleteTime;

        LogFileFilter(long deleteTime) {
            this.deleteTime = deleteTime;
        }

        @Override
        public boolean accept(File pathname) {
            try {
                String name = pathname.getName();
                int index = name.indexOf("_");
                if (index <= 0) {
                    index = name.lastIndexOf(".");
                }
                name = name.substring(0, index);

                long curTime = Long.parseLong(name);
                Log.d("VSaveLog", "LogFileFilter curTime: " + curTime + " " + this.deleteTime);
                return curTime < this.deleteTime;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return false;
        }
    }
}
