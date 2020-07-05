package com.georlegacy.general.vestrimu.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

@SuppressWarnings("rawtypes")
public final class BinderModule extends AbstractModule {

    private static Class clazz;

    public BinderModule(Class clazz) {
        BinderModule.clazz = clazz;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void configure() {
        this.bind(clazz).toInstance(clazz);
    }

}
