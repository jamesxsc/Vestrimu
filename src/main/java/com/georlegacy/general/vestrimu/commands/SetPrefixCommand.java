package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.google.inject.Inject;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class SetPrefixCommand extends Command {

    @Inject private SQLManager sqlManager;

    public SetPrefixCommand() {
        super(new String[]{"setprefix", "prefix", "sp"}, "Sets the prefix for the server.", "", CommandAccessType.SERVER_ADMIN, false);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        String raw = message.getContentRaw();

        ArrayList<String> args = new ArrayList<String>(Arrays.asList(message.getContentRaw().split(" ")));
        args.remove(0);

        if (args.size() == 0) {
            channel.sendMessage("THE CURRENT prefix is `" + sqlManager.readGuild(guild.getId()).getPrefix() + "`\nTo change it, run this command with the prefix after it lol").queue();
            return;
        }

        String oldPrefix = sqlManager.readGuild(guild.getId()).getPrefix();
        String newPrefix = args.get(0);
        sqlManager.updateGuild(sqlManager.readGuild(guild.getId()).setPrefix(newPrefix));
        channel.sendMessage("welp, prefix was `" + oldPrefix + "`\nnow its `" + newPrefix + "`").queue();
    }

}
