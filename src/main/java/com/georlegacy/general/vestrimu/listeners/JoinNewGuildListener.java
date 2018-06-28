package com.georlegacy.general.vestrimu.listeners;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.objects.GuildConfiguration;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JoinNewGuildListener extends ListenerAdapter {
    @Inject private SQLManager sqlManager;

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                if (!guild.getMemberById(Constants.VESTRIMU_ID).hasPermission(Permission.ADMINISTRATOR)) {
                    PrivateChannel dm = guild.getOwner().getUser().openPrivateChannel().complete();
                    EmbedBuilder eb = new EmbedBuilder();
                    eb
                            .setTitle("Sorry!")
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setDescription("I didn't have the required permissions to run correctly in your server. To run me correctly, please click [**here**](https://www.615283.net \"Server invite with permissions\").")
                            .setFooter("Vestrimu", "https://i.615283.net/u/29e043.png");
                    dm.sendMessage(eb.build()).queue();
                    event.getGuild().leave().queue();
                    return;
                }
                System.out.println(guild.getId());
                if (sqlManager.containsGuild(guild.getId())) {
                    System.out.println("contains guild");
                    EmbedBuilder eb = new EmbedBuilder();
                    eb
                            .setTitle("Hello")
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setFooter("Vestrimu", "https://i.615283.net/u/29e043.png")
                            .setDescription("Hello, I'm **Vestrimu**. *I've been here before.*")
                            .addField("Help", "If you need help getting started, ping me in any channel.", true)
                            .addField("My Creator", "I was built by 615283.", true);
                    guild.getDefaultChannel().sendMessage(eb.build()).queue();
                    return;
                }
                System.out.println("not contained");
                EmbedBuilder eb = new EmbedBuilder();
                eb
                        .setTitle("Hello")
                        .setColor(Constants.VESTRIMU_PURPLE)
                        .setFooter("Vestrimu", "https://i.615283.net/u/29e043.png")
                        .setDescription("Hello, I'm **Vestrimu**, your new server administration bot!")
                        .addField("Help", "If you need help getting started, ping me in any channel.", true)
                        .addField("My Creator", "I was built by 615283.", true);
                guild.getDefaultChannel().sendMessage(eb.build()).queue();
                Role accessRole = guild.getController().createRole()
                        .setColor(Constants.VESTRIMU_PURPLE)
                        .setHoisted(false)
                        .setMentionable(false)
                        .setName("Vestrimu Access")
                        .complete();
                sqlManager.writeGuild(new GuildConfiguration(
                        guild.getId(),
                        accessRole.getId()
                ));
            }
        }, 3, TimeUnit.SECONDS);
    }

}
