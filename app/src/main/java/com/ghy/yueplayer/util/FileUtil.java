package com.ghy.yueplayer.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by GHY on 2015/5/18.
 * 文件管理工具类
 */
public class FileUtil {

    private static String YuePlayerRootPath = null;//YuePlayer文件夹根目录
    private static String FolderPath = null;//YuePlayer文件夹子目录

    /*
    * 创建文件夹
    * */
    public static String createFilePath(String filePath) {
        if (getSDCardRoot() != null) {
            File dirFile = new File(createRootPath() + filePath);// SDCard/.../YuePlayer/filePath
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            FolderPath = dirFile + File.separator;// SDCard/.../YuePlayer/filePath/
            return FolderPath;
        } else {
            return null;
        }
    }

    /*
    * 创建一个YuePlayer根目录
    * */
    private static String createRootPath() {
        File rootFile = new File(getSDCardRoot() + "YuePlayer");// SDCard/.../YuePlayer
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        YuePlayerRootPath = rootFile + File.separator;// SDCard/.../YuePlayer/
        return YuePlayerRootPath;
    }

    /*
    * 创建文件，参数为：保存路径和文件名
    * */
    public static File createFile(String path, String fileName) {
        File file = null;
        if (createFilePath(path) != null) {
            file = new File(FolderPath + fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    private static boolean isSDCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private static String getSDCardRoot() {
        return isSDCardExist() ? Environment.getExternalStorageDirectory() + File.separator : null;
    }

    /**
     * 写文件
     *
     * @param content 要写入的内容
     * @param absPath 绝对路径
     * @return
     */
    public static boolean writeFile(String content, String absPath) {
        File file = new File(absPath);
        if (file.exists()) file.delete();
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file.getAbsoluteFile(), false);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fileWriter != null) fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * 升级时需要获取apk安装包的大小
    * 把apk安装包放到手机SD卡根目录下，执行此方法，查看log获取
    * */
    public static void getYueDuApkSize() {
        String path = Environment.getExternalStorageDirectory() + File.separator + "YueDu1.1.apk";

        File file = new File(path);

        if (file.exists()) {
            long size = file.length();
            Log.i("apkSize---->>>>", size + "");
        } else {
            Log.i("apkSize---->>>>", "Not find Apk");
        }
    }
}
