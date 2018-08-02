package com.georlegacy.general.vestrimu.listeners;

import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.objects.behaviour.GuildBehaviourRecord;
import com.georlegacy.general.vestrimu.core.objects.config.GuildConfiguration;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;


public class BotModeReactionSelectionListener extends ListenerAdapter {

    @Inject private SQLManager sqlManager;

    @Override
    public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event) {
        String guildId = sqlManager.isWaiting(event.getMessageId());
        if (guildId == null)
            return;
        if (event.getUser().getId().equals(Constants.VESTRIMU_ID))
            return;
        if (event.getReactionEmote().getName().equals("\u0031\u20E3")) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setDescription("Okay, I'll leave your server and you can re-add me with the URL sent in my last message.")
                    .setFooter("Vestrimu", Constants.ICON_URL)
                    .build()
            ).queue();
            sqlManager.setNotWaiting(event.getJDA().getGuildById(guildId));
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
            sqlManager.setNotWaiting(event.getJDA().getGuildById(guildId));
            sqlManager.writeGuild(new GuildConfiguration(
                    guildId,
                    "N/A",
                    "N/A",
                    "-",
                    false,
                    false,
                    new GuildBehaviourRecord()
            ));
        }
    }
}
