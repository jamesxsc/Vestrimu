package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.managers.WebhookManager;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WebhookCommand extends Command {

    @Inject private WebhookManager webhookManager;
    @Inject private SQLManager sqlManager;

    public WebhookCommand() {
        super(new String[]{"wh", "webhook", "sendwebhook"}, "Sends webhooks to the server.", "<channel>", CommandAccessType.SERVER_ADMIN, true);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        ArrayList<String> args = new ArrayList<String>(Arrays.asList(message.getContentRaw().split(" ")));
        args.remove(0);

        EmbedBuilder argumentsHelp = new EmbedBuilder();
        argumentsHelp
                .setTitle("Arguments")
                .setDescription("Below are all arguments for the webhook command, each of which begins with `--` and their values are supplied as like below after the mentioned channel to send to ```--plainMessage:This is the plain text before the embed```*Note: Do not use `--` within the value for any arguments*")
                .setColor(Constants.VESTRIMU_PURPLE)
                .setFooter("Vestrimu", Constants.ICON_URL)
                .addField("--avatarUrl", "The URL for the avatar of the webhook when the message is sent", true)
                .addField("--webhookName", "The name of the webhook when the message is sent", true)
                .addField("--plainMessage", "The plain message sent by the webhook before any embed content", true)
                .addField("--embedTitle", "The title of the (optional) embedded message following the plain message", true)
                .addField("--embedDescription", "The description of the (optional) embedded message following the plain message", true)
                .addField("--embedFooterText", "The text in the footer for the (optional) embedded message following the plain message", true)
                .addField("--embedFooterIconUrl", "The URL of the icon for the footer of the (optional) embedded message following the plain message", true)
                .addField("--embedThumbnailUrl", "The URL of the thumbnail of the (optional) embedded message following the plain message", true)
                .addField("--embedImageUrl", "The URL of the image of the (optional) embedded message following the plain message", true)
                .addField("--embedAuthorName", "The name of the author displayed on the (optional) embedded message following the plain message", true)
                .addField("--fieldsInline", "A boolean value denoting whether all fields in the (optional) embedded message following the plain message will be inline", true)
                .addField("--fieldName", "The name of a field in the (optional) embedded message following the plain message, must be followed with a --fieldValue, both can be used several times", true)
                .addField("--fieldValue", "The value of a field in the (optional) embedded message following the plain message, must follow a --fieldName", true);


        if (args.size() == 0) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Sorry")
                    .setDescription("You need to provide a channel and arguments, for information on arguments, follow this command with `arguments`")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        if (args.get(0).equalsIgnoreCase("arguments") || args.get(0).equalsIgnoreCase("args")) {
            channel.sendMessage(argumentsHelp.build()).queue();
            return;
        }

        List<TextChannel> mentioned  = message.getMentionedChannels();
        if (mentioned.size() == 0) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Sorry")
                    .setDescription("You need to mention a channel to send the webhook to")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        if (!args.get(0).equals(mentioned.get(0).getAsMention())) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Sorry")
                    .setDescription("The channel to send the webhook to must directly follow the command")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        args.remove(0);

        String avatarUrl;
        String webhookName;
        String plainMessage;
        String embedTitle;
        String embedDescription;
        String embedFooterText;
        String embedFooterIconUrl;
        String embedThumbnailUrl;
        String embedImageUrl;
        String embedAuthorName;
        boolean fieldsInline = true;
        HashMap</* Name */ String, /* Value */String> fields = new HashMap<String, String>();

        ArrayList<String> newArgs = new ArrayList<String>(Arrays.asList(String.join(" ", args).trim().split("--")));

        System.out.println(newArgs.size());

        if (newArgs.size() == 1) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Sorry")
                    .setDescription("You need to provide further arguments, for more information on arguments, run this command followed by `arguments`")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        newArgs.remove(0);

        for (String arg : newArgs) {
            System.out.println(arg);

            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Sorry")
                    .setDescription("`" + arg + "` is not a valid argument for this command, to view valid arguments, use `" + sqlManager.readGuild(event.getGuild().getId()).getPrefix() + "webhook arguments`")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        channel.sendMessage("eh thx 4 teh chanel").queue();
        webhookManager.loadWebhook(event.getGuild());

    }

}
