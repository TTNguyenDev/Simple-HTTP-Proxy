import java.net.*;
import java.io.*;

public class ProxyServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        int port = 8888;	//default

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Started on: " + port);
        } catch (IOException e) {
            System.exit(-1);
        }

        while (listening) {
            new Handler(serverSocket.accept()).start();
        }
        serverSocket.close();
    }
}

