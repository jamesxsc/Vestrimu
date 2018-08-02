package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class BotInfoCommand extends Command {

    public BotInfoCommand() {
        super(new String[]{"botinfo", "bi"}, "Shows information about the bot and guild settings if a server admin runs the command.", "", CommandAccessType.USER_ANY, false);
    }

    @Override
    public void execute(MessageReceivedEvent event) {

    }

}
