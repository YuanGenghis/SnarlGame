import org.json.JSONArray;
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


    private static String fileName = "snarl.levels";
    private static int timeOfWaiting = 6;
    private static boolean isObserverMode = false;
    private static int port = 45678;

    private static int minPlayers = 1;
    private static int maxPlayers = 4;

    private static List<String> names = new ArrayList<>();
    private static List<Socket> playerSockets = new ArrayList<>();
    private static GameManager gm = null;
//  private static User user;

    public Server(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server started");
            System.out.println("Waiting for a client ...");
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    // run the game
    public static void run() throws IOException, InterruptedException {
        waitForPlayers(minPlayers);
        System.out.println("Client accepted");
        System.out.println("Closing connection");

        try {
            server.setSoTimeout(timeOfWaiting * 1000);
            waitForPlayers(maxPlayers - minPlayers);
        } catch (SocketTimeoutException s) {
            System.out.println("timeout for more players");
        }
        gm = new GameManager(names);
        gm.init();
        sendInitialUpdate();
        startGame();

//    socket.close();
//    in.close();
    }

    private static void sendInitialUpdate() throws IOException {
        for (int ii = 0; ii < playerSockets.size(); ++ii) {
            out = new PrintWriter(playerSockets.get(ii).getOutputStream(), true);
            JSONObject playerUpdate = makePlayerUpdateMessage(gm.players.get(ii));
            sendJSONMessage(playerUpdate);
        }

    }

    private static JSONObject makePlayerUpdateMessage(Player p) {
        JSONObject msg = new JSONObject();
        //type
        msg.put("type", "player-update");

        //tile layout
        int[][] tiles = gm.getViewOfPlayer(p, p.getPosition());
        JSONArray tileLayout = new JSONArray();
        for (int[] tile : tiles) {
            JSONArray row = new JSONArray();
            for (int i : tile) {
                row.put(i);
            }
            tileLayout.put(row);
        }
        msg.put("layout", tileLayout);

        //position
        msg.put("position", p.position);

        //objects
        JSONArray objectList = new JSONArray();
        JSONObject keyObj = new JSONObject();
        keyObj.put("type", "key");
        keyObj.put("position", gm.gameState.levels.get(gm.gameState.curLevel).keyPosition);
        objectList.put(keyObj);
        JSONObject exitObj = new JSONObject();
        exitObj.put("type", "exit");
        exitObj.put("position", gm.gameState.levels.get(gm.gameState.curLevel).exitPosition);
        objectList.put(exitObj);
        msg.put("objects", objectList);

        //actors
        JSONArray actorList = new JSONArray();
        for (int ii = 0; ii < gm.players.size(); ++ii) {
            JSONObject playerObj = new JSONObject();
            playerObj.put("type", "player");
            playerObj.put("name", gm.players.get(ii).name);
            playerObj.put("position", gm.players.get(ii).getPosition());
            actorList.put(playerObj);
        }
        List<Adversary> ads = gm.gameState.levels.get(gm.gameState.curLevel).ads;
        for (int ii = 0; ii < ads.size(); ++ii) {
            JSONObject adObj = new JSONObject();
            adObj.put("type", ads.get(ii).getType());
            adObj.put("name", ads.get(ii).getType() + ii);
            adObj.put("position", ads.get(ii).getPosition());
            actorList.put(adObj);
        }
        msg.put("actors", actorList);


        // TODO: add described information
        // message: maybe-string
        String message = "Player " + p.getName() + " ";
        msg.put("message", message);

        return msg;
    }

    private static void startGame() throws IOException, InterruptedException {
//        while (gm.isGameEnd()) {
            for (int ii = 0; ii < playerSockets.size(); ++ii) {
                Socket s = playerSockets.get(ii);
                Player player = gm.players.get(gm.curPlayer);
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new PrintWriter(s.getOutputStream(), true);
                sendStringMessage("move");
                JSONObject playerMove = null;
                while ((playerMove = receiveJSONMessage()) != null) {
                    System.out.println(playerMove);
                    int[] dst = new int[]{playerMove.getJSONArray("to").getInt(0),
                            playerMove.getJSONArray("to").getInt(1)};
                    sendStringMessage(gm.checkMoveResult(player, dst));
                    gm.movePlayer(player, dst);
                    JSONObject updateMsg = makePlayerUpdateMessage(player);
                    sendJSONMessage(updateMsg);
                }



                System.out.println("I'm so tired :(");
            }
//        }
    }

    // wait for min players to register
    private static void waitForPlayers(int minPlayers) throws IOException {
        for (int i = 0; i < minPlayers; i++) {
            Socket s = server.accept();
            System.out.println("new Client connected to " + s);
            playerSockets.add(s);

            System.out.println("wait for a username");
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

            String line = receiveStringMessage();
            names.add(line);
            System.out.println(names);
        }
    }


    // stop the connection and close socket, and input & output streams
    public static void stop() throws IOException {
        in.close();
        out.close();
        socket.close();
        server.close();
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--levels")) {
                fileName = args[++i];
                System.out.println("filename: " + fileName);
            } else if (args[i].equals("--clients")) {
                int num = Integer.parseInt(args[++i]);
                if (num <= 4 && num >= 1) {
                    maxPlayers = num;
                    System.out.println("maxPlayers: " + maxPlayers);
                } else {
                    System.out.println("numberOfPlayer should be 1 <= N <= 4");
                }
            } else if (args[i].equals("--wait")) {
                timeOfWaiting = Integer.parseInt(args[++i]);
                System.out.println("timeOfWaiting: " + timeOfWaiting);
            } else if (args[i].equals("--observe")) {
                isObserverMode = true;
                System.out.println("ObserverMode: " + isObserverMode);
            } else if (args[i].equals("--port")) {
                port = Integer.parseInt(args[++i]);
                System.out.println("port: " + port);
            }
        }

            Server server = new Server(port);
            run();
//        for (Socket s : playerSockets) {
//            stop();
//        }
        }


        public static void sendJSONMessage (JSONObject jo){
            out.println(jo);
        }

        public static void sendStringMessage (String msg){
            out.println(msg);
        }

        public static String receiveStringMessage () throws IOException {
            return in.readLine();
        }

        public static JSONObject receiveJSONMessage () throws IOException {
            return new JSONObject(in.readLine());
        }

        private static void runGame (List < String > names) {

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

// get the output stream from the socket.
//      OutputStream outputStream = s.getOutputStream();
//      // create an object output stream from the output stream so we can send an object through it
//      ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
//      objectOutputStream.writeObject(gm);
