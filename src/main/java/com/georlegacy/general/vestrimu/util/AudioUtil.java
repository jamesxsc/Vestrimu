package com.georlegacy.general.vestrimu.util;

import com.google.common.collect.Lists;
import ws.schild.jave.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AudioUtil {

    public static List<File> compress(File source, String uuid) {

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libmp3lame");
        audio.setBitRate(64000);
        audio.setChannels(1);
        audio.setSamplingRate(48000);

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("mp3");
        attrs.setAudioAttributes(audio);

        byte[] header = new byte[44];
        byte[] data = new byte[(int) source.length() - 44];

        try (InputStream inputStream = new FileInputStream(source)) {
            inputStream.read(header);
            inputStream.read(data, 0, (int) source.length() - 44);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Byte[] referenceData = new Byte[data.length];
        int iter = 0;
        for (byte b : data)
            referenceData[iter++] = b;

        List<List<Byte>> partitions = referenceData.length > 9500000 ?
                Lists.partition(new ArrayList<>(Arrays.asList(referenceData)), 161280000) :
                new ArrayList<List<Byte>>(Arrays.asList(new ArrayList<Byte>(Arrays.asList(referenceData))));

        List<File> filesToSend = new ArrayList<File>();
        iter = 1;
        for (List<Byte> fileData : partitions) {
            File wavTarget = new File("tmp" + File.separator + "recordings" + File.separator + uuid + "---" + iter + ".wav");
            File mp3Target = new File("tmp" + File.separator + "recordings" + File.separator + uuid + "---" + iter + ".mp3");
            try (FileOutputStream wavOutputStream = new FileOutputStream(wavTarget)) {
                wavOutputStream.write(header);

                int pos = 0;
                byte[] fileDataPrimitive = new byte[fileData.size()];
                for (Byte b : fileData)
                    fileDataPrimitive[pos++] = b;
                wavOutputStream.write(fileDataPrimitive);

                Encoder encoder = new Encoder();
                encoder.encode(new MultimediaObject(wavTarget), mp3Target, attrs);
            } catch (IOException | EncoderException e) {
                e.printStackTrace();
            }
            iter++;
            filesToSend.add(mp3Target);
        }

        return filesToSend;
    }

}
