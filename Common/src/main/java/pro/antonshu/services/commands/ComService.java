package pro.antonshu.services.commands;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import pro.antonshu.services.bytebuf.ByteBufService;
import pro.antonshu.services.chunk.ChunkService;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class ComService {

    /*
     * This class allows to parse command messages. It works on the Server and on the Client.
     * Transmission protocol was described in ByteBufService.class
     */
    private String owner;
    private Path rootPath;
    private byte[] marker = new byte[16];
    private byte[] type = new byte[16];
    private byte[] ownerName = new byte[16];
    private byte[] data = new byte[976];
    private DefaultListModel<String> fileListModel;
    private ChunkService chunkService;

    /*
     * Constructor for Server. Without GUI.
     */
    public ComService(String owner) throws IOException {
        this.owner = owner;
        this.rootPath = getRootByOwner(owner);
        this.chunkService = new ChunkService();
    }

    /*
     * Overloaded constructor for Client. With GUI @param.
     */
    public ComService(String owner, DefaultListModel<String> fileListModel) throws IOException {
        this.owner = owner;
        this.rootPath = getRootByOwner(owner);
        this.fileListModel = fileListModel;
        this.chunkService = new ChunkService();
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


    public String parseMsg(ChannelHandlerContext ctx, ByteBuf buf) throws IOException {
        buf.readBytes(marker);
        buf.readBytes(type);
        buf.readBytes(ownerName);
        buf.readBytes(data);
        String comType = new String(type).trim();
        String msgOwner = new String(ownerName).trim();
        String dataStr = new String(data).trim();
        String currentFilename = null;

        if (comType.equals("req_file_list")) {
            ByteBuf outBuf = Unpooled.copiedBuffer(createFileList(msgOwner));
            ctx.writeAndFlush(outBuf);
        } else if (comType.equals("res_file_list")) {
            if (fileListModel != null) {
                fileListModel.clear();
                String[] list = dataStr.split("-");
                for (String str : list) {
                    fileListModel.addElement(str);
                }
            }
        } else if (comType.equals("fileName")) {
            currentFilename = dataStr;
        } else if (comType.equals("get_fileName")) {
            ByteBuf outBuf2 = Unpooled.copiedBuffer(ByteBufService.prepareSendData("fileName", owner, data));
            ctx.writeAndFlush(outBuf2);
            chunkService.sendFile(ctx.channel(), rootPath.toString() + File.separator + msgOwner + File.separator + dataStr);
        }
        return currentFilename;
    }

    private byte[] createFileList(String msgOwner) {
        Path pathToFind = Paths.get(rootPath.toString() + File.separator + msgOwner + File.separator);
        if (!pathToFind.toFile().exists()) {
            pathToFind.toFile().mkdir();
        }
        final StringBuilder sb = new StringBuilder();

        try {
            Files.walkFileTree(pathToFind, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    sb.append(file.getFileName().toString()).append("-");
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ByteBufService.prepareSendData("res_file_list", msgOwner, sb.toString().getBytes());
    }
}
