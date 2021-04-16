import java.util.*;
import org.json.*;
import java.io.*;
import java.net.*;

public class Client {
  private static String address = "127.0.0.1";
  private static String port = "8000";

  private static Socket clientSocket;
  private static PrintWriter out;
  private static BufferedReader in;


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

  public static void main(String[] args) throws IOException {
    Scanner scanner = new Scanner(System.in);

    ////Registration of players
    //tcp-connect
    int portNum = Integer.parseInt(port);
    connect(address, portNum);


    //get server-welcome message
    JSONObject welcomeMSG = receiveJSONResponse();

    //get "name" message
    String welcomeName = receiveStringResponse();

    //send name message
    String name = scanner.next();
    sendStringMessage(name);



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
