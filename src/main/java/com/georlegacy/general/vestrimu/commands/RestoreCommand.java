package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.managers.WebhookManager;
import com.georlegacy.general.vestrimu.core.objects.config.GuildConfiguration;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RestoreCommand extends Command {

    @Inject private SQLManager sqlManager;
    @Inject private WebhookManager webhookManager;

    public RestoreCommand() {
        super(new String[]{"restore", "fixbot", "botfix"}, "Checks the bot's core functionality.", "", CommandAccessType.SERVER_ADMIN, true);
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        GuildConfiguration configuration = sqlManager.readGuild(guild.getId());

        EmbedBuilder initial = new EmbedBuilder();
        initial
                .setDescription(":hourglass_flowing_sand: **Initializing...**")
                .setColor(Constants.VESTRIMU_PURPLE)
                .setTitle("**Bot Fix**");

        boolean hasPermissions;
        EmbedBuilder permissionCheck = new EmbedBuilder();
        permissionCheck
                .setTitle("**Bot Fix**")
                .setColor(Constants.VESTRIMU_PURPLE)
                .setDescription("**<a:loading:468689847935303682> Checking permissions**");
        hasPermissions = guild.getMemberById(Constants.VESTRIMU_ID).hasPermission(Permission.ADMINISTRATOR);

        boolean roleExists;
        EmbedBuilder roleCheck = new EmbedBuilder();
        roleCheck
                .setTitle("**Bot Fix**")
                .setColor(Constants.VESTRIMU_PURPLE)
                .setDescription("**<a:loading:468689847935303682> Checking access role**");
        roleExists = guild.getRoleById(configuration.getBotaccessroleid()) != null;

        boolean modRoleExists;
        EmbedBuilder modRoleCheck = new EmbedBuilder();
        modRoleCheck
                .setTitle("**Bot Fix**")
                .setColor(Constants.VESTRIMU_PURPLE)
                .setDescription("**<a:loading:468689847935303682> Checking moderator role**");
        modRoleExists = guild.getRoleById(configuration.getBotmodroleid()) != null;

        AtomicBoolean webhookExists = new AtomicBoolean();
        EmbedBuilder webhookCheck = new EmbedBuilder();
        webhookCheck
                .setTitle("**Bot Fix**")
                .setColor(Constants.VESTRIMU_PURPLE)
                .setDescription("<a:loading:468689847935303682> **Checking primary webhook**");
        guild.retrieveWebhooks().queue(webhooks -> {
            for (Webhook webhook : webhooks) {
                if (webhook.getId().equals(configuration.getPrimarywebhookid())) {
                    webhookExists.set(true);
                }
            }
            EmbedBuilder summary = new EmbedBuilder();
            summary
                    .setTitle("**Bot Fix**")
                    .setColor(hasPermissions && roleExists && webhookExists.get() ? Color.GREEN : !hasPermissions && !roleExists && !webhookExists.get() ? Color.RED : Color.ORANGE)
                    .setDescription(hasPermissions && roleExists && webhookExists.get() ? "**Analysis complete with no issues**" : "**Analysis complete with issues**")
                    .addField("Permissions", (hasPermissions ? ":white_check_mark: " : ":x: ") + (hasPermissions ? "The bot has the correct permissions to run normally" : "The bot does not have the correct permissions, try rerunning this command after giving the bot administrator privileges"), true)
                    .addField("Access Role", (roleExists ? ":white_check_mark: " : ":x: ") + (roleExists ? "The bot's access role is intact as expected" : "The bot's access role has been deleted and potentially unsuccessfully recreated, this will be fixed shortly"), true)
                    .addField("Moderator Role", (modRoleExists ? ":white_check_mark: " : ":x: ") + (modRoleExists ? "The bot's moderator role is intact as expected" : "The bot's moderator role has been deleted and potentially unsuccessfully recreated, this will be fixed shortly"), true)
                    .addField("Primary Webhook", (webhookExists.get() ? ":white_check_mark: " : ":x: ") + (webhookExists.get() ? "The bot's primary webhook is intact as expected" : "The bot's primary webhook has been deleted and potentially unsuccessfully recreated, this will be fixed shortly"), true);

            EmbedBuilder finish = new EmbedBuilder();
            if (hasPermissions) {
                finish
                        .setTitle("**Bot Fix**")
                        .setColor(Constants.VESTRIMU_PURPLE)
                        .setDescription("Issues found in analysis were fixed");

                finish.addField("Permissions", ":white_check_mark: The bot had the correct permissions to fix issues", true);
                if (!roleExists) {
                    finish.addField("Access Role", ":white_check_mark: The bot's access role has successfully been repaired", true);
                    guild.createRole()
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setName("Vestrimu Access")
                            .queue(role -> {
                                sqlManager.updateGuild(configuration.setBotaccessroleid(role.getId()));
                            });
                }
                if (!modRoleExists) {
                    finish.addField("Moderator Role", ":white_check_mark: The bot's moderator role has successfully been repaired", true);
                    guild.createRole()
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setName("Vestrimu Moderator")
                            .queue(role -> {
                                sqlManager.updateGuild(configuration.setBotmodroleid(role.getId()));
                            });
                }
                if (!webhookExists.get()) {
                    finish.addField("Primary Webhook", ":white_check_mark: The bot's primary webhook has successfully been repaired", true);
                    webhookManager.loadWebhook(guild);
                }
            }

            channel.sendMessage(initial.build()).queue(message1 -> {
                message1.editMessage(permissionCheck.build()).queueAfter(1, TimeUnit.SECONDS);
                message1.editMessage(roleCheck.build()).queueAfter(2, TimeUnit.SECONDS);
                message1.editMessage(modRoleCheck.build()).queueAfter(3, TimeUnit.SECONDS);
                message1.editMessage(webhookCheck.build()).queueAfter(4, TimeUnit.SECONDS);
                message1.editMessage(summary.build()).queueAfter(5, TimeUnit.SECONDS);
                if (!hasPermissions || !roleExists || !webhookExists.get() || !modRoleExists) {
                    message1.editMessage(finish.build()).queueAfter(6, TimeUnit.SECONDS);
                }
            });
        });

    }

}
