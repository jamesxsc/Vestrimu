package com.georlegacy.general.vestrimu.core;

import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class Command {

    private final String[] names;
    private final String description;
    private final String help;
    private final CommandAccessType accessType;
    private final boolean onlyAdminModeServers;

    protected Command() {
        this(null, null, null, CommandAccessType.USER_ANY, false);
    }

    protected Command(String[] names, String description, String help, CommandAccessType accessType, boolean onlyAdminModeServers) {
        this.names = names;
        this.description = description;
        this.help = help;
        this.accessType = accessType;
        this.onlyAdminModeServers = onlyAdminModeServers;
    }

    public abstract void execute(MessageReceivedEvent event);

    public void run(MessageReceivedEvent event) {
        execute(event);
    }

    public String[] getNames() {
        return names;
    }

    public String getDescription() {
        return description;
    }

    public String getHelp() {
        return help;
    }

    public CommandAccessType getAccessType() {
        return accessType;
    }

    public boolean isOnlyAdminModeServers() {
        return onlyAdminModeServers;
    }
}