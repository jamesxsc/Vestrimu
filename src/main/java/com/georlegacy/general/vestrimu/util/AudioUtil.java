package com.georlegacy.general.vestrimu.util;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;

import java.io.File;

public class AudioUtil {

    public static File compress(File source, String uuid) {

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libmp3lame");
        audio.setBitRate(128000);
        audio.setChannels(2);
        audio.setSamplingRate(48000);

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("mp3");
        attrs.setAudioAttributes(audio);

        File target = new File("tmp" + File.separator + "recordings" + File.separator + uuid + ".mp3");

        Encoder encoder = new Encoder();
        try {
            encoder.encode(source, target, attrs);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return target;
    }

}
