package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.objects.config.GuildConfiguration;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import net.dv8tion.jda.core.EmbedBuilder;
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
        GuildConfiguration configuration = sqlManager.readGuild(guild.getId());

        ArrayList<String> args = new ArrayList<String>(Arrays.asList(message.getContentRaw().split(" ")));
        args.remove(0);

        if (args.size() == 0) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Prefix")
                    .setDescription("The current prefix is `" + configuration.getPrefix() + "`\nTo change it, run this command again with your desired prefix after the command")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        String oldPrefix = configuration.getPrefix();
        String newPrefix = args.get(0);
        sqlManager.updateGuild(configuration.setPrefix(newPrefix));
        EmbedBuilder eb = new EmbedBuilder();
        eb
                .setTitle("Success")
                .setDescription("The prefix for **" + guild.getName() + "** has been cahnged from `" + oldPrefix + "` to `" + newPrefix + "`")
                .setColor(Constants.VESTRIMU_PURPLE)
                .setFooter("Vestrimu", Constants.ICON_URL);
        channel.sendMessage(eb.build()).queue();
    }

}
