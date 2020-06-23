package com.georlegacy.general.vestrimu.core.objects.hungergames;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Member;

public class HungerGamesTribute {

    @Getter
    private final String id;

    @Getter
    private int district;

    @Getter
    @Setter
    private boolean alive;

    public HungerGamesTribute(Member member, int district) {
        id = member.getUser().getId();
        this.district = district;
        alive = true;
    }

}
