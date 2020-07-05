package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StopCommand extends Command {

    public StopCommand() {
        super(new String[]{"abort", "stop", "haltall", "emergencystop", "orderhalt"}, "Stops the bot.", "", CommandAccessType.SUPER_ADMIN, false);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb
                .setTitle("Success")
                .setDescription("**Vestrimu** will now be shutdown.")
                .setColor(Constants.VESTRIMU_PURPLE)
                .setFooter("Vestrimu", Constants.ICON_URL);
        event.getChannel().sendMessage(eb.build()).queue();
        System.exit(0);
    }

}
