package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class GuildInfoCommand extends Command {

    public GuildInfoCommand() {
        super(new String[]{"guildinfo", "gi"}, "Shows information of the current guild.", "", CommandAccessType.USER_ANY, false);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        Guild guild = event.getGuild();

        EmbedBuilder eb = new EmbedBuilder();
        eb
                .setTitle("Guild Information")
                .setColor(Constants.VESTRIMU_PURPLE)
                .setFooter("Vestrimu", Constants.ICON_URL)
                .addField("Name", guild.getName(), true)
                .addField("ID", guild.getId(), true)
                .addField("Owner",
                        guild.getOwner().getEffectiveName() + " (" +
                        guild.getOwner().getUser().getName() +
                        guild.getOwner().getUser().getDiscriminator() + ")", true)
                .addField("Icon URL", "[`Icon URL`](" +
                        guild.getIconUrl() +
                        " \"Icon URL\")", true)
                .addField("Splash URL", guild.getSplashUrl() == null ? "`N/A`" :
                        "[`Splash URL`](" +
                                guild.getSplashUrl() +
                                " \"Splash URL\")", true)
                .addField("Text Channels", String.valueOf(guild.getTextChannels().size()), true)
                .addField("Voice Channels", String.valueOf(guild.getVoiceChannels().size()), true)
                .addField("Region", guild.getRegion().getEmoji() + " " + guild.getRegion().getName(), true)
                .addField("Members", "Total: " + guild.getMembers().size() + "\n" +
                        "Users: " + guild.getMembers().stream().filter(m -> !m.getUser().isBot()).toArray().length + "\n" +
                        "Bots: " + guild.getMembers().stream().filter(m -> m.getUser().isBot()).toArray().length, true)
                .addField("MFA Level", guild.getRequiredMFALevel().name(), true)
                .setThumbnail(guild.getIconUrl());

        channel.sendMessage(eb.build()).queue();
    }

}
