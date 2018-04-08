package org.yang.common.utils;

import android.os.Environment;

import org.yang.common.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Gxy on 2017/6/19
 */

public class Setting {
    private final static String CURRENT_TIMESTAMP_PATTERN = "%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL";
    private static final Setting ourInstance = new Setting();
    public static String APPFILEPATH = null;

    private Setting() {
    }

    public static Setting getInstance() {
        return ourInstance;
    }

    /**
     * 初始化
     */
    public static void init() {
        File storageDirectory = Environment.getExternalStorageDirectory();
        APPFILEPATH = storageDirectory + File.separator + BuildConfig.APPLICATION_ID;

        createAppFileDir();
    }

    /**
     * 创建app文件目录
     */
    private static void createAppFileDir() {
        File fileDir = new File(APPFILEPATH);
        if (!fileDir.exists() || !fileDir.isDirectory()) {
            fileDir.mkdirs();
        }
    }


    /**
     * 获取当前时间戳
     *
     * @return
     */
    public static String getTimestamp() {
        return String.format(CURRENT_TIMESTAMP_PATTERN, new Date());
    }

    /**
     * 创建apk文件
     *
     * @param sufixx
     * @return
     */
    public File createFile(String sufixx) {
        createAppFileDir();

        String apkFilePath = APPFILEPATH + File.separator + getTimestamp() + sufixx;
        File apkFile = new File(apkFilePath);
        if (!apkFile.exists()) {
            try {
                if (apkFile.createNewFile()) {
                    return apkFile;
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }


}
