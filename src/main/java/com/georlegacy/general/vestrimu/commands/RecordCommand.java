package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.handlers.audio.AudioRecordHandler;
import com.georlegacy.general.vestrimu.util.AudioUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.io.IOUtils;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RecordCommand extends Command {
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

        UUID uuid = UUID.randomUUID();
        File file = new File("tmp" + File.separator + "recordings" + File.separator + uuid.toString() + ".pcm");
        file.getParentFile().mkdirs();
        FileOutputStream fileOutputStream;
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        final AtomicInteger seconds = new AtomicInteger();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println(seconds);
                if (seconds.get() >= 30) {
                    File wavFile = new File("tmp" + File.separator + "recordings" + File.separator + uuid.toString() + ".wav");
                    try {
                        FileOutputStream wavOutputStream = new FileOutputStream(wavFile);
                        FileInputStream pcmInputStream = new FileInputStream(file);
                        System.out.println(IOUtils.toByteArray(pcmInputStream).length / 2);
                        AudioSystem.write(new AudioInputStream(pcmInputStream,
                                new AudioFormat(48000f, 16, 2, true,
                                        true), wavFile.length() / 32),
                                AudioFileFormat.Type.WAVE, wavOutputStream);
                        wavOutputStream.close();
                        pcmInputStream.close();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    File toSend = AudioUtil.compress(wavFile, uuid.toString());
                    channel.sendFile(toSend).queue();
                    executorService.shutdownNow();
                    return;
                }
                try {
                    System.out.println("adding");
                    fileOutputStream.write(recordHandler.getOutputStream().toByteArray());
                    recordHandler.getOutputStream().reset();
                    seconds.addAndGet(3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 3, TimeUnit.SECONDS);



    }


    public RecordCommand() {
        super(new String[]{"recordcall", "tapecall"}, "Records the duration of a call. [WIP]", "", CommandAccessType.SUPER_ADMIN, false);
    }

}
