package pro.antonshu.services.chunk;

import io.netty.channel.Channel;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ChunkService {

    /*
     * This class is needed to transmit files by chunks.
     * It's allows to transmit big files.
     */

    public void sendFile(Channel channel, String pathToFile) throws IOException {
        RandomAccessFile raf = null;
        long length = -1;
        try {
            raf = new RandomAccessFile(pathToFile, "r");
            length = raf.length();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            if (length < 0 && raf != null) {
                raf.close();
            }
        }

        if (channel.pipeline().get(SslHandler.class) == null) {
            channel.write(new DefaultFileRegion(raf.getChannel(), 0, length));
        } else {
            channel.write(new ChunkedFile(raf));
        }
        channel.flush();
    }
}
