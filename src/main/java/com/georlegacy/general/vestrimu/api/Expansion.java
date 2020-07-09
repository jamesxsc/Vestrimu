package com.georlegacy.general.vestrimu.api;

import com.georlegacy.general.vestrimu.Vestrimu;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public abstract class Expansion {

    public abstract void initialize(Vestrimu instance);

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface VestrimuExpansion {

    }

}
