package com.georlegacy.general.vestrimu.listeners;

import com.georlegacy.general.vestrimu.util.Constants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Map;

public class BotModeReactionSelectionListener extends ListenerAdapter {

    @Override
    public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event) {
        boolean contained = false;
        String guildId = "";
        for (Map.Entry<String, String> entry : JoinNewGuildListener.getIdsInWaiting().entrySet()) {
            if (entry.getValue().equals(event.getMessageId())) {
                guildId = entry.getKey();
                contained = true;
            }
        }
        if (!contained) return;
        if (event.getUser().getId().equals(Constants.VESTRIMU_ID)) return;

        if (event.getReactionEmote().getName().equals("\u0031\u20E3")) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setDescription("Okay, I'll leave your server and you can re-add me with the URL sent in my last message.")
                    .setFooter("Vestrimu", Constants.ICON_URL)
                    .build()
            ).queue();
            event.getJDA().getGuildById(guildId).leave().queue();
            return;
        }

        if (event.getReactionEmote().getName().equals("\u0032\u20E3")) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setDescription("Okay, I'll stay in your server and operate in non-administrator mode.")
                    .setFooter("Vestrimu", Constants.ICON_URL)
                    .build()
            ).queue();
            return;
        }
    }
}
