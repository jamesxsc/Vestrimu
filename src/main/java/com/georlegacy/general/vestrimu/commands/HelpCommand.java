package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.objects.config.GuildConfiguration;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends Command {

    @Inject private SQLManager sqlManager;

    public HelpCommand() {
        super(new String[]{"help", "commands"}, "Send help", "", CommandAccessType.USER_ANY, false);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        Message message = event.getMessage();

        GuildConfiguration configuration = sqlManager.readGuild(event.getGuild().getId());
        if (configuration.isRequireaccessforhelp()) {
            boolean hasAccess = false;
            for (Role role : event.getMember().getRoles()) {
                if (role.getId().equals(configuration.getBotaccessroleid())) {
                    hasAccess = true;
                    break;
                }
            }
            if (!hasAccess) {
                message.addReaction("\u274C").queue();
                return;
            }
        }

        message.addReaction("\u2705").queue();

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
                            .setTitle("Permissions")
                            .setDescription("The current access role in ** " + event.getGuild().getName() + "** is `@" + event.getGuild().getRoleById(configuration.getBotaccessroleid()).getName() + "`.\n" +
                                    "This role can be assigned to anyone, giving them access to server administration commands.\n" +
                                    "The proceedure is the same for the moderation role, which is currently `@" + event.getGuild().getRoleById(configuration.getBotmodroleid()).getName() + "`\n" +
                                    ":no_entry: - Commands only for super admins\n" +
                                    ":warning: - Commands only for server admins\n" +
                                    ":regional_indicator_m: - Command only for server moderators" +
                                    ":eight_pointed_black_star: - Commands for any user")
                            .build()
            );
        } else {
            helps.add(
                    new EmbedBuilder()
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setTitle("Permissions")
                            .setDescription("This server is not in admin mode so only the server owner can perform restricted commands.\n \n" +
                                    ":no_entry: - Commands only for super admins\n" +
                                    ":warning: - Commands only for server admins\n" +
                                    ":regional_indicator_m: - Command only for server moderators" +
                                    ":eight_pointed_black_star: - Commands for any user")
                            .build()
            );
        }

        // Commands
        EmbedBuilder commands = new EmbedBuilder();
        for (Command command : Vestrimu.getInstance().getCommandManager().getCommands()) {
            if (!sqlManager.readGuild(event.getGuild().getId()).isAdmin_mode() && command.isOnlyAdminModeServers())
                continue;
            if (command.getAccessType().equals(CommandAccessType.SUPER_ADMIN) && !(Constants.ADMIN_IDS.contains(message.getAuthor().getId())))
                continue;
            if (command.getAccessType().equals(CommandAccessType.SERVER_ADMIN) && (sqlManager.readGuild(event.getGuild().getId()).isAdmin_mode() ? !(event.getMember().getRoles().contains(event.getGuild().getRoleById(sqlManager.readGuild(event.getGuild().getId()).getBotaccessroleid()))) : !(event.getMember().isOwner())))
                continue;
            if (command.getAccessType().equals(CommandAccessType.SERVER_MOD) && (sqlManager.readGuild(event.getGuild().getId()).isAdmin_mode() ? !(event.getMember().getRoles().contains(event.getGuild().getRoleById(sqlManager.readGuild(event.getGuild().getId()).getBotmodroleid()))) : !(event.getMember().isOwner())))
                continue;
            commands.addField(
                    (command.getAccessType().equals(CommandAccessType.SUPER_ADMIN) ? ":no_entry: " : command.getAccessType().equals(CommandAccessType.SERVER_ADMIN) ? ":warning: " : command.getAccessType().equals(CommandAccessType.SERVER_MOD) ? ":regional_indicator_m: " : ":eight_pointed_black_star: ") +
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
                        .setDescription("Below are all of your available commands in **" + event.getGuild().getName() + "**\nTo see all commands click [here](TBC)")
                        .build()
        );

        helps.forEach(msg -> message.getAuthor().openPrivateChannel().queue(dm -> dm.sendMessage(msg).queue()));

    }

}
