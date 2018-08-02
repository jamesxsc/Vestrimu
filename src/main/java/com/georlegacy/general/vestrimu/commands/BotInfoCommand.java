package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.objects.config.GuildConfiguration;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.google.inject.Inject;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class BotInfoCommand extends Command {

    @Inject private SQLManager sqlManager;

    public BotInfoCommand() {
        super(new String[]{"botinfo", "bi"}, "Shows information about the bot and guild settings if a server admin runs the command.", "", CommandAccessType.USER_ANY, false);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        Member author = event.getMember();
        GuildConfiguration configuration = sqlManager.readGuild(guild.getId());
    }

}
