package com.georlegacy.general.vestrimu.core.managers;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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

    public void addCommand(Command command) {
        commands.add(command);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        if (event.getAuthor().isBot()) return;
        for (Command command : commands) {
            String cmdname = command.getName();
            if (message.getContentRaw().startsWith(Vestrimu.getInstance().getGuildConfigs().getOrDefault(event.getGuild().getId(), sqlManager.readGuild(event.getGuild().getId())).getPrefix() + command.getName())) {
                if (command.isAdminOnly()) {
                    if (Constants.ADMIN_IDS.contains(event.getAuthor().getId())) {
                        command.run(event);
                        return;
                    } else {
                        channel.sendMessage("no perms lol").queue();
                        return;
                    }
                }
                command.run(event);
            }
        }
    }

}
