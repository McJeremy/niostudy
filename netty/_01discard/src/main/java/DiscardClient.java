import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by zhize.xu on 2018/1/24 0024.
 *
 * @since 1.0
 */
public class DiscardClient {
    public void run() throws Exception
    {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b= new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception
                        {
                            ch.pipeline().addLast(new DiscardClientHandler());
                        }
                    });
            ChannelFuture f = b.connect("127.0.0.1",8080).sync();
//            f.channel().closeFuture().sync();
            Channel c = f.channel();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                c.writeAndFlush(in.readLine()+"\r\n");
            }
        }
        finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception
    {
        new DiscardClient().run();
    }
}
