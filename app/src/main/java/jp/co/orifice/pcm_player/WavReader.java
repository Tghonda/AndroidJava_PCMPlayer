package jp.co.orifice.pcm_player;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.IllegalFormatException;

public class WavReader implements AudioDataReader {
    private final int CHANK_RIFF = 0x52494646;      //  'RIFF'
    private final int CHANK_WAVE = 0x57415645;      //  'WAVE'
    private final int CHANK_fmt  = 0x666d7420;      //  'fmt '
    private final int CHANK_data = 0x64617461;      //  'data'


    private InputStream fis;
    private BufferedInputStream bis;
    private DataInputStream dis;

    private short fmt_format;           // Liner PCM : 1
    private short fmt_channels;         // Montal : 1
    private int   fmt_samplePerSec;     // 48000
    private int   fmt_AvgBytesPerSec;   // 48000*2
    private short fmt_BlockAlign;       // 2
    private short fmt_BitsPerSample;    // 16

    private int samples;

    WavReader(InputStream fis) {
        this.fis = fis;
        bis = new BufferedInputStream(fis);
        dis = new DataInputStream(bis);

        try {
            boolean foundDataChank = false;
            while (!foundDataChank) {
                int chankID = dis.readInt();
                int chankSize = readIntLittleEndian(dis);
                switch (chankID) {
                    case CHANK_RIFF:
                        int tagWave = dis.readInt();
                        if (tagWave != CHANK_WAVE) {
                            System.out.println("WavReader:Illegal format WAVE tag.");
                            throw new RuntimeException();
                        }
                        break;
                    case CHANK_fmt:
                        if (chankSize != 16) {
                            System.out.println("WavReader:Illegal format fmt tag size(16)."+chankSize);
                            throw new RuntimeException();
                        }
                        fmt_format   = readShortLittleEndian(dis);
                        fmt_channels = readShortLittleEndian(dis);
                        fmt_samplePerSec   = readIntLittleEndian(dis);
                        fmt_AvgBytesPerSec = readIntLittleEndian(dis);
                        fmt_BlockAlign     = readShortLittleEndian(dis);
                        fmt_BitsPerSample  = readShortLittleEndian(dis);
                        break;
                    case CHANK_data:
                        samples = chankSize/(fmt_BitsPerSample/8);
                        foundDataChank = true;
                        break;
                    default:
                        dis.skipBytes(chankSize);
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("WavReader:Exception "+e);
            throw new RuntimeException();
        }
    }
    private int readIntLittleEndian(DataInputStream dis) throws IOException {
        byte[] readBuf = new byte[4];
        dis.read(readBuf);
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.put(readBuf);
        byteBuffer.flip();
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return byteBuffer.getInt();
    }
    private short readShortLittleEndian(DataInputStream dis) throws IOException {
        byte[] readBuf = new byte[2];
        dis.read(readBuf);
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.put(readBuf);
        byteBuffer.flip();
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return byteBuffer.getShort();
    }

    public int getFormat() {
        return fmt_format;
    }
    public int getChannels() {
        return fmt_channels;
    }
    public int getSamplingRate() {
        return fmt_samplePerSec;
    }
    public int getSamples() {
        return samples;
    }
    public int getPcmData(short[] buf) throws IOException{
        byte[] readBuf = new byte[buf.length*2];
        ByteBuffer byteBuffer = ByteBuffer.allocate(buf.length*2);
        int readSize = dis.read(readBuf);
        byteBuffer.put(readBuf);
        byteBuffer.flip();
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        for (int i=0; i<readSize/2; i++) {
            buf[i] = byteBuffer.getShort();
        }
        return readSize/2;
    }
}
