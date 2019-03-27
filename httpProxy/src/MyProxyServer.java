

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MyProxyServer {

    public static final int portNumber = 8888;

    public static void main(String[] args) {
        MyProxyServer myProxyServer = new MyProxyServer();
        myProxyServer.start();
    }
    public void start() {
        System.out.println("Starting the MyProxyServer ...");
        try {

            ServerSocket serverSocket = new ServerSocket(MyProxyServer.portNumber, 1);

            while (true) {
                System.out.println("Inside while loop ");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection to MyProxyServer is " + clientSocket.isConnected());

                InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String command = bufferedReader.readLine();

                bufferedReader.close();
                
                
                
                //Handled check blacklist
                String[] words = command.split("\\s");
                String mURL = words[1];

                System.out.println(mURL);
                
                

                System.out.println("Client has asked to ....\n" + command);

                if (command.equals("Cancel")) {
                    System.out.println("Shutting down the server ...");
                    break;
                }


                /*printStream.close();
                fileOutputStream.close();*/
            }



        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}