package com.georlegacy.general.vestrimu.core;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class Command {

    private final String[] names;
    private final String description;
    private final String help;
    private final boolean adminOnly;

    protected Command() {
        this(null, null, null, false);
    }

    protected Command(String[] names, String description, String help, boolean adminOnly) {
        this.names = names;
        this.description = description;
        this.help = help;
        this.adminOnly = adminOnly;
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

    public boolean isAdminOnly() {
        return adminOnly;
    }
}
