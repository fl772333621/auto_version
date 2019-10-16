package com.mfanw.auto_version;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class DeleteAutoVersionFile extends AutoVersion {

    public static void main(String[] args) throws Exception {
        String sourcePath = getSourcePath();
        File sourceFile = new File(sourcePath);
        String backupPath = getBackupPath(sourceFile);
        deleteAutoVersionFile(new File(backupPath));
        System.out.println("完成全部操作");
    }

    private static void deleteAutoVersionFile(File backupFile) throws IOException {
        if (backupFile == null || !backupFile.exists()) return;
        if (backupFile.isDirectory()) {
            File[] children = backupFile.listFiles();
            if (children != null && children.length > 0) {
                for (File child : children) deleteAutoVersionFile(child);
            }
            return;
        }
        if (backupFile.getAbsolutePath().endsWith(".auto_version")) {
            FileUtils.forceDelete(backupFile);
            System.out.println("删除 " + backupFile.getAbsolutePath());
        }
    }
}
