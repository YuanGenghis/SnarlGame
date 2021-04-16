import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Server {
  private static ServerSocket server;
  private static Socket socket;
  private static PrintWriter out;
  private static BufferedReader in;
  private static int maxPlayers = 4;
  private static int minPlayers = 1;
  private static List<String> names = new ArrayList<>();

  private static List<Socket> playerSockets = new ArrayList<>();
  private static User user;

  public static void start(int port) throws IOException {
    server = new ServerSocket(port);

    //wait for minimum number of player
    registration(port, minPlayers);

    //wait for more players until timeout
    socket.setSoTimeout(5);
    try {
      registration(port, maxPlayers - minPlayers);
    }
    catch (SocketTimeoutException s) {
      System.out.println("timeout!");
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
  private static void registration(int port, int playerAmount) throws IOException {
    for (int ii = 0; ii < playerAmount; ++ii) {
      socket = server.accept();
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      JSONObject welcomeMsg = new JSONObject();
      welcomeMsg.put("type", "welcome");
      welcomeMsg.put("info", "Snarl Game");
      sendJSONMessage(welcomeMsg);
      String playerName = receiveStringMessage();
      names.add(playerName);
      playerSockets.add(socket);
    }
  }

  public static void main(String[] args) throws IOException {
    Server server = new Server();
    server.start(8000);
    runGame(names);

    for (Socket s: playerSockets) {
      stop();
    }
  }





  private static void runGame(List<String> names) {
    String fileName = "snarl.levels";
    int naturalNum = 0;
    StringBuilder jsonFile = new StringBuilder();
    List<Level> levels = new ArrayList<>();
    try {
      File myObj = new File(fileName);
      Scanner myReader = new Scanner(myObj);
      naturalNum = myReader.nextInt();

      int index = 0;
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        if (data.contains("level")) {
          if (index != 0) {
            String jsonString = jsonFile.toString();
            JSONObject jo = new JSONObject(jsonString);
            Level l = TestLevel.levelBuilder(jo);
            levels.add(l);

            jsonFile = new StringBuilder();
          }
          ++index;
        }
        jsonFile.append(data);
      }
      String jsonString = jsonFile.toString();
      JSONObject jo = new JSONObject(jsonString);
      Level l = TestLevel.levelBuilder(jo);
      levels.add(l);

      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    User user1 = new User(naturalNum, levels, names);
    user1.render();
  }

}
