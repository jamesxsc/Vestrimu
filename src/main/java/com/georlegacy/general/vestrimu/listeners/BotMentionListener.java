package com.georlegacy.general.vestrimu.listeners;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.objects.GuildConfiguration;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class BotMentionListener extends ListenerAdapter {

    @Inject
    private SQLManager sqlManager;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        if (message.getAuthor().isBot())
            return;

        if (message.getAuthor().getId().equals(Constants.VESTRIMU_ID))
            return;

        GuildConfiguration configuration = sqlManager.readGuild(event.getGuild().getId());

        if (message.getContentRaw().equals(event.getGuild().getMemberById(Constants.VESTRIMU_ID).getAsMention())) {

            if (configuration.isRequireaccessforhelp()) {
                boolean hasAccess = false;
                for (Role role : event.getMember().getRoles()) {
                    if (role.getId().equals(configuration.getBotaccessroleid())) {
                        hasAccess = true;
                        break;
                    }
                }
                if (!hasAccess)
                    return;
            }

            List<MessageEmbed> helps = new ArrayList<>();

            // Prefix
            helps.add(
                    new EmbedBuilder()
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setTitle("Prefix")
                            .setDescription("All commands begin with a prefix, **" + event.getGuild().getName() + "**'s prefix is currently `" + configuration.getPrefix() + "`.")
                            .build()
            );

            if (sqlManager.readGuild(event.getGuild().getId()).isAdmin_mode()) {
                // Access role
                helps.add(
                        new EmbedBuilder()
                                .setColor(Constants.VESTRIMU_PURPLE)
                                .setTitle("Access Role")
                                .setDescription("The current role in ** " + event.getGuild().getName() + "** is `@" + event.getGuild().getRoleById(configuration.getBotaccessroleid()).getName() + "`.\n" +
                                        "This role can be assigned to anyone, giving them access to server administration commands.")
                                .build()
                );
            } else {
                helps.add(
                        new EmbedBuilder()
                                .setColor(Constants.VESTRIMU_PURPLE)
                                .setTitle("Access Role")
                                .setDescription("This server is not in admin mode so only the server owner can perform restricted commands")
                                .build()
                );
            }

            // Commands
            EmbedBuilder commands = new EmbedBuilder();
            for (Command command : Vestrimu.getInstance().getCommandManager().getCommands()) {
                if (command.getAccessType().equals(CommandAccessType.SUPER_ADMIN) && !(Constants.ADMIN_IDS.contains(message.getAuthor().getId())))
                    continue;
                if (command.getAccessType().equals(CommandAccessType.SERVER_ADMIN) && (sqlManager.readGuild(event.getGuild().getId()).isAdmin_mode() ? !(event.getMember().getRoles().contains(event.getGuild().getRoleById(sqlManager.readGuild(event.getGuild().getId()).getBotaccessroleid()))) : !(event.getMember().isOwner())))
                    continue;
                commands.addField(
                        (command.getAccessType().equals(CommandAccessType.SUPER_ADMIN) ? ":no_entry: " : command.getAccessType().equals(CommandAccessType.SERVER_ADMIN) ? ":warning: " : ":eight_pointed_black_star: ") +
                                configuration.getPrefix() +
                                String.join("|", command.getNames()),
                        command.getDescription() +
                                "\n`" +
                                configuration.getPrefix() +
                                command.getNames()[0] +
                                " " +
                                command.getHelp() + "`",
                        false
                );
            }
            helps.add(
                    commands
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setTitle("Commands")
                            .setDescription("Below are all of your available commands")
                            .build()
            );

            helps.forEach(msg -> message.getAuthor().openPrivateChannel().queue(dm -> dm.sendMessage(msg).queue()));

        }
    }

}
