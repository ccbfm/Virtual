package com.virtual.util.common;

import android.os.Build;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtils {

    public static long copyFile(String sourcePath, String targetPath) throws Throwable {
        try (FileInputStream inputStream = new FileInputStream(sourcePath);
             FileOutputStream outputStream = new FileOutputStream(targetPath)) {
            long all_len = 0L;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                all_len = android.os.FileUtils.copy(inputStream, outputStream);
            } else {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    all_len += length;
                    outputStream.write(buffer, 0, length);
                }
            }
            outputStream.flush();
            return all_len;
        }
    }

    public static boolean deleteDirFiles(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles != null) {
                    for (File childFile : listFiles) {
                        if (!deleteDirFiles(childFile)) {
                            return false;
                        }
                    }
                }
            }
            return file.delete();
        }
        return false;
    }


    public static class LogFileFilter implements FileFilter {
        private final long mDeleteTime;

        public LogFileFilter(long deleteTime) {
            mDeleteTime = deleteTime;
        }

        @Override
        public boolean accept(File pathname) {
            return pathname.lastModified() < mDeleteTime;
        }
    }

}
