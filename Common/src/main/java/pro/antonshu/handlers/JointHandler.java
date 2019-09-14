package pro.antonshu.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pro.antonshu.services.commands.ComService;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
  * Server and Handler are implemented in the protocol version.
  * This handler is used both on the server and on the client.
  * Therefore it contains 2 constructors to separate functions.
  * Client/server can receive / send either a file or commands.
  * Each command has 4 required fields:
  * 1. Command marker (16 bytes);
  * 2. Type of command (16 bytes);
  * 3. The owner of the command (16 bytes);
  * 4. Array of data (976 bytes);
  *
*/
public class JointHandler extends ChannelInboundHandlerAdapter {

    private Path path;
    private String owner;
    private byte[] marker = new byte[16];
    private byte[] type = new byte[16];
    private byte[] user = new byte[16];
    private ComService comService;
    private String currentFilename;
    private String userName;
    private boolean serverInstance;
    private static final Logger logger = LogManager.getLogger(JointHandler.class);

    /*
     * Constructor for the Client. It's need to contain GUI @param
     */
    public JointHandler(String owner) throws IOException {
        this.owner = owner;
        this.path = getRootByOwner(owner);
        this.comService = new ComService(owner);
        this.serverInstance = true;
    }

    /*
     * Overloaded constructor for the Client. It's need to contain GUI @param
     */
    public JointHandler(String owner, DefaultListModel<String> fileListModel) throws IOException {
        this.owner = owner;
        this.path = getRootByOwner(owner);
        this.comService = new ComService(owner, fileListModel);

    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        ByteBuf byteBuf = (ByteBuf) msg;
        ByteBuf buf = byteBuf.duplicate();

        buf.readBytes(marker);
        if (new String(marker).trim().equals("marker")) {
            buf.readBytes(type);
            buf.readBytes(user);
            userName = new String(user).trim();
            buf.resetReaderIndex();
            logger.info(owner + " received command: " + new String(type));
            currentFilename = comService.parseMsg(ctx, buf);
        } else {
            receiveFile(byteBuf);
        }
    }

    private void receiveFile(ByteBuf byteBuf) throws IOException {
        String pathToWriteFile;
        if (serverInstance) {
            pathToWriteFile = path.toString() + File.separator + userName + File.separator + currentFilename;
        } else {
            pathToWriteFile = path.toString() + File.separator + currentFilename;
        }
        File file = new File(pathToWriteFile);

        if (!file.exists()) {
            file.createNewFile();
        }

        ByteBuffer byteBuffer = byteBuf.nioBuffer();

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();

        while (byteBuffer.hasRemaining()) {
            fileChannel.position(file.length());
            fileChannel.write(byteBuffer);
        }

        byteBuf.release();
        fileChannel.close();
        randomAccessFile.close();
    }

    private Path getRootByOwner(String owner) throws IOException {
        if (owner.equals("Server")) {
            if (!Files.exists(Paths.get("Server/ServerStorage/"))) {
                Files.createDirectory(Paths.get("Server/ServerStorage/"));
            }
            return Paths.get("Server/ServerStorage/");
        } else {
            if (!Files.exists(Paths.get("Client/ClientStorage/"))) {
                Files.createDirectory(Paths.get("Client/ClientStorage/"));
            }
            return Paths.get("Client/ClientStorage/");
        }
    }
}
