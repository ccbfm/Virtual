package com.virtual.util.common;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileUtils {

    /**
     * @param srcFileString 压缩文件和文件夹
     * @param zipFileString 输出目录文件路径
     * @throws Exception Exception
     */
    public static void zipFiles(String srcFileString,
                                String zipFileString) throws Exception {
        File file = new File(srcFileString);
        if (!file.exists()) {
            Log.w("ZipFileUtils", "zipFiles srcFile is not exists");
            return;
        }
        //创建ZIP
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString));
        //压缩
        zipFiles(file.getParent(), "", file.getName(), outZip);
        //完成和关闭
        outZip.finish();
        outZip.close();
    }

    /**
     * 压缩文件
     */
    private static void zipFiles(String rootPath,
                                 String folderString,
                                 String fileString,
                                 ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam == null) {
            return;
        }
        String name = folderString + File.separator + fileString;
        File file = new File(rootPath + name);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(name);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else {
            //文件夹
            String[] fileList = file.list();
            //没有子文件和压缩
            if (fileList != null && fileList.length > 0) {
                //子文件和递归
                for (String childFile : fileList) {
                    zipFiles(rootPath, name, childFile, zipOutputSteam);
                }
            } else {
                ZipEntry zipEntry = new ZipEntry(name);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }
        }
    }

}
