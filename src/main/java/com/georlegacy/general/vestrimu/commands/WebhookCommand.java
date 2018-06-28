package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.WebhookManager;
import com.google.inject.Inject;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebhookCommand extends Command {
    @Inject private WebhookManager webhookManager;

    public WebhookCommand() {
        super("webhook", "Sends webhooks to the server", "<channel> <json>", false);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        ArrayList<String> args = new ArrayList<String>(Arrays.asList(message.getContentRaw().replaceFirst("-" + "webhook", "").trim().split(" ")));

        List<TextChannel> mentioned  = message.getMentionedChannels();
        System.out.println(mentioned);
        if (mentioned.size() == 0) {
            channel.sendMessage("you actually need a channel tho").queue();
            return;
        }
        channel.sendMessage("eh thx 4 teh chanel").queue();
        webhookManager.loadWebhook(event.getGuild());

    }

}
