package com.georlegacy.general.vestrimu.core.managers;

import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.objects.config.GuildConfiguration;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class CommandManager extends ListenerAdapter {

    @Inject private SQLManager sqlManager;

    private List<Command> commands;

    public CommandManager() {
        commands = new ArrayList<Command>();
    }

    public List<Command> getCommands() {
        return commands;
    }

    public synchronized void addCommand(Command command) {
        commands.add(command);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        Guild guild = event.getGuild();

        if (message.getAuthor().isBot())
            return;

        if (channel.getType().equals(ChannelType.PRIVATE))
            return;

        if (message.getAuthor().getId().equals(Constants.VESTRIMU_ID))
            return;

        if (sqlManager.isWaiting(event.getGuild()) != null)
            return;

        if (event.getAuthor().isBot()) return;
        GuildConfiguration configuration = sqlManager.readGuild(guild.getId());
        for (Command command : commands) {
            String[] cmdnames = command.getNames();
            for (String cmdname : cmdnames) {
                if (message.getContentRaw().toLowerCase().startsWith(configuration.getPrefix() + cmdname)) {
                    if (command.isOnlyAdminModeServers() && !configuration.isAdmin_mode()) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb
                                .setTitle("Sorry")
                                .setDescription("That command can only be used in servers that I administrate")
                                .setColor(Constants.VESTRIMU_PURPLE)
                                .setFooter("Vestrimu", Constants.ICON_URL);
                        channel.sendMessage(eb.build()).queue();
                        return;
                    }
                    if (command.getAccessType().equals(CommandAccessType.SUPER_ADMIN)) {
                        if (Constants.ADMIN_IDS.contains(event.getAuthor().getId())) {
                            command.run(event);
                            return;
                        } else {
                            EmbedBuilder eb = new EmbedBuilder();
                            eb
                                    .setTitle("Sorry")
                                    .setDescription("That command can only be used by super administrators")
                                    .setColor(Constants.VESTRIMU_PURPLE)
                                    .setFooter("Vestrimu", Constants.ICON_URL);
                            channel.sendMessage(eb.build()).queue();
                            return;
                        }
                    } else if (command.getAccessType().equals(CommandAccessType.SERVER_ADMIN)) {
                        if (sqlManager.readGuild(guild.getId()).isAdmin_mode()) {
                            if (event.getMember().getRoles().contains(guild.getRoleById(configuration.getBotaccessroleid()))) {
                                command.run(event);
                                return;
                            } else {
                                EmbedBuilder eb = new EmbedBuilder();
                                eb
                                        .setTitle("Sorry")
                                        .setDescription("That command can only be used by server administrators")
                                        .setColor(Constants.VESTRIMU_PURPLE)
                                        .setFooter("Vestrimu", Constants.ICON_URL);
                                channel.sendMessage(eb.build()).queue();
                                return;
                            }
                        } else {
                            if (event.getMember().equals(guild.getOwner())) {
                                command.run(event);
                                return;
                            } else {
                                EmbedBuilder eb = new EmbedBuilder();
                                eb
                                        .setTitle("Sorry")
                                        .setDescription("That command can only be used by the server owner")
                                        .setColor(Constants.VESTRIMU_PURPLE)
                                        .setFooter("Vestrimu", Constants.ICON_URL);
                                channel.sendMessage(eb.build()).queue();
                                return;
                            }
                        }
                    } else if (command.getAccessType().equals(CommandAccessType.SERVER_MOD)) {
                        if (sqlManager.readGuild(guild.getId()).isAdmin_mode()) {
                            if (event.getMember().getRoles().contains(guild.getRoleById(configuration.getBotmodroleid()))) {
                                command.run(event);
                                return;
                            } else {
                                EmbedBuilder eb = new EmbedBuilder();
                                eb
                                        .setTitle("Sorry")
                                        .setDescription("That command can only be used by server moderators")
                                        .setColor(Constants.VESTRIMU_PURPLE)
                                        .setFooter("Vestrimu", Constants.ICON_URL);
                                channel.sendMessage(eb.build()).queue();
                                return;
                            }
                        } else {
                            if (event.getMember().equals(guild.getOwner())) {
                                command.run(event);
                                return;
                            } else {
                                EmbedBuilder eb = new EmbedBuilder();
                                eb
                                        .setTitle("Sorry")
                                        .setDescription("That command can only be used by the server owner")
                                        .setColor(Constants.VESTRIMU_PURPLE)
                                        .setFooter("Vestrimu", Constants.ICON_URL);
                                channel.sendMessage(eb.build()).queue();
                                return;
                            }
                        }
                    }
                    command.run(event);
                }
            }
        }
    }

}
