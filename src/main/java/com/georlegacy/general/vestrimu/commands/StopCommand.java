package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.util.Constants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop", "Stops the bot.", "", true);
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
        Vestrimu.getInstance().getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Vestrimu.getInstance().getJda().shutdownNow();
    }

}
