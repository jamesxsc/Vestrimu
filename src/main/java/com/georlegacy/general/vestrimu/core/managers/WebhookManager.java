package com.georlegacy.general.vestrimu.core.managers;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.google.inject.Inject;
import net.dv8tion.jda.core.entities.*;

import java.util.ArrayList;
import java.util.List;

public class WebhookManager {
    @Inject private Vestrimu vestrimu;

    private Webhook webhook;

    public WebhookManager() {
        for (Guild guild : vestrimu.getJDA().getGuilds()) {
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
            this.webhook = webhooks.get(0);
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
        List<Webhook> webhooks = guild.getWebhooks().complete();
        for (Webhook w : webhooks) {
            if (w.getOwner().getUser().getId().equals(vestrimu.getJDA().getSelfUser().getId())) {
                toReturn.add(w);
                System.out.println("we are the owner");
            }
            System.out.println("we dont own it");
        }
        return toReturn;
    }

    private void addWebhook(Guild guild) {

    }

    private void deleteWebhooks(List<Webhook> webhooks) {
        for (Webhook w : webhooks) {
            w.delete().queue();
        }
    }

}
