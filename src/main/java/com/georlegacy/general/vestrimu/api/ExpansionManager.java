package com.georlegacy.general.vestrimu.api;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.api.exceptions.InvalidExpansionManifestException;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.Getter;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ExpansionManager {

    private final Vestrimu instance;

    @Getter
    private final Set<Expansion> expansions;

    public ExpansionManager(Vestrimu instance) {
        this.instance = instance;
        expansions = new HashSet<>();

        loadExpansions();
        initializeExpansions();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadExpansions() {
        File expansionDirectory = new File("expansions/");
        if (!expansionDirectory.exists()) {
            Vestrimu.getLogger().info("Creating expansions directory");
            expansionDirectory.mkdir();
            return;
        }

        Set<Expansion> result = new HashSet<>();

        Gson gson = new Gson();

        for (File expansionFile : expansionDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"))) {
            Vestrimu.getLogger().info("Found expansion '" + expansionFile.getName() + "'");

            JarFile jarFile;

            try {
                jarFile = new JarFile(expansionFile);
                JarEntry jarEntry = jarFile.getJarEntry("expansion.json");

                if (jarEntry == null) {
                    throw new InvalidExpansionManifestException(new FileNotFoundException("expansion.json manifest not found!"));
                }

                InputStream inputStream = jarFile.getInputStream(jarEntry);

                ExpansionManifest manifest = gson.fromJson(new JsonReader(new InputStreamReader(inputStream)), ExpansionManifest.class);

                result.add(new ExpansionLoader(manifest, expansionFile, getClass().getClassLoader()).getExpansion());
            } catch (IOException e) {
                throw new InvalidExpansionManifestException(e);
            }
        }

        expansions.addAll(result);
    }

    private void initializeExpansions() {
        expansions.forEach(expansion -> expansion.initialize(this.instance));
    }

    public static class ExpansionManifest {

        @Getter
        private final String version;

        @Getter
        private final String author;

        @Getter
        private final String name;

        @Getter
        private final String description;

        @Getter
        private final String entryPoint;

        public ExpansionManifest(String version, String author, String name, String description, String entryPoint) {
            this.version = version;
            this.author = author;
            this.name = name;
            this.description = description;
            this.entryPoint = entryPoint;
        }

    }

}
