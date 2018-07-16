package com.georlegacy.general.vestrimu.core.managers;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.dv8tion.jda.core.entities.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Singleton
public class WebhookManager {

    @Inject private SQLManager sqlManager;

    public void loadWebhook(Guild guild) {
        if (sqlManager.isWaiting(guild) != null)
            return;
        if (!sqlManager.readGuild(guild.getId()).isAdmin_mode())
            return;
        List<Webhook> webhooks = getOwnWebhooks(guild);
        if (webhooks.isEmpty()) {
            addWebhook(guild);
            return;
        }
        if (webhooks.size() > 1) {
            deleteWebhooks(webhooks);
            addWebhook(guild);
            return;
        }
    }

    public void loadWebhooks() {
        for (Guild guild : Vestrimu.getInstance().getJda().getGuilds()) {
            loadWebhook(guild);
        }
    }

    public void sendWebhookMessage(MessageChannel channel, String name, String avatarUrl, String message) {

    }

    public void sendWebhookMessage(MessageChannel channel, String name, String avatarUrl, MessageEmbed... embeds) {

    }

    public void sendWebhookMessage(MessageChannel channel, String name, String avatarUrl, String message, MessageEmbed... embeds) {

    }

    private List<Webhook> getOwnWebhooks(Guild guild) {
        List<Webhook> toReturn = new ArrayList<Webhook>();
        guild.getWebhooks().queue(webhooks -> {
            for (Webhook w : webhooks) {
                if (w.getOwner().getUser().getId().equals(Vestrimu.getInstance().getJda().getSelfUser().getId())) {
                    toReturn.add(w);
                }
            }
        });
        return toReturn;
    }

    private void addWebhook(Guild guild) {
        try {
            guild.getDefaultChannel().createWebhook("Vestrimu Primary Webhook").setAvatar(Icon.from(Vestrimu.getInstance().getClass().getClassLoader().getResourceAsStream("icon.png"))).queue(hook -> {
                sqlManager.updateGuild(sqlManager.readGuild(guild.getId()).setPrimarywebhookid(hook.getId()));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteWebhooks(List<Webhook> webhooks) {
        for (Webhook w : webhooks) {
            w.delete().queue();
        }
    }

}
