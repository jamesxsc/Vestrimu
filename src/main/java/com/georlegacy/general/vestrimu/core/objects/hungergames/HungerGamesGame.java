package com.georlegacy.general.vestrimu.core.objects.hungergames;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class HungerGamesGame {

    @Getter
    private final TextChannel channel;

    @Getter
    private final Set<HungerGamesTribute> tributes;

    @Getter
    @Setter
    private final GameStatus status;

    private int maxDistrict = 1;
    private boolean maxDistrictFull;

    public HungerGamesGame(TextChannel channel) {
        this.channel = channel;
        tributes = new HashSet<>();
        status = GameStatus.REAPING;
    }

    public HungerGamesTribute addTribute(Member member) {
        if (maxDistrictFull) {
            maxDistrict++;
            maxDistrictFull = false;
        } else
            maxDistrictFull = true;
        HungerGamesTribute tribute = new HungerGamesTribute(member, maxDistrict);
        this.tributes.add(tribute);
        return tribute;
    }

    public Optional<HungerGamesTribute> getTributeByMember(Member member) {
        for (HungerGamesTribute tribute : tributes)
            if (tribute.getId().equals(member.getUser().getId()))
                return Optional.of(tribute);
        return Optional.empty();
    }

    public void startGame() {
        //todo
    }

    public enum GameStatus {
        REAPING,
        LAUNCH,
        RUNNING,
        ENDED,
        ;
    }

}
