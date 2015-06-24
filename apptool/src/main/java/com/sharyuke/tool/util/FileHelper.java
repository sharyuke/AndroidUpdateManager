package com.sharyuke.tool.util;

import java.io.File;

import timber.log.Timber;

/**
 * FileHelper
 * Created by sy_home on 2015/6/20.
 */
public class FileHelper {
    public static boolean deleteFile(File file) {
        if (file == null) return false;
        boolean deleted = false;
        if (file.exists()) {
            String name = file.getName();
            if (file.isFile()) {
                File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
                if (file.renameTo(to)) {
                    if (file.delete()) {
                        log(name);
                        deleted = true;
                    }
                }
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (File file1 : files) {
                    if (deleteFile(file1)) {
                        deleted = true;
                    }
                }
            }
        }
        return deleted;
    }

    private static void log(String name) {
        Timber.d("delete " + name);
    }
}
