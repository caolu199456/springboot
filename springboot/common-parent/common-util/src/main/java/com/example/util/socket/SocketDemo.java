package com.example.util.socket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketDemo {
    public static void main(String[] args) {

        try {
            Socket socket = new Socket("127.0.0.1", 8080);

            OutputStream stream = socket.getOutputStream();

            PrintWriter pw = new PrintWriter(stream);
            pw.println("你好");
            pw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
