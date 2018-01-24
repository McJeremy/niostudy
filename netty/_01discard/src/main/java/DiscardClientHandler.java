import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by zhize.xu on 2018/1/24 0024.
 *
 * @since 1.0
 */
public class DiscardClientHandler extends ChannelHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        ctx.writeAndFlush(Unpooled.copiedBuffer(("client send hello ").getBytes()));

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf in = (ByteBuf) msg;
        try {
            // Do something with msg
            System.out.println("client get :" + in.toString(CharsetUtil.UTF_8));

            ctx.close();
        } finally {
            //ByteBuf是一个引用计数对象，这个对象必须显示地调用release()方法来释放
            //or ((ByteBuf)msg).release();
            ReferenceCountUtil.release(msg);
        }
    }
}
