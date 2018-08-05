package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class PurgeCommand extends Command {

    public PurgeCommand() {
        super(new String[]{"purge", "clear", "purgemessages"}, "Purges a number of messages from a channel.", "<number> [bot|user|webhook]", CommandAccessType.SERVER_MOD, true);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        Message message = event.getMessage();

        ArrayList<String> args = new ArrayList<String>(Arrays.asList(message.getContentRaw().split(" ")));
        args.remove(0);

        if (args.isEmpty()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Sorry")
                    .setDescription("You need to provide a number of messages to purge")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        int toRemove;
        try {
            toRemove = Integer.parseInt(args.get(0));
        } catch (NumberFormatException ex) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Sorry")
                    .setDescription("`" + args.get(0) + "` is not a valid number of messages.")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        if (args.size() == 1 || (!args.get(1).equalsIgnoreCase("bot") &&
                !args.get(1).equalsIgnoreCase("user") &&
                !args.get(1).equalsIgnoreCase("webhook"))) {
            channel.getHistory().retrievePast(toRemove).queue(msgs -> msgs.forEach(msg -> msg.delete().queue()));
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Success")
                    .setDescription(toRemove + "messages has been deleted from this channel.")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
        } else {
            if (args.get(0).equalsIgnoreCase("bot")) {
                channel.getIterableHistory().queue(msgs -> {
                    int i = toRemove;
                    for (Object m : msgs.stream().filter(m -> m.getAuthor().isBot()).toArray()) {
                        if (i != 0) {
                            ((Message) m).delete().queue();
                            i--;
                        }
                    }
                });
                EmbedBuilder eb = new EmbedBuilder();
                eb
                        .setTitle("Success")
                        .setDescription(toRemove + " bot messages have been deleted from this channel.")
                        .setColor(Constants.VESTRIMU_PURPLE)
                        .setFooter("Vestrimu", Constants.ICON_URL);
                channel.sendMessage(eb.build()).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }
            if (args.get(0).equalsIgnoreCase("user")) {
                channel.getIterableHistory().queue(msgs -> {
                    int i = toRemove;
                    for (Object m : msgs.stream().filter(m -> !m.getAuthor().isBot()).toArray()) {
                        if (i != 0) {
                            ((Message) m).delete().queue();
                            i--;
                        }
                    }
                });
                EmbedBuilder eb = new EmbedBuilder();
                eb
                        .setTitle("Success")
                        .setDescription(toRemove + " user messages have been deleted from this channel.")
                        .setColor(Constants.VESTRIMU_PURPLE)
                        .setFooter("Vestrimu", Constants.ICON_URL);
                channel.sendMessage(eb.build()).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }
            if (args.get(0).equalsIgnoreCase("webhook")) {
                channel.getIterableHistory().queue(msgs -> {
                    int i = toRemove;
                    for (Object m : msgs.stream().filter(m -> m.isWebhookMessage()).toArray()) {
                        if (i != 0) {
                            ((Message) m).delete().queue();
                            i--;
                        }
                    }
                });
                EmbedBuilder eb = new EmbedBuilder();
                eb
                        .setTitle("Success")
                        .setDescription(toRemove + " webhook messages have been deleted from this channel.")
                        .setColor(Constants.VESTRIMU_PURPLE)
                        .setFooter("Vestrimu", Constants.ICON_URL);
                channel.sendMessage(eb.build()).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }
        }
    }

}
