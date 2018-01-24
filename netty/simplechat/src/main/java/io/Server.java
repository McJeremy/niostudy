package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhize.xu on 2018/1/24 0024.
 *
 * @since 1.0
 */
public class Server {
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static Map<Integer,PrintWriter> clients = new ConcurrentHashMap<Integer, PrintWriter>();
    private static ExecutorService pool = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Throwable
    {
        ServerSocket serverSocket = new ServerSocket(8888);
        try {
            while(true)
            {
                Socket socket = serverSocket.accept();
                System.out.println("a client connected...");

                //socket.getInputStream()方法会导致程序阻塞，直到inputStream收到对方发过来的报文消息，程序才会继续往下执行
                //所以，采用线程来处理
                pool.execute(new ClientSocketHandler(socket));

            }
        }
        finally {
            serverSocket.close();
        }
    }

    private static class ClientSocketHandler implements Runnable
    {
        private Socket socket;
        private Integer id;
        public ClientSocketHandler(Socket socket)
        {
            this.socket = socket;
            this.id = counter.incrementAndGet();
        }

        public void run(){
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                clients.put(id,out);
                System.out.println("client num: " + clients.size());
                String msg = "";
//                readline只有读取文件等的结尾才会是null
//                在网络上（都是阻塞模式），readline是一直等待输入，
//                即使是对方什么也不输入，只是回车，readline也不会返回null
                while((msg = in.readLine())!=null)
                {
                    System.out.println("receive msg '" + msg + "' from client-" + id);
                    sendBack2Client("cllient-" + id + " said : " + msg);
                    out.flush();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        private static void sendBack2Client(String msg){
            for(PrintWriter client : clients.values()){
                client.println(msg);
                client.flush();
            }
        }
    }
}
