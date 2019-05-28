package com.example.util.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketDemo {
    private static ServerSocket serverSocket;

    static {
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        while (true) {
            Socket accept = serverSocket.accept();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
            System.out.println(bufferedReader.readLine());
        }
    }
}
