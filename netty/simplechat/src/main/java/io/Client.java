package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by zhize.xu on 2018/1/24 0024.
 *
 * @since 1.0
 */
public class Client {

    public static void main(String[] args) throws Throwable
    {
        Socket socket = new Socket("127.0.0.1",8888);
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            new Thread(new ServerResponseHandler(in)).start();
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String msg = "";
            while((msg = userInput.readLine())!=null){
                pw.println(msg);
                pw.flush();
            }
        }
        finally {
            socket.close();
        }
    }

    private  static  class ServerResponseHandler implements Runnable
    {
        BufferedReader in ;
        public ServerResponseHandler(BufferedReader in){
            this.in = in;
        }
        public void run()
        {
            try {
                String msg = "";
                while((msg=in.readLine())!="")
                {
                    System.out.println(msg);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
