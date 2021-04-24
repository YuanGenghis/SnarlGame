import org.json.JSONArray;
import org.json.JSONObject;

import sun.security.jgss.spnego.SpNegoContext;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Server3 {
    private static ServerSocket server = null;
    private static Socket socket = null;
    //    private static DataInputStream in = null;
    private static BufferedReader in;
    private static PrintWriter out = null;


    private static String fileName = "1-in.levels";
    private static int timeOfWaiting = 6;
    private static boolean isObserverMode = false;
    private static int port = 45678;

    private static int minPlayers = 1;
    private static int maxPlayers = 4;

    private static int maxAdversary = 2;

    private static List<String> names = new ArrayList<>();
    private static List<String> adNames = new ArrayList<>();
    private static List<Socket> playerSockets = new ArrayList<>();
    private static List<Socket> adversarySockets = new ArrayList<>();

    private static GameManager gm = null;
    private static String whoFindTheFuckingKey = "";
    private static String whoFindTheExit = "";
//  private static User user;

    public Server3(int port) {
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

        try {
            System.out.println("Time to add Adversary");
            server.setSoTimeout(10 * 1000);
            waitForAdversary(maxPlayers - minPlayers);
        } catch (SocketTimeoutException s) {
            System.out.println("timeout for more Adversary");
        }


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

        gm = new GameManager(levels, names, adNames);
        gm.init();
        sendInitialUpdate();
        sendInitialAdversaryUpdate();
        startGame();

    }

    private static void sendInitialUpdate() throws IOException {
        for (int ii = 0; ii < playerSockets.size(); ++ii) {
            out = new PrintWriter(playerSockets.get(ii).getOutputStream(), true);
            JSONObject playerUpdate = makePlayerUpdateMessage(gm.players.get(ii));
            sendJSONMessage(playerUpdate);
        }
    }



    private static void sendInitialAdversaryUpdate() throws IOException {
        for (int ii = 0; ii < adversarySockets.size(); ++ii) {
            out = new PrintWriter(adversarySockets.get(ii).getOutputStream(), true);
            sendJSONMessage(makeAdversaryUpdateMessage(gm));
        }
    }

    private static JSONObject makeAdversaryUpdateMessage(GameManager gm) {
        JSONObject msg = new JSONObject();
        msg.put("type", "ad-update");
        JSONArray rooms = new JSONArray();
        JSONArray hws = new JSONArray();
        Level level = gm.gameState.levels.get(gm.gameState.curLevel);
        for (Room r: level.getRooms()) {
            rooms.put(makeRoomObject(r));
        }

        for (Hallway hw: level.getHallways()) {
            hws.put(makeHwObject(hw));
        }

        JSONArray objects = makeObjects(level);

        msg.put("rooms", rooms);
        msg.put("hallways", hws);
        msg.put("objects", objects);
        return msg;
    }

    private static JSONObject makeRoomObject(Room r) {
        JSONObject roomObject = new JSONObject();
        roomObject.put("type", "room");
        roomObject.put("origin", r.position);
        roomObject.put("layout", r.layout);
        return roomObject;
    }

    private static JSONObject makeHwObject(Hallway hw) {
        JSONObject hwObject = new JSONObject();
        hwObject.put("type", "hallway");
        hwObject.put("layout", hw.layout);
        hwObject.put("isPlayerInside", hw.ifPlayerInside);
        hwObject.put("playerPos", hw.playerPosition);
        return hwObject;
    }

    private static JSONArray makeObjects(Level level) {
        JSONArray objects = new JSONArray();
        JSONObject keyObject = new JSONObject();
        JSONObject exitObject = new JSONObject();
        keyObject.put("type", "key");
        keyObject.put("position", level.keyPosition);
        exitObject.put("type", "exit");
        exitObject.put("position", level.exitPosition);
        objects.put(keyObject);
        objects.put(exitObject);
        return objects;
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
        int index = -1;
        while (!gm.isGameEnd()) {
            if (index == -1){
                sendStartLevel();
                index = gm.gameState.curLevel;
            }
            else if (gm.gameState.curLevel != index) {
                sendEndLevel();
                index = gm.gameState.curLevel;
                sendStartLevel();
            }
            for (int ii = 0; ii < playerSockets.size(); ++ii) {
                System.out.println("player" + ii);
                Socket s = playerSockets.get(ii);
                Player player = gm.players.get(gm.curPlayer);
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new PrintWriter(s.getOutputStream(), true);
                sendStringMessage("move");
                JSONObject playerMove = null;
                int move = 0;
                while (move < 2) {
                    if ((playerMove = receiveJSONMessage()) != null) {
                        System.out.println(playerMove);
                        int[] dst = new int[]{playerMove.getJSONArray("to").getInt(0),
                                playerMove.getJSONArray("to").getInt(1)};
                        String result = gm.checkMoveResult(player, dst);
                        sendStringMessage(result);
                        if (result.equals("key")) {
                            whoFindTheFuckingKey = player.getName();
                        } else if (result.equals("exit")) {
                            whoFindTheExit = player.getName();
                        }

                        gm.movePlayer(player, dst);
                        sendUpdateToAllUsers();
                        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        out = new PrintWriter(s.getOutputStream(), true);
                        ++move;
                    }
                }
                gm.nextPlayerWithRemoteAdversary();
                System.out.println("next Player");
            }

            sendUpdateToADs();

        }
        sendEndgame();
    }

    private static void sendUpdateToAllUsers() throws IOException {
        for (int ii = 0; ii < playerSockets.size(); ++ii) {
            Socket s = playerSockets.get(ii);
            Player player = gm.players.get(ii);
            JSONObject updateMsg = makePlayerUpdateMessage(player);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
            sendJSONMessage(updateMsg);
        }
    }

    private static void sendUpdateToADs() throws IOException {
        for (int ii = 0; ii < adversarySockets.size(); ++ii) {
            Socket s = adversarySockets.get(ii);
            JSONObject updateMsg = makeAdversaryUpdateMessage(gm);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
            sendJSONMessage(updateMsg);
        }
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


    // wait for min players to register
    private static void waitForAdversary(int maxAdversary) throws IOException {
        for (int i = 0; i < maxAdversary; i++) {
            Socket s = server.accept();
            System.out.println("new Client connected to " + s);
            adversarySockets.add(s);

            System.out.println("wait for an Adversary username");
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
            adNames.add(line);
            System.out.println(adNames);
        }
    }

    public static void sendStartLevel() throws IOException {
        JSONObject startLevel = new JSONObject();
        startLevel.put("type", "start-level");
        startLevel.put("level", gm.gameState.levels.size());

        JSONArray nameList = new JSONArray();
        for (Player p: gm.players) {
            nameList.put(p.name);
        }
        startLevel.put("players", nameList);

        sendJsonToAllUsers(startLevel);
    }

    public static void sendEndLevel() throws IOException {
        JSONObject endLevel = new JSONObject();
        endLevel.put("type", "end-level");
        endLevel.put("key", whoFindTheFuckingKey);
        endLevel.put("exit", whoFindTheExit);
        JSONArray ejects = new JSONArray();
        for(Player p: gm.players) {
            if (p.status == -1) {
                ejects.put(p.name);
            }
        }
        endLevel.put("ejects", ejects);
        sendJsonToAllUsers(endLevel);
    }

    public static void sendEndgame() throws IOException {
        JSONObject endGame = new JSONObject();
        endGame.put("type", "end-game");
        JSONArray scores = new JSONArray();
        for (Player p : gm.players) {
            JSONObject playerScore = new JSONObject();
            playerScore.put("type", "player-score");
            playerScore.put("name", p.getName());
            playerScore.put("exits", p.timesExited);
            playerScore.put("ejects", p.numOfEjects);
            playerScore.put("keys", p.numOfKeys);
            scores.put(playerScore);
        }
        endGame.put("scores", scores);
        sendJsonToAllUsers(endGame);
    }

    public static void sendJsonToAllUsers(JSONObject jo) throws IOException {
        for (int ii = 0; ii < playerSockets.size(); ++ii) {
            Socket s = playerSockets.get(ii);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
            sendJSONMessage(jo);
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

        Server3 server = new Server3(port);
        run();

        stop();
    }


    public static void sendJSONMessage (JSONObject jo) {
        out.println(jo);
    }
    public static void sendStringMessage (String msg){ out.println(msg);}
    public static String receiveStringMessage () throws IOException {
        return in.readLine();
    }
    public static JSONObject receiveJSONMessage () throws IOException {
        return new JSONObject(in.readLine());
    }


}