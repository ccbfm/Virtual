package com.virtual.util.persist.file;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public final class VFileIO {

    private VFileIO() {
        throw new UnsupportedOperationException("u can't instantiate me.");
    }

    /**
     * 将字符串写入文件
     *
     * @param filePath 文件路径
     * @param content  写入内容
     * @param append   是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean writeFileFromString(String filePath, String content, boolean append) {
        return writeFileFromString(VFile.getFileByPath(filePath), content, append);
    }

    /**
     * 将字符串写入文件
     *
     * @param file    文件
     * @param content 写入内容
     * @param append  是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean writeFileFromString(File file, String content, boolean append) {
        if (file == null || content == null) return false;
        if (!VFile.createOrExistsFile(file)) return false;
        try (FileWriter fileWriter = new FileWriter(file, append)) {
            fileWriter.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String readFileToString(final String filePath) {
        return readFileToString(filePath, null);
    }

    public static String readFileToString(final String filePath, final String charsetName) {
        return readFileToString(VFile.getFileByPath(filePath), charsetName);
    }

    /**
     * Return the string in file.
     *
     * @param file The file.
     * @return the string in file
     */
    public static String readFileToString(final File file) {
        return readFileToString(file, null);
    }

    public static String readFileToString(final File file, final String charsetName) {
        byte[] bytes = readFileToBytesByStream(file);
        if (bytes == null) return null;
        if (TextUtils.isEmpty(charsetName)) {
            return new String(bytes);
        } else {
            try {
                return new String(bytes, charsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    public static byte[] readFileToBytesByStream(final String filePath) {
        return readFileToBytesByStream(filePath, null);
    }

    public static byte[] readFileToBytesByStream(final File file) {
        return readFileToBytesByStream(file, null);
    }

    public static byte[] readFileToBytesByStream(final String filePath,
                                                 final OnProgressUpdateListener listener) {
        return readFileToBytesByStream(VFile.getFileByPath(filePath), listener);
    }

    public static byte[] readFileToBytesByStream(final File file,
                                                 final OnProgressUpdateListener listener) {
        if (!VFile.isFileExists(file)) return null;
        try {
            ByteArrayOutputStream os = null;
            InputStream is = new BufferedInputStream(new FileInputStream(file), sBufferSize);
            try {
                os = new ByteArrayOutputStream();
                byte[] b = new byte[sBufferSize];
                int len;
                if (listener == null) {
                    while ((len = is.read(b, 0, sBufferSize)) != -1) {
                        os.write(b, 0, len);
                    }
                } else {
                    double totalSize = is.available();
                    int curSize = 0;
                    listener.onProgressUpdate(0);
                    while ((len = is.read(b, 0, sBufferSize)) != -1) {
                        os.write(b, 0, len);
                        curSize += len;
                        listener.onProgressUpdate(curSize / totalSize);
                    }
                }
                return os.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int sBufferSize = 524288;

    public static void setBufferSize(int bufferSize) {
        sBufferSize = bufferSize;
    }

    public interface OnProgressUpdateListener {
        void onProgressUpdate(double progress);
    }

    public static void copyFile(String sourcePath, String targetPath) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(sourcePath);
            outputStream = new FileOutputStream(targetPath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
