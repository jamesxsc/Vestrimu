package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.HungerGamesManager;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.objects.config.GuildConfiguration;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.core.objects.hungergames.HungerGamesGame;
import com.georlegacy.general.vestrimu.core.objects.hungergames.HungerGamesTribute;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;

import java.util.concurrent.TimeUnit;

public class HungerGamesCommand extends Command {

    @Inject
    private SQLManager sqlManager;

    public HungerGamesCommand() {
        super(new String[]{"hungergames", "hg"}, "Play hunger games!", "", CommandAccessType.USER_ANY, false);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        HungerGamesManager manager = Vestrimu.getInstance().getHungerGamesManager();
        Guild guild = event.getGuild();
        TextChannel channel = event.getTextChannel();
        Member member = event.getMember();
        GuildConfiguration configuration = sqlManager.readGuild(event.getGuild().getId());

        EmbedBuilder eb = new EmbedBuilder();
        if (manager.getGames().containsKey(guild.getId())) {
            HungerGamesGame game = manager.getGames().get(guild.getId());
            TextChannel activeChannel = game.getChannel();
            if (!channel.getId().equals(activeChannel.getId())) {
                if (game.getStatus() == HungerGamesGame.GameStatus.REAPING) {
                    eb
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setTitle("Sorry")
                            .setDescription("A game is reaping in " + activeChannel.getAsMention() +
                                    "\nThere is still time to join!")
                            .setFooter("Vestrimu", Constants.ICON_URL);
                } else {
                    eb
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setTitle("Sorry")
                            .setDescription("A game is already running in " + activeChannel.getAsMention() +
                                    "\nPlease wait until it has finished before starting one here.")
                            .setFooter("Vestrimu", Constants.ICON_URL);
                }
            } else {
                if (game.getTributeByMember(member).isPresent()) {
                    HungerGamesTribute tribute = game.getTributeByMember(member).get();
                    eb
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setTitle("Sorry")
                            .setDescription(member.getAsMention() + ", you have already been entered into the Hunger Games!")
                            .addField("Your District", String.valueOf(tribute.getDistrict()), false)
                            .setFooter("Vestrimu", Constants.ICON_URL);
                } else {
                    HungerGamesTribute tribute = game.addTribute(member);
                    eb
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setTitle("Success")
                            .setDescription(member.getAsMention() + ", you have been reaped to participate in the Hunger Games!\nAs the games progress, you will have to make decisions through PMs with the bot.")
                            .addField("Your District", String.valueOf(tribute.getDistrict()), false)
                            .setFooter("Vestrimu", Constants.ICON_URL);
                }
            }
            if (game.getTributes().size() > 0) {
                channel.sendMessage(eb.build()).queue();
                EmbedBuilder startWarning = new EmbedBuilder();
                startWarning
                        .setColor(Constants.VESTRIMU_PURPLE)
                        .setTitle("Alert")
                        .setDescription("The Hunger Games will begin in 2 minutes or when a moderator reacts to this message with :white_check_mark:!")
                        .setFooter("Vestrimu", Constants.ICON_URL);
                channel.sendMessage(startWarning.build()).queue(m -> m.addReaction("\u2705").queue());
                Vestrimu.getInstance().getEventWaiter().waitForEvent(GenericGuildMessageReactionEvent.class,
                        (genericGuildMessageReactionEvent) ->
                                genericGuildMessageReactionEvent.getMember().getRoles().contains(guild.getRoleById(configuration.getBotmodroleid())) &&
                                        genericGuildMessageReactionEvent.getReaction().getReactionEmote().getName().equals("\u2705"),
                        (genericGuildMessageReactionEvent) -> startGame(game),
                        2, TimeUnit.MINUTES, () -> startGame(game));
            } else
                channel.sendMessage(eb.build()).queue();
        } else {
            HungerGamesGame game = new HungerGamesGame(channel);
            manager.getGames().put(guild.getId(), game);
            eb
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setTitle("Success")
                    .setDescription("The Hunger Games has begun in this channel, use `" + configuration.getPrefix() + "hungergames` to join the games!")
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
        }
    }

    private void startGame(HungerGamesGame game) {
        EmbedBuilder eb = new EmbedBuilder();
        eb
                .setColor(Constants.VESTRIMU_PURPLE)
                .setTitle("Alert")
                .setDescription("The Hunger Games is now beginning!")
                .addField("Tributes", String.valueOf(game.getTributes().size()), false)
                .setFooter("Vestrimu", Constants.ICON_URL);
        game.getChannel().sendMessage(eb.build()).queue();

        game.setStatus(HungerGamesGame.GameStatus.LAUNCH);

        EmbedBuilder countdown = new EmbedBuilder();
        countdown
                .setColor(Constants.VESTRIMU_PURPLE)
                .setTitle("Alert")
                .setDescription("The Hunger Games will start in `5` seconds!")
                .setFooter("Vestrimu", Constants.ICON_URL);

        game.getChannel().sendMessage(countdown.build()).queueAfter(3, TimeUnit.SECONDS, (m) -> {
            for (int i = 4; i > 0; i--)
                m.editMessage(countdown.setDescription("The Hunger Games will start in `" + i + "` seconds!").build()).queueAfter(5 - i, TimeUnit.SECONDS);
            m.editMessage(countdown.setDescription("The Hunger Games will start in `0` seconds!").build()).queueAfter(5, TimeUnit.SECONDS, (m1) -> game.startGame());
        });
    }

}
