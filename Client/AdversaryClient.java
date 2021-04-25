import java.util.*;
import org.json.*;
import java.io.*;
import java.net.*;

public class AdversaryClient {
    private static String address = "127.0.0.1";
    private static int port = 45678;
    private static String type = "zombie";

    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;



    //send a string to server
    public static void sendStringMessage(String msg) throws IOException {
        out.println(msg);
    }

    //send a JSONObject to server
    public static void sendJSONMessage(JSONObject jo) throws IOException {
        System.out.println("send json");
        out.println(jo);
    }

    public static JSONObject receiveJSONResponse() throws IOException {
        JSONObject resp = new JSONObject(in.readLine());
        return resp;
    }

    public static String receiveStringResponse() throws IOException {
        String resp = in.readLine();
        return resp;
    }

    public static Object receiveMsg() throws IOException {
        return in.readLine();
    }

    //build connection
    public static void connect(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        // update ip address / port according to arguments
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--address")) {
                address = args[++i];
                System.out.println("address: " + address);
            } else if (args[i].equals("--port")) {
                port = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--ghost")) {
                type = "ghost";
            }
        }

        Scanner scanner = new Scanner(System.in);

        ////Registration of players
        //tcp-connect
        connect(address, port);


        //get server-welcome message
        JSONObject welcomeMsg = receiveJSONResponse();
        System.out.println(welcomeMsg);

        //get "name" message
        String welcomeName = receiveStringResponse();
        System.out.println(welcomeName);

        //send name message
        String name = scanner.next();
        sendStringMessage("zombie" + name);

        //start a level and get updated message
        JSONObject updateMsg = receiveJSONResponse();
        System.out.println(updateMsg);

        AdversaryUser user = new AdversaryUser(updateMsg);
        user.render();

        //keep updating
        Object msg = null;
        while ((msg = in.readLine()) != null) {
            msg = new JSONTokener(msg.toString()).nextValue();
            System.out.println("msg:" + msg);
            if (msg instanceof String && msg.equals("move")) {
                System.out.println(msg);
                msg = receiveJSONResponse();
                System.out.println(msg);
                user.setAdversaryUpdateMessage((JSONObject) msg);

                int[] dst = user.getAdMove();
                if (dst != null) {
                    JSONObject dstObj = new JSONObject();
                    dstObj.put("to", dst);

                    sendJSONMessage(dstObj);
                    Thread.sleep(100);
                }
                user.setMoveAmount();
            } else if (((JSONObject)msg).get("type").equals("ad-update")) {
                System.out.println("update message");
                user.setAdversaryUpdateMessage((JSONObject) msg);
            } else {
                System.out.println(msg);
            }

            Thread.sleep(100);
        }
    }
}
