package pro.antonshu.client.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.stream.ChunkedWriteHandler;
import pro.antonshu.handlers.JointHandler;

import javax.net.ssl.SSLException;
import javax.swing.*;
import java.net.InetSocketAddress;

public class NettyBFClient {

    private Channel channel;
    private static final boolean SSL = System.getProperty("ssl") != null;
    private static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8992" : "8023"));

    public void run(DefaultListModel<String> windowFileList) throws InterruptedException, SSLException {
        final SslContext sslCtx;

        if (SSL) {
            sslCtx = SslContextBuilder.forClient().build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap clientBootstrap = new Bootstrap();

            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.remoteAddress(new InetSocketAddress("localhost", PORT));
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    if (sslCtx != null) {
                        socketChannel.pipeline().addLast(sslCtx.newHandler(socketChannel.alloc()));
                    }
                    socketChannel.pipeline().addLast(
                            new ChunkedWriteHandler(),
                            new JointHandler("Client", windowFileList));
                    channel = socketChannel;
                }
            });

            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            System.out.println("Client start at port: " + PORT);
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public void closeConnection() {
        channel.close();
    }

}
