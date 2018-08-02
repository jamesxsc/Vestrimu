package com.georlegacy.general.vestrimu.core.objects.behaviour;

import lombok.Getter;

public class BehaviourIssuer {

    @Getter private String name;

    @Getter private String discriminator;

    @Getter private BehaviourIssuerType issuerType;

    public BehaviourIssuer(String name, String discriminator, BehaviourIssuerType issuerType) {
        this.name = name;
        this.discriminator = discriminator;
        this.issuerType = issuerType;
    }

    public enum BehaviourIssuerType {

        MEMBER(0),
        SELF(1);

        @Getter private int key;

        BehaviourIssuerType(int key) {
            this.key = key;
        }

    }

}