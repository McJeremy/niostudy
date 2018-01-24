package netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhize.xu on 2018/1/24 0024.
 *
 * @since 1.0
 */
public class MyServerHandler extends SimpleChannelInboundHandler<String> {

    private static AtomicInteger counter = new AtomicInteger(0);
    private static Map<Channel, Integer> channels = new ConcurrentHashMap<Channel, Integer>();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws UnknownHostException {
        Integer channelNum = counter.incrementAndGet();
        channels.put(ctx.channel(), channelNum);
        ctx.writeAndFlush("Hello user-" + channelNum + "\r\n");
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        // Send the received message to all channels but the current one.
        for (Map.Entry<Channel, Integer> entry : channels.entrySet()) {
            Channel c = entry.getKey();
            if (c != ctx.channel()) {
                c.writeAndFlush("user-" + channels.get(ctx.channel()) + " said: " + msg + '\n');
            } else {
                c.writeAndFlush("[you] said: " + msg + '\n');
            }
        }

        // Close the connection if the client has sent 'bye'.
        if ("bye".equals(msg.toLowerCase())) {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
