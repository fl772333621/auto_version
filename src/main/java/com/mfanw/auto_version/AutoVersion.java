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
    }

    private static void backup() throws Exception {
        // 读取source配置信息
        File sourceConfigFile = null;
        for (int i = 0; i < 26; i++) {
            char disk = (char) ('A' + i);
            sourceConfigFile = new File(disk + "://source.auto_version");
            if (sourceConfigFile.exists()) break;
        }
        if (!sourceConfigFile.exists()) throw new Exception("未搜索到 source.auto_version 配置文件");
        System.out.println("source.auto_version 配置文件 " + sourceConfigFile.getAbsolutePath());
        // 读取backup配置信息
        File backupConfigFile = null;
        for (int i = 0; i < 26; i++) {
            char disk = (char) ('A' + i);
            backupConfigFile = new File(disk + "://backup.auto_version");
            if (backupConfigFile.exists()) break;
        }
        if (!backupConfigFile.exists()) throw new Exception("未搜索到 backup.auto_version 配置文件");
        System.out.println("backup.auto_version 配置文件 " + backupConfigFile.getAbsolutePath());
        // 解析source配置（哪些文件夹/文件/文件类型不需要备份）
        Map<String, String> sourceConfigs = toConfigs(sourceConfigFile);
        String sourcePath = sourceConfigFile.getParent() + MapUtils.getString(sourceConfigs, "sourcePath", "");
        if (!new File(sourcePath).isDirectory()) throw new Exception(sourcePath + " 文件夹不存在");
        if (!sourcePath.endsWith("\\")) sourcePath += "\\";
        System.out.println("待备份目录 sourcePath=" + sourcePath);
        // 解析target配置（备份到的文件夹的名称，版本化的文件夹的名称）
        Map<String, String> backupConfigs = toConfigs(backupConfigFile);
        String backupPath = backupConfigFile.getParent() + MapUtils.getString(backupConfigs, "backupPath", "backup");
        long size = sourceConfigFile.getTotalSpace() * 100 / 1024 / 1024 / 1024;
        backupPath += "_" + (size / 100) + "." + (size % 100) + "GB\\" + MapUtils.getString(sourceConfigs, "sourcePath", "");
        if (!backupPath.endsWith("\\")) backupPath += "\\";
        System.out.println("备份至目录 backupPath=" + backupPath);
        // 如果back是soure的子目录则报错停止
        if (backupPath.startsWith(sourcePath)) throw new Exception("backupPath 不能是 sourcePath的子目录");
        backup(new File(sourcePath), sourcePath, backupPath, SDF.format(new Date()));
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
        if (sourceFile.isDirectory()) {
            File[] children = sourceFile.listFiles();
            if (children != null && children.length > 0) {
                for (File child : children) backup(child, sourcePath, backupPath, time);
            }
            return;
        }
        long sourceSize = FileUtils.sizeOf(sourceFile);
        File backupFile = new File(backupPath + sourceFile.getAbsolutePath().substring(sourcePath.length()));
        if (!backupFile.exists()) {
            FileUtils.copyFile(sourceFile, backupFile);
            System.out.println("新增 " + sourceFile.getAbsolutePath() + " -> " + backupFile.getAbsolutePath());
            return;
        }
        if (sourceSize != FileUtils.sizeOf(backupFile) || sourceFile.lastModified() != backupFile.lastModified()) {
            FileUtils.moveFile(backupFile, new File(backupFile.getAbsolutePath() + "_" + time));
            FileUtils.copyFile(sourceFile, backupFile);
            System.out.println("修改 " + sourceFile.getAbsolutePath() + " -> " + backupFile.getAbsolutePath());
            return;
        }
        System.out.println("相同 " + sourceFile.getAbsolutePath() + " -> " + backupFile.getAbsolutePath());
    }
}