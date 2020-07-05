package com.georlegacy.general.vestrimu.commands.behaviour;

import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.objects.behaviour.GuildBehaviourRecord;
import com.georlegacy.general.vestrimu.core.objects.behaviour.MemberBehaviourRecord;
import com.georlegacy.general.vestrimu.core.objects.behaviour.Punishment;
import com.georlegacy.general.vestrimu.core.objects.config.GuildConfiguration;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import com.georlegacy.general.vestrimu.util.time.TimeFormatter;
import com.google.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BehaviourInfoCommand extends Command {

    @Inject
    private SQLManager sqlManager;

    public BehaviourInfoCommand() {
        super(new String[]{"punishments", "infractions"}, "Shows your punishments in this server.", "[usermention]|[userid]", CommandAccessType.USER_ANY, false);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        Message message = event.getMessage();
        GuildConfiguration configuration = sqlManager.readGuild(guild.getId());

        ArrayList<String> args = new ArrayList<String>(Arrays.asList(message.getContentRaw().split(" ")));
        args.remove(0);

        Member member;
        if (!args.isEmpty() && args.get(0).equals(!message.getMentionedMembers().isEmpty() ? message.getMentionedMembers().get(0).getAsMention().replaceFirst("@!", "@").replaceFirst("@", "@!") : null))
            member = message.getMentionedMembers().get(0);
        else
            try {
                member = event.getGuild().getMemberById(args.get(0));
            } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                member = event.getMember();
            }

        EmbedBuilder eb = new EmbedBuilder();
        eb
                .setTitle("Infractions for " + member.getEffectiveName() +
                        " (" + member.getUser().getName() + "#" +
                        member.getUser().getDiscriminator() + ") in " + guild.getName())
                .setColor(Constants.VESTRIMU_PURPLE)
                .setFooter("Vestrimu", Constants.ICON_URL);

        MemberBehaviourRecord record = new GuildBehaviourRecord(true).deserialize(
                new JSONObject(configuration.getGuild_behaviour_record())).getMemberRecords()
                .getOrDefault(member.getUser().getId(), new MemberBehaviourRecord(true));

        int totalInfractions = record.getPunishments().size();

        if (totalInfractions == 0)
            eb.setDescription(":white_check_mark: No infractions");
        else {
            List<Punishment> warnings = record.getPunishments().stream().filter(
                    p -> p.getType().equals(Punishment.PunishmentType.WARNING)).collect(Collectors.toList());
            if (warnings.size() != 0) {
                StringBuilder warningDetail = new StringBuilder();
                warnings.forEach(warning -> warningDetail.append("**" + warning.getIssuer().getName() + "** warned you on **" +
                        new SimpleDateFormat("dd/MM/yyyy").format(warning.getIssueDate()) + "** for **" +
                        warning.getReason() + "**" + "\n"));

                eb
                        .addField(":warning: " + String.valueOf(warnings.size()) + " Warnings ",
                                warningDetail.toString(), false);
            }
            List<Punishment> mutes = record.getPunishments().stream().filter(
                    p -> p.getType().equals(Punishment.PunishmentType.MUTE)).collect(Collectors.toList());
            if (mutes.size() != 0) {
                StringBuilder muteDetail = new StringBuilder();
                mutes.forEach(mute -> muteDetail.append("**" + mute.getIssuer().getName() + "** muted you on **" +
                        new SimpleDateFormat("dd/MM/yyyy").format(mute.getIssueDate()) + "** for **" +
                        mute.getReason() + "** for **" + TimeFormatter.millisToTime(mute.getDurationMillis()) + "** \n"));

                eb
                        .addField(":mute: " + String.valueOf(mutes.size()) + " Mutes",
                                muteDetail.toString(), false);
            }
            List<Punishment> kicks = record.getPunishments().stream().filter(
                    p -> p.getType().equals(Punishment.PunishmentType.KICK)).collect(Collectors.toList());
            if (kicks.size() != 0) {
                StringBuilder kickDetail = new StringBuilder();
                kicks.forEach(kick -> kickDetail.append("**" + kick.getIssuer().getName() + "** kicked you on **" +
                        new SimpleDateFormat("dd/MM/yyyy").format(kick.getIssueDate()) + "** for **" +
                        kick.getReason() + "**" + "\n"));

                eb
                        .addField("<:kick:474974493593305089> " + String.valueOf(kicks.size()) + " Kicks",
                                kickDetail.toString(), false);
            }
            List<Punishment> tempbans = record.getPunishments().stream().filter(
                    p -> p.getType().equals(Punishment.PunishmentType.TEMP_BAN)).collect(Collectors.toList());
            if (tempbans.size() != 0) {
                StringBuilder tempbanDetail = new StringBuilder();
                tempbans.forEach(tempban -> tempbanDetail.append("**" + tempban.getIssuer().getName() + "** banned you on **" +
                        new SimpleDateFormat("dd/MM/yyyy").format(tempban.getIssueDate()) + "** for **" +
                        tempban.getReason() + "** for **" + TimeFormatter.millisToTime(tempban.getDurationMillis()) + "** \n"));

                eb
                        .addField("<:hammerclock:474895878897795082> " + String.valueOf(mutes.size()) + " Temporary Bans",
                                tempbanDetail.toString(), false);
            }
            List<Punishment> bans = record.getPunishments().stream().filter(
                    p -> p.getType().equals(Punishment.PunishmentType.BAN)).collect(Collectors.toList());
            if (bans.size() != 0) {
                StringBuilder banDetail = new StringBuilder();
                bans.forEach(ban -> banDetail.append("**" + ban.getIssuer().getName() + "** banned you on **" +
                        new SimpleDateFormat("dd/MM/yyyy").format(ban.getIssueDate()) + "** for **" +
                        ban.getReason() + "**" + "\n"));

                eb
                        .addField("<:hammer:474892530153029632> " + String.valueOf(mutes.size()) + " Bans",
                                banDetail.toString(), false);
            }
        }

        channel.sendMessage(eb.build()).queue();
    }

}
