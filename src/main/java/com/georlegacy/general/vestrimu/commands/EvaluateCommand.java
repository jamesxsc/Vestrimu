package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.SecretConstants;
import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.google.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class EvaluateCommand extends Command {
    @Inject private SQLManager sqlManager;

    private ScriptEngine scriptEngine;

    public EvaluateCommand() {
        super(new String[]{"eval", "evaluate", "evalstatement", "runstatement"}, "Evaluates statements.", "<statement>", CommandAccessType.SUPER_ADMIN, false);
        scriptEngine = new ScriptEngineManager().getEngineByName("js");
        try {
            scriptEngine.eval("var _imports = new JavaImporter(" +
                    "java.lang," +
                    "java.util," +
                    "java.awt," +
                    "Packages.net.dv8tion.jda.core," +
                    "Packages.net.dv8tion.jda.core.entities," +
                    "Packages.net.dv8tion.jda.core.events.message," +
                    "Packages.com.georlegacy.general.vestrimu," +
                    "Packages.com.rmtheis.yandtran" +
            ");");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        long currentTime = System.currentTimeMillis();

        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        ArrayList<String> args = new ArrayList<String>(Arrays.asList(message.getContentRaw().split(" ")));
        args.remove(0);

        scriptEngine.put("message", message);
        scriptEngine.put("jda", event.getJDA());
        scriptEngine.put("vestrimu", Vestrimu.getInstance());
        scriptEngine.put("guild", event.getGuild());
        scriptEngine.put("channel", channel);
        scriptEngine.put("author", event.getAuthor());
        scriptEngine.put("event", event);
        scriptEngine.put("sql", new SQL());

        try {
            Object output = scriptEngine.eval("with (_imports) {" + String.join(" ", args) + "}");
            if (output == null) {
                channel.sendMessage(success(System.currentTimeMillis() - currentTime)).queue();
            } else {
                channel.sendMessage(details(output, System.currentTimeMillis() - currentTime)).queue();
            }
        } catch (Exception ex) {
            channel.sendMessage(error(ex.toString(), System.currentTimeMillis() - currentTime)).queue();
        }
    }

    private MessageEmbed success(long time) {
        return new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("__**Success**__")
                .setDescription(":white_check_mark:")
                .addField("Time Taken", time + "ms", false)
                .build();
    }

    private MessageEmbed details(Object details, long time) {
        return new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("__**Success:**__")
                .setDescription("```js\n" + details + "```")
                .addField("Time Taken", time + "ms", false)
                .build();
    }

    private MessageEmbed error(String error, long time) {
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("__**Failed to Evaluate:**__")
                .setDescription("```js\n" + error + "```")
                .addField("Time Taken", time + "ms", false)
                .build();
    }

    private class SQL {
        public Object evalStatement(String query) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://db.615283.net/vestrimu", SecretConstants.SQL_USER, SecretConstants.SQL_PASS)) {
                Statement statement = connection.createStatement();
                return statement.execute(query);
            } catch (SQLException ex) {
                return ex;
            }
        }
    }

}
