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

    public boolean checkBlacklist(String mUrl) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/Users/trietnguyen/Desktop/Simple-HTTP-Proxy/MultiThread_HTTP_Proxy/blacklist.conf"));

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

    public void run() {
        try {
            while(true) {
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

                if (checkBlacklist(mHost)) {
                    DataOutputStream mResponseToClient = new DataOutputStream(mClientSocket.getOutputStream());
                    String line2 = "HTTP/1.1 403 Access Forbidden\r\n\r\n" + "<!DOCTYPE html>\n" +
                            "<html lang=\"en\">\n" +
                            "<head>\n" +
                            "    <meta charset=\"UTF-8\">\n" +
                            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                            "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n" +
                            "    <title>403 Access Forbidden</title>\n" +
                            "    <style> \n" +
                            "        .centered {\n" +
                            "            position: fixed;\n" +
                            "            top: 50%;\n" +
                            "            left: 50%;\n" +
                            "            color: red;\n" +
                            "            font-size: 80px;\n" +
                            "            font-weight: 700;\n" +
                            "            /* bring your own prefixes */\n" +
                            "            transform: translate(-50%, -50%);\n" +
                            "        }    \n" +
                            "    </style>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "    <div class=\"centered\">\n" +
                            "        403 Access Forbidden\n" +
                            "      </div>\n" +
                            "</body>\n" +
                            "</html>";

                    mResponseToClient.writeBytes(line2.toString());
                    mResponseToClient.flush();
                    mClientSocket.close();

                } else {
//                print to console Domain name & http request
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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
