import javax.xml.crypto.Data;
import java.net.*;
import java.io.*;
import java.util.*;

public class Handler extends Thread {
    private Socket mClientSocket = null;
    private static final int BUFFER_SIZE = 100000;


    public Handler(Socket socket) {
        super("ProxyThread");
        this.mClientSocket = socket;
    }

    public void run() {
        try {
            while (true) {

                //get Request from browser
                BufferedReader mClientRequest = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
                String line;
                StringBuilder mClientRequestMessage = new StringBuilder();
                String url = new String();

                while (true) {
                    line = mClientRequest.readLine();
                    if (mClientRequestMessage.length() == 0) {
                        url = line.split(" ")[1];
                    } else if (line.equals("")) {
                        mClientRequestMessage.append("\r\n");
                        break;
                    }
                    mClientRequestMessage.append(line).append("\r\n");
                }

                URL mUrl = new URL(url);
                String mHost = mUrl.getHost();

                //print to console Domain name & http request
                System.out.println("URL: " + url);
                System.out.println(mClientRequestMessage);

                //forward request to server
                Socket mServerSocket = new Socket(InetAddress.getByName(mHost), 80);
                DataOutputStream mForwardMessageWriter = new DataOutputStream((mServerSocket.getOutputStream()));
                mForwardMessageWriter.writeBytes(mClientRequestMessage.toString());
                mForwardMessageWriter.flush();

                //get response message from server
                InputStream mResponseFromServer = mServerSocket.getInputStream();
                DataOutputStream mResponseToClient = new DataOutputStream(mClientSocket.getOutputStream());
                //begin send response to client
                byte[] buffer = new byte[BUFFER_SIZE];
                int read = 0;
                while ((read = mResponseFromServer.read(buffer)) > 0) {
                    mResponseToClient.write(buffer, 0, read);
                    mResponseToClient.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
