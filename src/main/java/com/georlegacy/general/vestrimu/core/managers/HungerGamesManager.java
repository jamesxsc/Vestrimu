package com.georlegacy.general.vestrimu.core.managers;

import com.georlegacy.general.vestrimu.core.objects.hungergames.HungerGamesGame;
import lombok.Getter;

import java.util.HashMap;

public class HungerGamesManager {

    @Getter
    private final HashMap<String, HungerGamesGame> games;

    public HungerGamesManager() {
        games = new HashMap<>();
    }

}
