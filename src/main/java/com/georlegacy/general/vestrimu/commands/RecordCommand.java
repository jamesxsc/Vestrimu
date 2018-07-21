package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.handlers.audio.AudioRecordHandler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RecordCommand extends Command {

    public RecordCommand() {
        super(new String[]{"recordcall", "tapecall"}, "Records the duration of a call. [WIP]", "", CommandAccessType.SUPER_ADMIN, false);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        Member member = event.getMember();
        MessageChannel channel = event.getChannel();
        Guild guild = event.getGuild();

        AudioRecordHandler recordHandler = new AudioRecordHandler();
        guild.getAudioManager().setReceivingHandler(recordHandler);

        if (!member.getVoiceState().inVoiceChannel()) {
            channel.sendMessage("boi get in a channel").queue();
            return;
        }
        VoiceChannel voiceChannel = member.getVoiceState().getChannel();
        guild.getAudioManager().openAudioConnection(voiceChannel);

        Executors.newScheduledThreadPool(1).schedule(new Runnable() {
            @Override
            public void run() {
                guild.getAudioManager().closeAudioConnection();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(recordHandler.getOutputStream().toByteArray()), new AudioFormat(48000f, 16, 2, true, true), recordHandler.getOutputStream().toByteArray().length), AudioFileFormat.Type.WAVE, os);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                channel.sendFile(os.toByteArray(), "audio.wav").queue();
            }
        }, 10, TimeUnit.SECONDS);

    }

}
