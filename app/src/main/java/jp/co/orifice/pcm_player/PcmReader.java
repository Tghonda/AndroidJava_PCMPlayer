package jp.co.orifice.pcm_player;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PcmReader implements AudioDataReader{

    private InputStream fis;
    private BufferedInputStream bis;
    private DataInputStream dis;

    private int samples;

    PcmReader(InputStream fis) {
        this.fis = fis;
        bis = new BufferedInputStream(fis);
        dis = new DataInputStream(bis);
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
        for (int i=0; i<readSize/2; i++) {
            buf[i] = byteBuffer.getShort();
        }
        return readSize/2;
    }
}
