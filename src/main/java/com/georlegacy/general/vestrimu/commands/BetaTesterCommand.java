package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;

import java.util.Objects;

public class BetaTesterCommand extends Command {

    @Inject
    private SQLManager sqlManager;

    public BetaTesterCommand() {
        super(new String[]{"betatester", "betatest", "eap"}, "Register to become a beta tester for the latest Vestrimu features", "", CommandAccessType.USER_ANY, false);
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        long id = Objects.requireNonNull(event.getMember()).getIdLong();

        if (sqlManager.getBetaTesters().contains(id)) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Sorry")
                    .setDescription("You are already a beta tester")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            event.getChannel().sendMessage(eb.build()).queue();
        } else {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Success")
                    .setDescription("You're interest has been registered. You will be notified if you become a beta tester or not soon")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            event.getChannel().sendMessage(eb.build()).queue();

            String candidateName = event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator();

            EmbedBuilder adminEb = new EmbedBuilder();
            adminEb
                    .setTitle("New Beta Tester Candidate")
                    .setThumbnail(event.getMember().getUser().getAvatarUrl())
                    .setDescription("`" + candidateName + "` has registered to become a beta tester. React below to respond")
                    .addField(":white_check_mark:", "Accept `" + candidateName + "` as a beta tester", true)
                    .addField(":x:", "Reject `" + candidateName + "` as a beta tester", true)
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            Objects.requireNonNull(event.getJDA().getTextChannelById("417812820940292106")).sendMessage(adminEb.build()).queue(m -> {
                m.addReaction("\u2705").queue(); // :white_check_mark:
                m.addReaction("\u274C").queue(); // :x:

                Vestrimu.getInstance().getEventWaiter().waitForEvent(GenericGuildMessageReactionEvent.class, eventToCheck ->
                        Constants.ADMIN_IDS.contains(eventToCheck.getMember().getUser().getId()) &&
                                eventToCheck.getMessageId().equals(m.getId()) &&
                                (
                                        eventToCheck.getReactionEmote().getEmoji().equals("\u2705") ||
                                                eventToCheck.getReactionEmote().getEmoji().equals("\u274C")
                                ), (e) -> {
                    EmbedBuilder resultEb = new EmbedBuilder();
                    EmbedBuilder testerEb = new EmbedBuilder();
                    if (e.getReactionEmote().getEmoji().equals("\u2705")) {
                        sqlManager.addBetaTester(event.getMember().getIdLong());
                        testerEb
                                .setTitle("Success")
                                .setDescription("You have been accepted as a beta tester")
                                .setColor(Constants.VESTRIMU_PURPLE)
                                .setFooter("Vestrimu", Constants.ICON_URL);
                        resultEb
                                .setTitle("Success")
                                .setDescription("`" + event.getMember().getUser().getAsMention() + "` has been accepted as a beta tester")
                                .setColor(Constants.VESTRIMU_PURPLE)
                                .setFooter("Vestrimu", Constants.ICON_URL);
                    } else {
                        testerEb
                                .setTitle("Sorry")
                                .setDescription("You weren't accepted as a beta tester")
                                .setColor(Constants.VESTRIMU_PURPLE)
                                .setFooter("Vestrimu", Constants.ICON_URL);
                        resultEb
                                .setTitle("Success")
                                .setDescription("`" + event.getMember().getUser().getAsMention() + "` has been rejected as a beta tester")
                                .setColor(Constants.VESTRIMU_PURPLE)
                                .setFooter("Vestrimu", Constants.ICON_URL);
                    }
                    event.getMember().getUser().openPrivateChannel().queue((chan) -> chan.sendMessage(testerEb.build()).queue());
                    Objects.requireNonNull(event.getJDA().getTextChannelById("417812820940292106")).sendMessage(resultEb.build()).queue();
                });
            });
        }
    }

}
