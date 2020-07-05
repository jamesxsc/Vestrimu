package com.georlegacy.general.vestrimu.core.objects.hungergames;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;

public class HungerGamesTribute {

    @Getter
    private final String id;

    @Getter
    private final int district;

    @Getter
    @Setter
    private HungerGamesGame.Location location;

    @Getter
    @Setter
    private boolean alive;


    public HungerGamesTribute(Member member, int district, HungerGamesGame.Location location) {
        id = member.getUser().getId();
        this.district = district;
        this.location = location;
        alive = true;
    }

}
