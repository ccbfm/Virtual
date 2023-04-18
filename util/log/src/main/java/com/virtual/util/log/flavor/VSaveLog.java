package com.virtual.util.log.flavor;

import android.util.Log;

import com.virtual.util.log.VLevel;
import com.virtual.util.log.VLevelString;
import com.virtual.util.log.VLogConfig;
import com.virtual.util.persist.file.VFile;
import com.virtual.util.persist.file.VFileIO;
import com.virtual.util.thread.VThread;
import com.virtual.util.thread.model.VSimpleTask;
import com.virtual.util.thread.pool.VThreadPoolExecutor;

import java.io.File;
import java.util.Calendar;

public class VSaveLog extends VBaseLog {
    @VLevel
    protected final int mSaveLevel;
    protected final String mSaveRootDir;
    protected final VThreadPoolExecutor mExecutor;
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 5;

    public VSaveLog() {
        mSaveLevel = VLogConfig.instance().getSaveLevel();
        mSaveRootDir = VLogConfig.instance().getSaveRootDir();
        mExecutor = VThread.getFixedPool(1, false);
    }

    protected static class SaveTask extends VSimpleTask<Boolean> {
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
        protected Boolean doTask() throws Throwable {
            String content = this.saveLog.formatLog(this.level, this.tag, this.msg);
            return VFileIO.writeFileFromString(this.saveLog.getOrCreateSaveFile(), content, true);
        }

        @Override
        protected void onSuccess(Boolean result) {
            //Log.d("SaveTask", "onSuccess-result=" + result);
        }
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
        VThread.execute(mExecutor, new SaveTask(this, levelStr, tag, msg));
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
