package com.georlegacy.general.vestrimu.core.managers;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.objects.config.GuildConfiguration;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Webhook;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Singleton
public class WebhookManager {

    @Inject private SQLManager sqlManager;

    public void loadWebhook(Guild guild) {
        if (sqlManager.isWaiting(guild) != null)
            return;
        GuildConfiguration configuration = sqlManager.readGuild(guild.getId());
        if (!configuration.isAdmin_mode())
            return;

        guild.retrieveWebhooks().queue(webhooks -> {
            boolean b = true;
            for (Webhook w : webhooks) {
                if (w.getId().equals(configuration.getPrimarywebhookid())) {
                    b = false;
                }
            }
            if (b)
                addWebhook(guild);
        });
    }

    public void loadWebhooks() {
        for (Guild guild : Vestrimu.getInstance().getShardManager().getGuilds()) {
            loadWebhook(guild);
        }
    }

    private void addWebhook(Guild guild) {
        try {
            guild.getDefaultChannel().createWebhook("Vestrimu Primary Webhook").setAvatar(Icon.from(Vestrimu.getInstance().getClass().getClassLoader().getResourceAsStream("icon.png"))).queueAfter(1, TimeUnit.SECONDS, hook -> {
                sqlManager.updateGuild(sqlManager.readGuild(guild.getId()).setPrimarywebhookid(hook.getId()));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
