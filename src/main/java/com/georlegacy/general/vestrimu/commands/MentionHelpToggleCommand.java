package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.core.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MentionHelpToggleCommand extends Command {

    public MentionHelpToggleCommand() {
        super("pinghelp", "Toggles if a user must have the bot access role to view help.", "[enable|disable]", false);
    }

    @Override
    public void execute(MessageReceivedEvent event) {

    }

}
