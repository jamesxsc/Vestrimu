package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.objects.GuildConfiguration;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccessRequiredForHelpToggleCommand extends Command {

    @Inject
    private SQLManager sqlManager;

    public AccessRequiredForHelpToggleCommand() {
        super(new String[]{"rafh", "requireaccessforhelp", "helpneedaccess", "accesshelp"}, "Toggles if a user must have the bot access role to view help.", "[enable|disable]", CommandAccessType.SERVER_ADMIN, false);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        List<String> args = new ArrayList<String>(Arrays.asList(message.getContentRaw().split(" ")));
        args.remove(0);

        GuildConfiguration configuration = sqlManager.readGuild(event.getGuild().getId());

        if (args.size() == 1) {
            if (args.get(0).equalsIgnoreCase("enable") ||
                    args.get(0).equalsIgnoreCase("on") ||
                    args.get(0).equalsIgnoreCase("yes") ||
                    args.get(0).equalsIgnoreCase("true")) {
                configuration.setRequireaccessforhelp(true);
                sqlManager.updateGuild(configuration);
                EmbedBuilder eb = new EmbedBuilder();
                eb
                        .setTitle("Success")
                        .setColor(Constants.VESTRIMU_PURPLE)
                        .setDescription("Users are now required to have the bot access role to use help.")
                        .setFooter("Vestrimu", Constants.ICON_URL);
                channel.sendMessage(eb.build()).queue();
                return;
            }
            if (args.get(0).equalsIgnoreCase("disable") ||
                    args.get(0).equalsIgnoreCase("off") ||
                    args.get(0).equalsIgnoreCase("no") ||
                    args.get(0).equalsIgnoreCase("false")) {
                configuration.setRequireaccessforhelp(false);
                sqlManager.updateGuild(configuration);
                EmbedBuilder eb = new EmbedBuilder();
                eb
                        .setTitle("Success")
                        .setColor(Constants.VESTRIMU_PURPLE)
                        .setDescription("Users are no longer required to have the bot access role to use help.")
                        .setFooter("Vestrimu", Constants.ICON_URL);
                channel.sendMessage(eb.build()).queue();
                return;
            }
        }
        boolean requiresAccess = configuration.isRequireaccessforhelp();
        configuration.setRequireaccessforhelp(!requiresAccess);
        sqlManager.updateGuild(configuration);
        EmbedBuilder eb = new EmbedBuilder();
        eb
                .setTitle("Success")
                .setColor(Constants.VESTRIMU_PURPLE)
                .setDescription(!requiresAccess ? "Users are now required to have the bot access role to use help" : "Users are no longer required to have the bot access role to use help.")
                .setFooter("Vestrimu", Constants.ICON_URL);
        channel.sendMessage(eb.build()).queue();
    }

}
