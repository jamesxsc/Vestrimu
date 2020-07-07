package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.SecretConstants;
import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import com.rmtheis.yandtran.TranslateUtils;
import com.rmtheis.yandtran.language.Language;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class TranslateCommand extends Command {

    public TranslateCommand() {
        super(new String[]{"translate"}, "Translates a message", "<language> <message>", CommandAccessType.USER_ANY, false);
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        ArrayList<String> args = new ArrayList<String>(Arrays.asList(message.getContentRaw().split(" ")));
        args.remove(0);
        if (args.size() == 0) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setTitle("**Sorry**")
                    .setDescription("You didn't provide a language or anything to translate, try using this format\n`translate " + getHelp() + "`")
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }
        if (args.size() == 1) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setTitle("**Sorry**")
                    .setDescription("You didn't provide anything to translate, try using this format\n`translate " + getHelp() + "`")
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }
        final Language finalLanguage;
        try {
            finalLanguage = Language.valueOf(args.get(0).toUpperCase());
        } catch (IllegalArgumentException ex) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setTitle("**Sorry**")
                    .setDescription("The language you provided (`" + args.get(0) + "`) is not a valid language. Find a list of valid languages here\nhttps://yandex.com/support/webmaster/robot-workings/supported-languages.html")
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }
        args.remove(0);
        Language originalLanguage = Language.fromString(TranslateUtils.detect(SecretConstants.YAND_TRAN_SECRET, String.join(" ", args)));
        String translated = TranslateUtils.translate(SecretConstants.YAND_TRAN_SECRET, String.join(" ", args), originalLanguage, finalLanguage);
        EmbedBuilder eb = new EmbedBuilder();
        eb
                .setColor(Constants.VESTRIMU_PURPLE)
                .setTitle("**Success**")
                .addField("Translation", translated, false)
                .setFooter("Vestrimu", Constants.ICON_URL);
        channel.sendMessage(eb.build()).queue();
    }

}
