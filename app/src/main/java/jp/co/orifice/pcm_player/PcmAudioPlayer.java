package jp.co.orifice.pcm_player;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import java.io.IOException;

interface AudioDataReader {
    int getPcmData(short[] buf) throws IOException;
}

public class PcmAudioPlayer {
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;

    private AudioTrack audioTrack;
    private Thread threadPlayer;

    PcmAudioPlayer(int samplingRate) {
        audioTrack = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_UNKNOWN)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AUDIO_FORMAT)
                        .setSampleRate(samplingRate)
                        .setChannelMask(CHANNEL_CONFIG)
                        .build())
                .setBufferSizeInBytes(samplingRate)
                .build();
    }

    public void play(final AudioDataReader wavReader) {

        threadPlayer = new Thread() {
            @Override
            public void run() {
                short[] buf = new short[512];
                audioTrack.play();

                while (!isInterrupted()) {
                    try {
                        int readSamples = wavReader.getPcmData(buf);
                        audioTrack.write(buf, 0, readSamples);
                    }catch (IOException e) {
                        break;
                    }
                }
                audioTrack.stop();
            }
        };

        threadPlayer.start();
    }

    public void stop() {
        if (threadPlayer == null)
            return;

        try {
            threadPlayer.interrupt();
            threadPlayer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            threadPlayer = null;
        }
    }
}
