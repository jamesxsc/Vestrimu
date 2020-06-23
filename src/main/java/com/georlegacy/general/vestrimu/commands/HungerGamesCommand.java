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
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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
                            .setDescription(member.getAsMention() + ", you have been reaped to participate in the Hunger Games!")
                            .addField("Your District", String.valueOf(tribute.getDistrict()), false)
                            .setFooter("Vestrimu", Constants.ICON_URL);
                }
            }
            if (game.getTributes().size() > 6) {
                // todo start game/timer
            }
        } else {
            HungerGamesGame game = new HungerGamesGame(channel);
            manager.getGames().put(guild.getId(), game);
            eb
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setTitle("Success")
                    .setDescription("The Hunger Games has begun in this channel, use `" + configuration.getPrefix() + "hungergames` to join the games!")
                    .setFooter("Vestrimu", Constants.ICON_URL);
        }
        channel.sendMessage(eb.build()).queue();
    }

}
