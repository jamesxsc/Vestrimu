package com.georlegacy.general.vestrimu.listeners;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.managers.WebhookManager;
import com.georlegacy.general.vestrimu.core.objects.GuildConfiguration;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JoinNewGuildListener extends ListenerAdapter {

    @Inject private SQLManager sqlManager;
    @Inject private WebhookManager webhookManager;

    private static Map<String /* Guild ID */, String /* Message ID */> idsInWaiting;

    public static Map<String, String> getIdsInWaiting() {
        return idsInWaiting;
    }

    public JoinNewGuildListener() {
        idsInWaiting = new HashMap<String, String>();
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                if (!guild.getMemberById(Constants.VESTRIMU_ID).hasPermission(Permission.ADMINISTRATOR)) {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb
                            .setTitle("Sorry!")
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setDescription("**I didn't have the required permissions to run normally in your server. You have two options:**")
                            .addField(":one:", "Allow me leave your server and you add me again [**here**](https://www.615283.net \"Invite link with correct permissions\") to resolve permission issues", true)
                            .addField(":two:", "Run me in non-administrator mode", true)
                            .setFooter("Vestrimu", Constants.ICON_URL);
                    guild.getOwner().getUser().openPrivateChannel().queue(dm -> dm.sendMessage(eb.build()).queue(message -> {
                        message.addReaction("\u0031\u20E3").queue();
                        message.addReaction("\u0032\u20E3").queue();
                        idsInWaiting.put(guild.getId(), message.getId());
                    }));
                    sqlManager.setWaiting(guild);
                    return;
                }
                if (sqlManager.containsGuild(guild.getId())) {
                    GuildConfiguration configuration = sqlManager.readGuild(guild.getId());
                    webhookManager.loadWebhook(guild);
                    EmbedBuilder eb = new EmbedBuilder();
                    eb
                            .setTitle("Hello")
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setFooter("Vestrimu", Constants.ICON_URL)
                            .setDescription("I'm **Vestrimu**. *I've been here before.*")
                            .addField("Help", "If you need help getting started, ping me in any channel.", true)
                            .addField("My Creator", "I was built by 615283.", true);
                    guild.getDefaultChannel().sendMessage(eb.build()).queue();
                    if (guild.getRoleById(configuration.getBotaccessroleid()) == null) {
                        guild.getController().createRole()
                                .setColor(Constants.VESTRIMU_PURPLE)
                                .setName("Vestrimu Access")
                                .queue(role ->
                                        configuration.setBotaccessroleid(role.getId())
                                );
                    sqlManager.updateGuild(configuration);
                        Vestrimu.getInstance().getGuildConfigs().put(guild.getId(), configuration);
                    }
                    return;
                }
                webhookManager.loadWebhook(guild);
                EmbedBuilder eb = new EmbedBuilder();
                eb
                        .setTitle("Hello")
                        .setColor(Constants.VESTRIMU_PURPLE)
                        .setFooter("Vestrimu", Constants.ICON_URL)
                        .setDescription("I'm **Vestrimu**, your new server administration bot!")
                        .addField("Help", "If you need help getting started, ping me in any channel.", true)
                        .addField("My Creator", "I was built by 615283.", true);
                guild.getDefaultChannel().sendMessage(eb.build()).queue();
                guild.getController().createRole()
                        .setColor(Constants.VESTRIMU_PURPLE)
                        .setName("Vestrimu Access")
                        .queue(role -> {
                            GuildConfiguration configuration = new GuildConfiguration(
                                    guild.getId(),
                                    role.getId(),
                                    "-",
                                    false
                            );
                            sqlManager.writeGuild(configuration);
                            Vestrimu.getInstance().getGuildConfigs().put(guild.getId(), configuration);
                        });

            }
        }, 3, TimeUnit.SECONDS);
    }

}
