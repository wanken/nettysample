import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author waver
 * @date 2019.3.8 16:21
 */

public class EchoSever extends EchoSeverHandler {

    private final int port;

    private EchoSever(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.err.println("Usage: " + EchoSever.class.getSimpleName() + " <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        EchoSever echoClient = new EchoSever(port);
        echoClient.start();
    }

    private void start() throws InterruptedException {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(eventExecutors)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoSeverHandler());
                        }
                    });
            ChannelFuture sync = serverBootstrap.bind().sync();
            System.out.println(EchoSever.class.getName() + "start and listen on " + sync.channel().localAddress());
            sync.channel().closeFuture().sync();
        }finally {
            eventExecutors.shutdownGracefully().sync();
        }
    }

}
