package com.georlegacy.general.vestrimu;

import net.dv8tion.jda.api.entities.Guild;

public class Functions {

    public static String testFunction() {
        return "Test";
    }

    public static void reactToMessage(Guild guild, String channelId, String messageId, String reaction) {
        guild.getTextChannelById(channelId).getHistory().getMessageById(messageId).addReaction(reaction).queue();
    }

}
