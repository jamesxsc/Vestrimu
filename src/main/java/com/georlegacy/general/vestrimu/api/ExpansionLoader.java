package com.georlegacy.general.vestrimu.api;

import com.georlegacy.general.vestrimu.api.exceptions.InvalidExpansionException;
import lombok.Getter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ExpansionLoader extends URLClassLoader {

    @Getter
    private final ExpansionManager.ExpansionManifest manifest;

    @Getter
    private final Expansion expansion;

    public ExpansionLoader(ExpansionManager.ExpansionManifest manifest, File file, ClassLoader parent) throws MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);

        this.manifest = manifest;

        try {
            Class<?> entryPoint;
            try {
                entryPoint = Class.forName(manifest.getEntryPoint(), true, this);

            } catch (ClassNotFoundException e) {
                throw new InvalidExpansionException("Cannot find entry point '" + manifest.getEntryPoint() + "'");
            }

            Class<? extends Expansion> expansionClass;
            try {
                expansionClass = entryPoint.asSubclass(Expansion.class);
            } catch (ClassCastException e) {
                throw new InvalidExpansionException("Entry point '" + manifest.getEntryPoint() + "' does not extend Expansion");
            }

            expansion = expansionClass.newInstance();
        } catch (IllegalAccessException e) {
            throw new InvalidExpansionException("No public constructor");
        } catch (InstantiationException e) {
            throw new InvalidExpansionException(e);
        }
    }

}
