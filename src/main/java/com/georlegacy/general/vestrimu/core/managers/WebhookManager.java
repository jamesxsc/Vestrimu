package com.georlegacy.general.vestrimu.core.managers;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.listeners.JoinNewGuildListener;
import com.google.inject.Singleton;
import net.dv8tion.jda.core.entities.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Singleton
public class WebhookManager {

    private HashMap<String, Webhook> webhooks;

    public HashMap<String, Webhook> getWebhooks() {
        return webhooks;
    }

    public WebhookManager() {
        this.webhooks = new HashMap<String, Webhook>();
    }

    public void loadWebhook(Guild guild) {
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
        this.webhooks.put(guild.getId(), webhooks.get(0));
        return;
    }

    public void loadWebhooks() {
        for (Guild guild : Vestrimu.getInstance().getJda().getGuilds()) {
            if (JoinNewGuildListener.getIdsInWaiting().containsKey(guild.getId()))
                continue;
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
            this.webhooks.put(guild.getId(), webhooks.get(0));
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
                this.webhooks.put(guild.getId(), hook);
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
