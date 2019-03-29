import java.io.*;
import java.net.*;
import java.sql.SQLOutput;

public class MyProxyServer {

    private final String USER_AGENT = "Mozilla/5.0";

    public static final int portNumber = 8888;

    public static void main(String[] args) throws Exception {
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

    private void sendGet(String url, OutputStream destination) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);


        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine).append("\r\n");
        }
        in.close();
        destination.write(response.toString().getBytes());
        destination.flush();
        //print result
        System.out.println(response.toString());

    }

    public void start() throws Exception {
        System.out.println("Starting the MyProxyServer ...");
        try {

            ServerSocket serverSocket = new ServerSocket(MyProxyServer.portNumber, 1);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                StringBuffer request = new StringBuffer();
                String mURL;
                line = bufferedReader.readLine();
                request.append(line).append("\r\n");
                String[] requestLineComponents = line.split(" ");
                mURL = requestLineComponents[1];

                while ((line = bufferedReader.readLine()) != null) {
                    if (line.equals(""))
                        break;
                    request.append(line).append("\r\n");
                }



                System.out.println(request.toString() + "\n" + "\n");
                String realUrl = mURL.split("://")[1];
                realUrl = realUrl.substring(0, realUrl.length() - 1);
                sendGet(mURL, clientSocket.getOutputStream());
//                InetAddress address = InetAddress.getByName(realUrl);
//                Socket desSocket = new Socket(address, 64122);
//                desSocket.getOutputStream().write(request.toString().getBytes());
//
//                BufferedReader resReader = new BufferedReader(new InputStreamReader(desSocket.getInputStream()));
//                StringBuilder response = new StringBuilder();
//                while (true) {
//                    line = resReader.readLine();
//                    if (line.equals(""))
//                        break;
//
//                    response.append(line).append("\r\n");
//                }
//
//                System.out.println(response);
                //while ((requestFromClient = bufferedReader.readLine()) != null) {
                //    System.out.println(requestFromClient);
                //}
                //inputStreamReader.close();


//                sendGet(mURL, clientSocket.getOutputStream());
//                ---------Handled check blacklist
//                String[] words = requestFromClient.split("\\s");
//                String mURL = words[1];
                //clientSocket.close();


//                System.out.println(mURL);
//                if (checkBlacklist(mURL)) {
//                    System.out.println(mURL + " đã tồn tại trong blacklist");
//                } else {
//                    System.out.println("Được phép truy cập");
//                }
            }



        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}