package com.georlegacy.general.vestrimu.core.tasks;

import com.georlegacy.general.vestrimu.Vestrimu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class ClearTempDirectory implements Runnable {

    private static final File tmpDir = new File("tmp" + File.separator);

    @Override
    public void run() {
        long currentMillis = System.currentTimeMillis();
        Vestrimu.getLogger().info("Clearing disused files from temp directory");
        for (File tmpFile : tmpDir.listFiles()) {
            if (tmpFile.isDirectory()) {
                proccessDirectory(tmpFile);
            } else {
                proccessFile(tmpFile);
            }
        }
    }

    private void proccessDirectory(File directory) {
        for (File tmpFile : directory.listFiles()) {
            if (tmpFile.isDirectory()) {
                proccessDirectory(tmpFile);
            } else {
                proccessFile(tmpFile);
            }
        }
    }

    private void proccessFile(File file) {
        try {
            long currentMillis = System.currentTimeMillis();
            
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            long creationMillis = attrs.creationTime().toMillis();
            long accessMillis = attrs.lastAccessTime().toMillis();

            if ((creationMillis < (currentMillis - (2 * 1000 * 60 * 60))) && (accessMillis < (currentMillis - (2 * 1000 * 60 * 60))))
                if (file.delete())
                    Vestrimu.getLogger().info("Successfully deleted temp file " + file.getName());
                else
                    Vestrimu.getLogger().info("Failed to delete temp file " + file.getName());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}