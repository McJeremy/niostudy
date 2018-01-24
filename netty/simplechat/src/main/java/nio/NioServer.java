package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by zhize.xu on 2018/1/24 0024.
 *
 * @since 1.0
 */
public class NioServer {
    private static Selector selector;
    private Set<SelectionKey> selectionKeys = null;
    private Iterator<SelectionKey> iterator = null;
    private Iterator<SocketChannel> iterator2 = null;
    private List<SocketChannel> clients = new ArrayList<SocketChannel>();

    static {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Throwable {
        new NioServer().start();
    }

    public void start() throws Exception {
        initServerSocketChannel();
        while (true) {
            int ready = selector.select();
            if (ready > 0) {
                selectionKeys = selector.selectedKeys();
                iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isAcceptable()) {
                        acceptClient(selectionKey);
                    } else if (selectionKey.isReadable()) {
                        readMsg(selectionKey);
                    }
                    iterator.remove();
                }
            }
        }
    }

    void initServerSocketChannel() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8889));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void acceptClient(SelectionKey selectionKey) throws Exception {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        clients.add(socketChannel);
        System.out.println("a client connected ...");
    }

    private void readMsg(SelectionKey selectionKey) throws Exception{
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
        buffer.clear();
        socketChannel.read(buffer);
        buffer.flip();
        iterator2 = clients.iterator();
        SocketChannel socketChannel2 = null;
        while(iterator2.hasNext()){
            socketChannel2 = iterator2.next();
            while(buffer.hasRemaining()){
                socketChannel2.write(buffer);
            }
            buffer.rewind();
        }
    }
}
