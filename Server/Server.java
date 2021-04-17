import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Server {
  private static ServerSocket server = null;
  private static Socket socket = null;
  //    private static DataInputStream in = null;
  private static BufferedReader in;
  private static PrintWriter out = null;

  private static int minPlayers = 1;
  private static int maxPlayers = 4;

  private static List<String> names = new ArrayList<>();
  private static List<Socket> playerSockets = new ArrayList<>();
  private static GameManager gm = null;
  private static User user;

  public Server(int port) {
    try {
      server = new ServerSocket(port);
      System.out.println("Server started");
      System.out.println("Waiting for a client ...");
    } catch (IOException i) {
      System.out.println(i);
    }


  }

  public static void run() throws IOException {
      waitForPlayers(minPlayers);
      System.out.println("Client accepted");
      System.out.println("Closing connection");

      try {
        server.setSoTimeout(5 * 1000);
        waitForPlayers(maxPlayers - minPlayers);
      } catch (SocketTimeoutException s) {
        System.out.println("timeout for more players");
      }
      gm = new GameManager(names);
      startGame();

//      runGame(names);

      //            // close connection
      //            socket.close();
      //            in.close();
  }

  private static void startGame() throws IOException {
    for (Socket s : playerSockets) {
      out = new PrintWriter(s.getOutputStream(), true);
      // get the output stream from the socket.
      OutputStream outputStream = s.getOutputStream();
      // create an object output stream from the output stream so we can send an object through it
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
      objectOutputStream.writeObject(gm);
      System.out.println("I'm so tired :(");
    }
//    while (true) {
//      for (Socket s : playerSockets) {
//        out = new PrintWriter(s.getOutputStream(), true);
//        // get the output stream from the socket.
//        OutputStream outputStream = s.getOutputStream();
//        // create an object output stream from the output stream so we can send an object through it
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
//        objectOutputStream.writeObject(gm);
//        System.out.println("I'm so tired :(");
//      }
//    }
  }

  // wait for min players to register
  private static void waitForPlayers(int minPlayers) throws IOException {
    for (int i = 0; i < minPlayers; i++) {
      Socket s = server.accept();
      System.out.println("new Client connected to " + s);
      playerSockets.add(s);

      System.out.println("input a username");
      out = new PrintWriter(s.getOutputStream(), true);
      // takes input from the client socket
      in = new BufferedReader(new InputStreamReader(s.getInputStream()));

      JSONObject welcomeMsg = new JSONObject();
      welcomeMsg.put("type", "welcome");
      welcomeMsg.put("info", "Snarl Game");
      //send server-welcome json
      sendJSONMessage(welcomeMsg);
      //send "name"
      sendStringMessage("name");
//            String playerName = receiveStringMessage();
//            System.out.println(playerName);
//            names.add(playerName);
//            playerSockets.add(socket);

      String line = in.readLine();
      names.add(line);
      System.out.println(names);
    }
  }

//    public static void start(int port) throws IOException {
//
//        //wait for minimum number of player
//        registration(port, minPlayers);
//
//        //wait for more players until timeout
//        socket.setSoTimeout(60);
//        try {
//            registration(port, maxPlayers - minPlayers);
//        } catch (SocketTimeoutException s) {
//            System.out.println("timeout!");
//        }
//    }

  // stop the connection and close socket, and input & output streams
  public static void stop() throws IOException {
    in.close();
    out.close();
    socket.close();
    server.close();
  }


  public static void main(String[] args) throws IOException {
    Server server = new Server(8000);
    run();
//        for (Socket s : playerSockets) {
//            stop();
//        }
  }


  public static void sendJSONMessage(JSONObject jo) {
    out.println(jo);
  }

  public static void sendStringMessage(String msg) {
    out.println(msg);
  }

  public static String receiveStringMessage() throws IOException {
    return in.readLine();
  }

  public static JSONObject readJSONMessage() throws IOException {
    return new JSONObject(in.readLine());
  }

  private static void runGame(List<String> names) {

    User user1 = new User(gm);
    user1.render();
  }
}

//  private static void runGame(List<String> names) {
//    String fileName = "1-in.levels";
//    int naturalNum = 0;
//    StringBuilder jsonFile = new StringBuilder();
//    List<Level> levels = new ArrayList<>();
//    try {
//      File myObj = new File(fileName);
//      Scanner myReader = new Scanner(myObj);
//      naturalNum = myReader.nextInt();
//
//      int index = 0;
//      while (myReader.hasNextLine()) {
//        String data = myReader.nextLine();
//        if (data.contains("level")) {
//          if (index != 0) {
//            String jsonString = jsonFile.toString();
//            JSONObject jo = new JSONObject(jsonString);
//            Level l = TestLevel.levelBuilder(jo);
//            levels.add(l);
//
//            jsonFile = new StringBuilder();
//          }
//          ++index;
//        }
//        jsonFile.append(data);
//      }
//      String jsonString = jsonFile.toString();
//      JSONObject jo = new JSONObject(jsonString);
//      Level l = TestLevel.levelBuilder(jo);
//      levels.add(l);
//
//      myReader.close();
//    } catch (FileNotFoundException e) {
//      System.out.println("An error occurred.");
//      e.printStackTrace();
//    }
//    User user1 = new User(naturalNum, levels, names);
//    user1.render();
//  }
//}



//Registration of players
//    private static void registration(int port, int playerAmount) throws IOException {
//        for (int ii = 0; ii < playerAmount; ++ii) {
//            socket = server.accept();
//            out = new PrintWriter(socket.getOutputStream(), true);
//            in = new DataInputStream(new InputStreamReader(socket.getInputStream()));
//
//            JSONObject welcomeMsg = new JSONObject();
//            welcomeMsg.put("type", "welcome");
//            welcomeMsg.put("info", "Snarl Game");
//            //send server-welcome json
//            sendJSONMessage(welcomeMsg);
//            //send "name"
//            sendStringMessage("name");
//            String playerName = receiveStringMessage();
//            System.out.println(playerName);
//            names.add(playerName);
//            playerSockets.add(socket);
//        }
//    }

