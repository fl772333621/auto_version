package com.mfanw.auto_version;


import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AutoVersion {

    private static final DateFormat SDF = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public static void main(String[] args) throws Exception {
        backup();
        System.out.println("完成全部操作");
    }

    private static void backup() throws Exception {
        String sourcePath = getSourcePath();
        File sourceFile = new File(sourcePath);
        String backupPath = getBackupPath(sourceFile);
        String time = SDF.format(new Date());
        backup(sourceFile, sourcePath, backupPath, time);
        checkDelete(new File(backupPath), sourcePath, backupPath, time);
    }

    protected static String getSourcePath() throws Exception {
        // 读取source配置信息
        File sourceConfigFile = null;
        for (int i = 0; i < 26; i++) {
            char disk = (char) ('A' + i);
            sourceConfigFile = new File(disk + "://source.auto_version");
            if (sourceConfigFile.exists()) break;
        }
        if (!sourceConfigFile.exists()) throw new Exception("未搜索到 source.auto_version 配置文件");
        System.out.println("source.auto_version 配置文件 " + sourceConfigFile.getAbsolutePath());
        // 解析source配置（哪些文件夹/文件/文件类型不需要备份）
        Map<String, String> sourceConfigs = toConfigs(sourceConfigFile);
        String sourcePath = sourceConfigFile.getParent() + MapUtils.getString(sourceConfigs, "sourcePath", "");
        if (!new File(sourcePath).isDirectory()) throw new Exception(sourcePath + " 文件夹不存在");
        if (!sourcePath.endsWith("\\")) sourcePath += "\\";
        System.out.println("待备份目录 sourcePath=" + sourcePath);
        return sourcePath;
    }

    protected static String getBackupPath(File sourceFile) throws Exception {
        // 读取backup配置信息
        File backupConfigFile = null;
        for (int i = 0; i < 26; i++) {
            char disk = (char) ('A' + i);
            backupConfigFile = new File(disk + "://backup.auto_version");
            if (backupConfigFile.exists()) break;
        }
        if (!backupConfigFile.exists()) throw new Exception("未搜索到 backup.auto_version 配置文件");
        System.out.println("backup.auto_version 配置文件 " + backupConfigFile.getAbsolutePath());

        // 解析target配置（备份到的文件夹的名称，版本化的文件夹的名称）
        Map<String, String> backupConfigs = toConfigs(backupConfigFile);
        String backupPath = backupConfigFile.getParent() + MapUtils.getString(backupConfigs, "backupPath", "backup");
        long size = sourceFile.getTotalSpace() * 100 / 1024 / 1024 / 1024;
        backupPath += "_" + (size / 100) + "." + (size % 100) + "GB\\";
        if (!backupPath.endsWith("\\")) backupPath += "\\";
        System.out.println("备份至目录 backupPath=" + backupPath);
        // 如果back是soure的子目录则报错停止
        if (backupPath.startsWith(sourceFile.getAbsolutePath())) throw new Exception("backupPath 不能是 sourcePath的子目录");
        return backupPath;
    }

    private static Map<String, String> toConfigs(File file) throws IOException {
        Map<String, String> maps = new HashMap<>();
        String content = FileUtils.readFileToString(file);
        if (content == null || content.isEmpty()) return maps;
        String[] lines = content.replaceAll("\r", "").split("\n");
        for (String line : lines) {
            String[] ps = line.split("=");
            maps.put(ps[0], ps[1]);
        }
        return maps;
    }

    private static void backup(File sourceFile, String sourcePath, String backupPath, String time) throws IOException {
        if (sourceFile == null || !sourceFile.exists()) return;
        if (sourceFile.isDirectory()) {
            File[] children = sourceFile.listFiles();
            if (children != null && children.length > 0) {
                for (File child : children) backup(child, sourcePath, backupPath, time);
            }
            return;
        }
        if (sourceFile.getAbsolutePath().endsWith(".auto_version")) return;
        long sourceSize = FileUtils.sizeOf(sourceFile);
        File backupFile = new File(backupPath + sourceFile.getAbsolutePath().substring(sourcePath.length()));
        if (!backupFile.exists()) {
            FileUtils.copyFile(sourceFile, backupFile);
            System.out.println("新增 " + sourceFile.getAbsolutePath() + " -> " + backupFile.getAbsolutePath());
            return;
        }
        if (sourceSize != FileUtils.sizeOf(backupFile) || sourceFile.lastModified() != backupFile.lastModified()) {
            FileUtils.moveFile(backupFile, new File(backupFile.getAbsolutePath() + "." + time + ".auto_version"));
            FileUtils.copyFile(sourceFile, backupFile);
            System.out.println("修改 " + sourceFile.getAbsolutePath() + " -> " + backupFile.getAbsolutePath());
            return;
        }
        System.out.println("相同 " + sourceFile.getAbsolutePath() + " -> " + backupFile.getAbsolutePath());
    }

    private static void checkDelete(File backupFile, String sourcePath, String backupPath, String time) throws IOException {
        if (backupFile == null || !backupFile.exists()) return;
        if (backupFile.isDirectory()) {
            File[] children = backupFile.listFiles();
            if (children != null && children.length > 0) {
                for (File child : children) checkDelete(child, sourcePath, backupPath, time);
            }
            return;
        }
        if (backupFile.getAbsolutePath().endsWith(".auto_version")) return;
        File sourceFile = new File(sourcePath + backupFile.getAbsolutePath().substring(backupPath.length()));
        if (!sourceFile.exists()) {
            FileUtils.moveFile(backupFile, new File(backupFile.getAbsolutePath() + "." + time + ".auto_version"));
            System.out.println("删除 " + backupFile.getAbsolutePath());
        }
    }

}