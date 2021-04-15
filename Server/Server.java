import org.json.JSONObject;

import java.io.*;
import java.net.*;

public class Server {
  private static ServerSocket server;
  private static Socket socket;
  private static PrintWriter out;
  private static BufferedReader in;
  private static String jsonNum;
  private static StringBuilder input = new StringBuilder();

  public static void start(int port) {
    try {
      server = new ServerSocket(port);
      socket = server.accept();
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    catch(IOException i)
    {
      System.out.println(i);
    }
  }

  // stop the connection and close socket, and input & output streams
  public static void stop() throws IOException {
    in.close();
    out.close();
    socket.close();
    server.close();
  }

  public static void sendJSONMessage(JSONObject jo) {
    out.println(jo);
  }

  public static String receiveStringMessage() throws IOException {
    return in.readLine();
  }

  public static JSONObject readJSONMessage() throws IOException {
    return new JSONObject(in.readLine());
  }

  //Registration of players
  private static void registration() throws IOException {
    JSONObject welcomeMsg = new JSONObject();
    welcomeMsg.put("type", "welcome");
    welcomeMsg.put("info", "Snarl Game");
    sendJSONMessage(welcomeMsg);
    String playerName = receiveStringMessage();
  }

  public static void main(String[] args) throws IOException {
    Server server = new Server();
    server.start(8000);
    registration();



    stop();
  }

}
