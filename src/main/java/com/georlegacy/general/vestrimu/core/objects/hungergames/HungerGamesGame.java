package com.georlegacy.general.vestrimu.core.objects.hungergames;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.util.Constants;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.react.GenericPrivateMessageReactionEvent;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class HungerGamesGame {

    @Getter
    private final TextChannel channel;

    @Getter
    private final Set<HungerGamesTribute> tributes;

    @Getter
    private final ArenaType arenaType;

    @Getter
    @Setter
    private GameStatus status;

    private int maxDistrict = 1;
    private boolean maxDistrictFull;

    public HungerGamesGame(TextChannel channel) {
        this.channel = channel;
        tributes = new HashSet<>();
        status = GameStatus.REAPING;
        arenaType = ArenaType.values()[(int) Math.floor(Math.random() * ArenaType.values().length)];
    }

    public HungerGamesTribute addTribute(Member member) {
        if (maxDistrictFull) {
            maxDistrict++;
            maxDistrictFull = false;
        } else
            maxDistrictFull = true;
        HungerGamesTribute tribute = new HungerGamesTribute(member, maxDistrict, Location.CORNUCOPIA);
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
        setStatus(GameStatus.RUNNING);

        String url = Vestrimu.getInstance().getUnsplashManager().getRandomThumbFromQuery(arenaType.name());

        AtomicReference<Set<HungerGamesAction>> actions = new AtomicReference<>(new HashSet<>());

        for (HungerGamesTribute tribute : getTributes()) {
            User user = getChannel().getGuild().getMemberById(tribute.getId()).getUser();
            user.openPrivateChannel().queue((pc) -> {
                EmbedBuilder eb = new EmbedBuilder();
                eb
                        .setColor(Constants.VESTRIMU_PURPLE)
                        .setTitle("Decision")
                        .setImage(url)
                        .setDescription("You emerge in a " + arenaType.name().toLowerCase() + ".\n" +
                                "You currently stand on your plate, the surrounding landmines deactivated.\n" +
                                "React to this message in order to choose an option from below.")
                        .addField(":fist:", "Attempt to take down a nearby opponent with your bare hands", true)
                        .addField(":crossed_swords:", "Approach the Cornucopia to acquire supplies", true)
                        .addField(":person_running:", "Run from the imminent bloodbath as tributes fight over supplies", true)
                        .setFooter("Vestrimu", Constants.ICON_URL);
                pc.sendMessage(eb.build()).queue(m -> {
                    m.addReaction("\u270A").queue();
                    m.addReaction("\u2694").queue();
                    m.addReaction("\uD83C\uDFC3").queue();
                    Vestrimu.getInstance().getEventWaiter().waitForEvent(GenericPrivateMessageReactionEvent.class,
                            genericPrivateMessageReactionEvent -> genericPrivateMessageReactionEvent.getUser().getId().equals(user.getId()) &&
                                    (
                                            genericPrivateMessageReactionEvent.getReactionEmote().getName().equals("\u270A") ||
                                                    genericPrivateMessageReactionEvent.getReactionEmote().getName().equals("\u2694") ||
                                                    genericPrivateMessageReactionEvent.getReactionEmote().getName().equals("\uD83C\uDFC3")
                                    ) &&
                                    genericPrivateMessageReactionEvent.getMessageId().equals(m.getId()),
                            genericPrivateMessageReactionEvent -> {
                                if (genericPrivateMessageReactionEvent.getReactionEmote().getName().equals("\u270A"))
                                    actions.get().add(new HungerGamesAction(tribute, HungerGamesAction.ActionType.BARE_ATTACK, Location.INNER));
                                if (genericPrivateMessageReactionEvent.getReactionEmote().getName().equals("\u2694"))
                                    actions.get().add(new HungerGamesAction(tribute, HungerGamesAction.ActionType.APPROACH_CORNUCOPIA, Location.INNER));
                                if (genericPrivateMessageReactionEvent.getReactionEmote().getName().equals("\uD83C\uDFC3"))
                                    actions.get().add(new HungerGamesAction(tribute, HungerGamesAction.ActionType.RUN, Location.INNER));

                                if (actions.get().size() == getTributes().size()) {
                                    processResults(HungerGamesAction.compute(actions.get()));
                                    System.out.println("Computed...");
                                }
                            },
                            (int) (30 + Math.floor(Math.random() * 30)), TimeUnit.SECONDS, () -> {
                                EmbedBuilder timeout = new EmbedBuilder();
                                timeout
                                        .setColor(Constants.VESTRIMU_PURPLE)
                                        .setTitle("Sorry")
                                        .setDescription("You didn't decide fast enough.\nYou can only hope your fellow tributes don't see you...")
                                        .setFooter("Vestrimu", Constants.ICON_URL);
                                pc.sendMessage(timeout.build()).queue();
                                actions.get().add(new HungerGamesAction(tribute, HungerGamesAction.ActionType.NONE, Location.INNER));

                                if (actions.get().size() == getTributes().size()) {
                                    processResults(HungerGamesAction.compute(actions.get()));
                                    System.out.println("Computed...");
                                }
                            });
                });
            });
        }
    }

    private void processResults(Set<HungerGamesActionResult> results) {
        //todo temp, this only sends one basic response to the user in a pm at this point in time
        for (HungerGamesActionResult result : results) {
            Objects.requireNonNull(Vestrimu.getInstance().getShardManager().getUserById(result.getTribute().getId())).openPrivateChannel().queue(channel -> {
                channel.sendMessage("Result:").queue();
                channel.sendMessage(result.getType().name()).queue();
                channel.sendMessage("Alive:").queue();
                channel.sendMessage(String.valueOf(result.getTribute().isAlive())).queue();
            });
        }
    }

    private boolean progressGame() {

        return false;
    }

    public enum GameStatus {
        REAPING,
        LAUNCH,
        RUNNING,
        ENDED,
        ;
    }

    public enum ArenaType {
        DESERT,
        RAINFOREST,
        FOREST,
        OCEAN,
        ;
    }

    public enum Location {
        CORNUCOPIA,
        INNER,
        MID_EAST,
        MID_WEST,
        MID_NORTH,
        MID_SOUTH,
        EDGE_EAST,
        EDGE_WEST,
        EDGE_NORTH,
        EDGE_SOUTH,
        ;
    }

}
