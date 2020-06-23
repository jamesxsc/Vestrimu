package com.georlegacy.general.vestrimu.commands.behaviour;

import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.objects.behaviour.GuildBehaviourRecord;
import com.georlegacy.general.vestrimu.core.objects.behaviour.MemberBehaviourRecord;
import com.georlegacy.general.vestrimu.core.objects.behaviour.Punishment;
import com.georlegacy.general.vestrimu.core.objects.config.GuildConfiguration;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class KickCommand extends Command {

    @Inject
    private SQLManager sqlManager;

    public KickCommand() {
        super(new String[]{"kick", "boot"}, "Kicks a user", "<user> <reason>", CommandAccessType.SERVER_MOD, false);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        Message message = event.getMessage();

        ArrayList<String> args = new ArrayList<String>(Arrays.asList(message.getContentRaw().split(" ")));
        args.remove(0);

        Member member;

        if (!args.isEmpty() ? args.get(0).equals(!message.getMentionedMembers().isEmpty() ? message.getMentionedMembers().get(0).getAsMention() : null) : false)
            member = message.getMentionedMembers().get(0);
        else {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Sorry")
                    .setDescription("You need to mention a valid user to kick.")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        args.remove(0);

        if (args.isEmpty()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Sorry")
                    .setDescription("You need to provide a reason for the kick.")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        String reason = String.join(" ", args);

        GuildConfiguration configuration = sqlManager.readGuild(event.getGuild().getId());
        GuildBehaviourRecord guildRecord = new GuildBehaviourRecord(true).deserialize(
                new JSONObject(configuration.getGuild_behaviour_record()));
        MemberBehaviourRecord memberRecord = guildRecord.getMemberRecords().getOrDefault(member.getUser().getId(), new MemberBehaviourRecord(true));
        memberRecord.addPunishment(new Punishment(Punishment.PunishmentType.KICK,
                reason, 0, event.getAuthor()));
        guildRecord.getMemberRecords().put(member.getUser().getId(), memberRecord);
        event.getGuild().getController().kick(member, reason).queue((v) -> {
            sqlManager.updateGuild(configuration.setGuild_behaviour_record(guildRecord.serialize().toString()));
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Success")
                    .setDescription("You have successfully kicked " + member.getAsMention() + " for **" + reason + "**")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
        });
    }

}
