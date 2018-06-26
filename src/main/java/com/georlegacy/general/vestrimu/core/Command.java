package com.georlegacy.general.vestrimu.core;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class Command {

    private final String name;
    private final String description;
    private final String help;
    private final boolean adminOnly;

    protected Command() {
        this(null, null, null, false);
    }

    protected Command(String name, String description, String help, boolean adminOnly) {
        this.name = name;
        this.description = description;
        this.help = help;
        this.adminOnly = adminOnly;
    }

    public abstract void execute(MessageReceivedEvent event);

    public void run(MessageReceivedEvent event) {
        execute(event);
    }

    public String getName() {
        return name;
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
