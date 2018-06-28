package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.SecretConstants;
import com.georlegacy.general.vestrimu.core.Command;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class EvaluateCommand extends Command {

    public EvaluateCommand() {
        super("eval", "Evaluates JDA Code", "<JDA statement>", true);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        ArrayList<String> args = new ArrayList<String>(Arrays.asList(message.getContentRaw().replaceFirst("-" + "eval", "").trim().split(" ")));

        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("js");
        scriptEngine.put("message", message);
        scriptEngine.put("jda", event.getJDA());
        scriptEngine.put("guild", event.getGuild());
        scriptEngine.put("channel", channel);
        scriptEngine.put("author", event.getAuthor());
        scriptEngine.put("sql", new SQL());

        try {
            Object output = scriptEngine.eval(String.join(" ", args));
            if (output == null) {
                channel.sendMessage(":white_check_mark: ").queue();
            } else {
                channel.sendMessage(":white_check_mark:\n `" + output + "`").queue();
            }
        } catch (ScriptException ex) {
            channel.sendMessage(":x: **Failed to evaluate.**\n`" + ex.getMessage() + "`").queue();
        }
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
