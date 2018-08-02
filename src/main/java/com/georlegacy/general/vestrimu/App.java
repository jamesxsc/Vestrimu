package com.georlegacy.general.vestrimu;

import com.georlegacy.general.vestrimu.core.objects.behaviour.GuildBehaviourRecord;

public class App {

    public static void main(String[] args) {
        //new Vestrimu();
        System.out.println(new GuildBehaviourRecord(true).serialize().toString(4));
    }

}
