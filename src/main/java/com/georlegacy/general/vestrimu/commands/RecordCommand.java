package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.objects.config.GuildConfiguration;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.handlers.audio.AudioRecordHandler;
import com.georlegacy.general.vestrimu.util.AudioUtil;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.io.IOUtils;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RecordCommand extends Command {

    @Inject private SQLManager sqlManager;

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        Member member = event.getMember();
        MessageChannel channel = event.getChannel();
        Guild guild = event.getGuild();

        GuildConfiguration configuration = sqlManager.readGuild(guild.getId());

        AudioRecordHandler recordHandler = new AudioRecordHandler();
        guild.getAudioManager().setReceivingHandler(recordHandler);

        if (!member.getVoiceState().inVoiceChannel()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Sorry")
                    .setDescription("You need to be in a voice channel to record a call.")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        EmbedBuilder start = new EmbedBuilder();
        start
                .setTitle("Success")
                .setDescription("Your current call is being recorded, the recording will end when you leave the channel or when the recording limit of 60 minutes has elapsed.")
                .setColor(Constants.VESTRIMU_PURPLE)
                .setFooter("Vestrimu", Constants.ICON_URL);
        channel.sendMessage(start.build()).queue();

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

        Vestrimu.getInstance().getEventWaiter().waitForEvent(GuildVoiceLeaveEvent.class, e ->
                        e.getMember().equals(member) &&
                        e.getChannelLeft().equals(voiceChannel),
                e -> {
                    endCall(channel, guild, uuid, file, fileOutputStream, executorService);
                },
                60,
                TimeUnit.MINUTES,
                () -> {
                    endCall(channel, guild, uuid, file, fileOutputStream, executorService);
                }
                );

        final AtomicInteger seconds = new AtomicInteger();

        executorService.scheduleAtFixedRate(() -> {
            try {
                fileOutputStream.write(recordHandler.getOutputStream().toByteArray());
                recordHandler.getOutputStream().reset();
                seconds.addAndGet(3);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 3, TimeUnit.SECONDS);



    }

    private void endCall(MessageChannel channel, Guild guild, UUID uuid, File file, FileOutputStream fileOutputStream, ScheduledExecutorService executorService) {
        EmbedBuilder end = new EmbedBuilder();
        end
                .setColor(Constants.VESTRIMU_PURPLE)
                .setTitle("<a:loading:468689847935303682> Your audio is being processed.");
        channel.sendMessage(end.build()).queue(msg -> msg.delete().queueAfter(120, TimeUnit.SECONDS));

        executorService.shutdownNow();
        guild.getAudioManager().closeAudioConnection();

        File wavFile = new File("tmp" + File.separator + "recordings" + File.separator + uuid.toString() + ".wav");
        try {
            FileInputStream pcmInputStream = new FileInputStream(file);
            FileOutputStream wavOutputStream = new FileOutputStream(wavFile);
            byte[] pcmBytes = IOUtils.toByteArray(pcmInputStream);
            AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(pcmBytes),
                            new AudioFormat(48000, 16, 2, true,
                                    true), pcmBytes.length / 4),
                    AudioFileFormat.Type.WAVE, wavOutputStream);
            wavOutputStream.flush();
            wavOutputStream.close();
            pcmInputStream.close();
            fileOutputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        List<File> toSends = AudioUtil.compress(wavFile, uuid.toString());
        int iter = 1;
        for (File toSend : toSends)
            channel.sendFile(toSend, "Vestrimu Call Recording Part " + iter++ + ".mp3").queue();
    }


    public RecordCommand() {
        super(new String[]{"recordcall", "tapecall"}, "Records the duration of a call. [WIP]", "", CommandAccessType.BETA_TESTER, false);
    }

}
