package com.virtual.util.log.flavor;

import android.util.Log;

import com.virtual.util.log.VLevel;
import com.virtual.util.log.VLevelString;
import com.virtual.util.log.VLogConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VSaveLog extends VBaseLog {
    @VLevel
    protected final int mSaveLevel;
    protected final String mSaveRootDir;
    protected final ExecutorService mExecutor;
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 5;

    public VSaveLog() {
        mSaveLevel = VLogConfig.instance().getSaveLevel();
        mSaveRootDir = VLogConfig.instance().getSaveRootDir();
        mExecutor = Executors.newSingleThreadExecutor();
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

    protected boolean checkSave(@VLevel int saveLevel) {
        return mSaveLevel <= saveLevel;
    }

    @Override
    protected String formatLog(String level, String tag, String msg) {
        return "[" + getCurrentTime() + "] " + super.formatLog(level, tag, msg) + "\n";
    }

    protected void saveLogToFile(@VLevel int level, String tag, String msg) {
        String levelStr = VLevelString.levelString(level);
        mExecutor.execute(new SaveTask(this, levelStr, tag, msg));
    }

    private String repair0(int num) {
        return repair0(num, 2);
    }

    private String repair0(int num, int digit) {
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

        File file = new File(mSaveRootDir, name);
        if (file.length() >= MAX_FILE_SIZE) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            int millisecond = calendar.get(Calendar.MILLISECOND);

            String rename = year + "" + repair0(month) + "" + repair0(day) + "_" + repair0(hour) + "" + repair0(minute) + "" + repair0(second) + "_" + repair0(millisecond, 3) + ".txt";
            File reFile = new File(mSaveRootDir, rename);
            if (!file.renameTo(reFile)) {
                Log.d("SaveLog", "getOrCreateSaveFile-delete=" + file.delete());
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
        if (checkSave(VLevel.D)) {
            saveLogToFile(VLevel.D, tag, msg);
        }
    }

    @Override
    public void i(String tag, String msg) {
        if (checkSave(VLevel.I)) {
            saveLogToFile(VLevel.I, tag, msg);
        }
    }

    @Override
    public void w(String tag, String msg) {
        if (checkSave(VLevel.W)) {
            saveLogToFile(VLevel.W, tag, msg);
        }
    }

    @Override
    public void e(String tag, String msg) {
        if (checkSave(VLevel.E)) {
            saveLogToFile(VLevel.E, tag, msg);
        }
    }

    @Override
    public void e(String tag, String msg, Throwable tr) {
        if (checkSave(VLevel.E)) {
            saveLogToFile(VLevel.E, tag, msg + " Throwable: " + Log.getStackTraceString(tr));
        }
    }
}
