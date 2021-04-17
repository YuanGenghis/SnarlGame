import java.util.*;
import org.json.*;
import java.io.*;
import java.net.*;

public class Client {
  private static String address = "127.0.0.1";
  private static int port = 45678;

  private static Socket clientSocket;
  private static PrintWriter out;
  private static BufferedReader in;
  private static User user = null;



  //send a string to server
  public static void sendStringMessage(String msg) throws IOException {
    out.println(msg);
  }

  //send a JSONObject to server
  public static void sendJSONMessage(JSONObject jo) throws IOException {
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

  //build connection
  public static void connect(String ip, int port) throws IOException {
    clientSocket = new Socket(ip, port);
    out = new PrintWriter(clientSocket.getOutputStream(), true);
    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
  }

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    // update ip address / port according to arguments
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--address")) {
        address = args[++i];
        System.out.println("address: " + address);
      } else if (args[i].equals("--port")) {
        port = Integer.parseInt(args[++i]);
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
    sendStringMessage(name);

    //get updated message
    JSONObject updateMsg = receiveJSONResponse();
    System.out.println(updateMsg);


    // while (true)
      //get "move" message
    String moveMsg = receiveStringResponse();
    System.out.println(moveMsg);

    if (moveMsg.equals("move")) {


    }



//
//    // get the input stream from the connected socket
//    InputStream inputStream = clientSocket.getInputStream();
//    // create a DataInputStream so we can read data from it.
//    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
//
//    GameManager gm = (GameManager) objectInputStream.readObject();
//    user = new User(gm);
//    user.render();




    ////Starting a level
    //get level-start msg

    //get init updates




    ////Playing a round
    //get "move" msg

    //send player move to server

    //get result

    //get update msg




    ////Ending a level





    ////Ending a Snarl game




  }

}
