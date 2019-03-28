

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MyProxyServer {

    public static final int portNumber = 8888;

    public static void main(String[] args) {
        MyProxyServer myProxyServer = new MyProxyServer();
        myProxyServer.start();
    }


    //return true when url exist in blacklist else return false
    public boolean checkBlacklist(String mUrl) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/Users/trietnguyen/Desktop/Simple-HTTP-Proxy/httpProxy/blacklist.conf"));

            String textInALine;

            while ((textInALine = br.readLine()) != null) {
                if (mUrl.equals(textInALine))
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void start() {
        System.out.println("Starting the MyProxyServer ...");
        try {

            ServerSocket serverSocket = new ServerSocket(MyProxyServer.portNumber, 1);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String requestFromClient;
                int i = 0;
                while (i<8) {
                    requestFromClient = bufferedReader.readLine();
                    System.out.println(requestFromClient);
                    i++;
                }
                bufferedReader.close();

                //---------Handled check blacklist
//                String[] words = requestFromClient.split("\\s");
//                String mURL = words[1];



//                System.out.println(mURL);
//                if (checkBlacklist(mURL)) {
//                    System.out.println(mURL + " đã tồn tại trong blacklist");
//                } else {
//                    System.out.println("Được phép truy cập");
//                }
            }



        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}