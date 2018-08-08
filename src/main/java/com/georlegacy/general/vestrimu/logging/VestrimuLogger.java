package com.georlegacy.general.vestrimu.logging;

import com.georlegacy.general.vestrimu.Vestrimu;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.logging.Level;

public class VestrimuLogger {

    @Getter @Setter private boolean error;
    @Getter @Setter private boolean trace;
    @Getter @Setter private boolean warn;
    @Getter @Setter private boolean debug;
    @Getter @Setter private boolean info;
    @Getter @Setter private boolean fine;

    @Getter @Setter private boolean doConsole;
    @Getter @Setter private boolean doDiscord;

    @Getter @Setter private String discordChannelId;

    @Getter private String latestLogToConsole;
    @Getter private String latestLogToDiscord;

    public VestrimuLogger(boolean error, boolean trace,
                          boolean warn, boolean debug,
                          boolean info, boolean fine,
                          boolean doConsole, boolean doDiscord,
                          String discordChannelId) {
        this.error = error;
        this.trace = trace;
        this.warn = warn;
        this.debug = debug;
        this.info = info;
        this.fine = fine;

        this.doConsole = doConsole;
        this.doDiscord = doDiscord;

        this.discordChannelId = discordChannelId;
    }

    public VestrimuLogger() {
        this(true, true, true, false, true, true, true, false, null);
    }

    public VestrimuLogger(boolean debug, String discordChannelId) {
        this(true, true, true, debug, true, true, true, true, discordChannelId);
    }

    public void log(Level level, String message) {
        if (doConsole) {
            System.out.printf("[%s %S] %s\n", new Date().toGMTString(), level.getName(), message);
            latestLogToConsole = "[" +
                    new Date().toGMTString() +
                    " " +
                    level.getName().toUpperCase() +
                    "] " +
                    message;
        }
        if (doDiscord)
            try {
                Vestrimu.getInstance().getShardManager().getTextChannelById(discordChannelId)
                .sendMessage("```" +
                        "[" +
                        new Date().toGMTString() +
                        " " +
                        level.getName().toUpperCase() +
                        "] " +
                        message +
                        "```").queue();
                latestLogToDiscord = "[" +
                        new Date().toGMTString() +
                        " " +
                        level.getName().toUpperCase() +
                        "] " +
                        message;
            } catch (Exception ex) {
                logConsole(Level.WARNING, "Error whilst sending log message to discord.\n" + ex.getMessage());
            }
    }

    public void logConsole(Level level, String message) {
        System.out.printf("[%s %S] %s\n", new Date().toGMTString(), level.getName(), message);
        latestLogToConsole = "[" +
                new Date().toGMTString() +
                " " +
                level.getName().toUpperCase() +
                "] " +
                message;
    }

    public void logDiscord(Level level, String message) {
        if (doDiscord) {
            try {
                Vestrimu.getInstance().getShardManager().getTextChannelById(discordChannelId)
                        .sendMessage("```" +
                                "[" +
                                new Date().toGMTString() +
                                " " +
                                level.getName().toUpperCase() +
                                "] " +
                                message).queue();
                latestLogToDiscord = "[" +
                        new Date().toGMTString() +
                        " " +
                        level.getName().toUpperCase() +
                        "] " +
                        message;
            } catch (Exception ex) {
                logConsole(Level.WARNING, "Error whilst sending log message to discord.\n" + ex.getMessage());
            }
        } else
            logConsole(Level.INFO, "Attempted to send log message to discord but discord logging was disabled.");
    }

    public void error(String message) {
        log(Level.SEVERE, message);
    }

    public void trace(String message) {
        log(Level.SEVERE, message);
    }

    public void debug(String message) {
        log(new DebugLevel(), message);
    }

    public void warn(String message) {
        log(Level.WARNING, message);
    }

    public void info(String message) {
        log(Level.INFO, message);
    }

    public void fine(String message) {
        log(Level.FINE, message);
    }

}
