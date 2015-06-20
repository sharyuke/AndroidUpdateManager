package com.sharyuke.tool.util;

import java.io.File;

import timber.log.Timber;

/**
 * FileHelper
 * Created by sy_home on 2015/6/20.
 */
public class FileHelper {
    public static void deleteFile(File file) {
        if (file == null) return;
        if (file.exists()) {
            String name = file.getName();
            if (file.isFile() && file.delete()) {
                log(name);
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (File file1 : files) {
                    deleteFile(file1);
                }
            }
            if (file.delete()) {
                log(name);
            }
        }
    }

    private static void log(String name) {
        Timber.d("delete " + name);
    }
}
