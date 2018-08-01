package com.georlegacy.general.vestrimu.core.tasks;

import com.georlegacy.general.vestrimu.Vestrimu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public class ClearTempDirectory implements Runnable {

    private static final File tmpDir = new File(File.separator + "tmp" + File.separator);

    @Override
    public void run() {
        long currentMillis = System.currentTimeMillis();
        Vestrimu.getLogger().info("Clearing disused files from temp directory");
        for (File tmpFile : Objects.requireNonNull(tmpDir.listFiles(f -> !f.isDirectory()))) {
            try {
                BasicFileAttributes attrs = Files.readAttributes(tmpFile.toPath(), BasicFileAttributes.class);
                long creationMillis = attrs.creationTime().toMillis();
                long accessMillis = attrs.lastAccessTime().toMillis();

                if ((creationMillis < (currentMillis - (2 * 1000 * 60 * 60))) && (accessMillis < (currentMillis - (2 * 1000 * 60 * 60))))
                    if (tmpFile.delete())
                        Vestrimu.getLogger().info("Successfully deleted temp file " + tmpFile.getName());
                    else
                        Vestrimu.getLogger().info("Failed to delete temp file " + tmpFile.getName());

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
