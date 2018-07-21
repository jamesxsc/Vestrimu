package com.georlegacy.general.vestrimu.handlers.audio;

import lombok.Getter;
import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AudioRecordHandler implements AudioReceiveHandler {

    private final double volume = 0.5;
    private int millisReceived;
    @Getter private ByteArrayOutputStream outputStream;

    public AudioRecordHandler() {
        millisReceived = 0;
        outputStream = new ByteArrayOutputStream();
    }

    @Override
    public boolean canReceiveCombined() {
        return true;
    }

    @Override
    public boolean canReceiveUser() {
        return false;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        byte[] audioReceived = combinedAudio.getAudioData(this.volume);
        try {
            outputStream.write(audioReceived);
            millisReceived += 20;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleUserAudio(UserAudio userAudio) {
        // We do not handle user audio
    }

}
