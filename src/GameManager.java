import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;



// represents the GameManager
public class GameManager implements Serializable {
  List<Player> players;
  int curPlayer;
  GameState gameState;

  public int rectWidth = 25;
  public static BufferedImage PlayerImage;
  public static BufferedImage ADImage;
  private static String ADUrl =
//          "https://avatars.githubusercontent.com/u/60799921?s=400&u=4d68d8d6c5acd9a4b48ef35dc7d3a0b9a8164d04&v=4";
          "https://images-na.ssl-images-amazon.com/images/I/71vj4KrX%2BvL._AC_SL1500_.jpg";
  private static String PlayerUrl =
//          "https://avatars.githubusercontent.com/u/46980128?s=400&u=abab5bff473ece8159ceb6f29ebf7cf3fc132e2b&v=4";
//          "https://avatars.githubusercontent.com/u/60799921?s=400&u=4d68d8d6c5acd9a4b48ef35dc7d3a0b9a8164d04&v=4";
//  "https://media-exp1.licdn.com/dms/image/C4E03AQFk3SizfWyASg/profile-displayphoto-shrink_800_800/0/1581017250813?e=1622678400&v=beta&t=Lw93auRr4x3oh9HvykxpqGsGVTjnrf547ApLp9NB3TA";
        "https://avatars.githubusercontent.com/u/46980128?s=400&u=abab5bff473ece8159ceb6f29ebf7cf3fc132e2b&v=4";

  private static String Ghost =
          "https://image.shutterstock.com/image-photo/zombie-ghost-isolated-on-black-260nw-646738963.jpg";
  // construct the GameManager with given parameters
  public GameManager(List<Player> players, GameState gameState, int curPlayer) {
    this.players = players;
    this.gameState = gameState;
    this.curPlayer = curPlayer;
  }

  public GameManager(List<Player> players, int curPlayer, GameState gameState, int rectWidth) {
    this.players = players;
    this.curPlayer = curPlayer;
    this.gameState = gameState;
    this.rectWidth = rectWidth;
  }

  //constructor for test task
  public GameManager(List<Player> players, Level curLevel, int curPlayer) {
    this.players = players;
    this.curPlayer = curPlayer;

    List<Level> levels = new ArrayList<>();
    levels.add(curLevel);
    this.gameState = new GameState(players, levels);
  }

  public GameManager(List<Level> levels, List<String> names) {
    this.players = new ArrayList<>();
    this.register(names);
    this.curPlayer = 0;
    this.gameState = new GameState(this.players, levels);
  }

  //Start a simple one level game
  public GameManager(List<String> names) {
    this.players = new ArrayList<>();
    this.register(names);
    this.gameState = new GameState(this.players);
    this.curPlayer = 0;
  }

  // register players
  public void register(List<String> names) {
    for (String name: names) {
      if (isValidName(name)) {
        Player p = new Player(name);
        this.players.add(p);
      }
      else {
        System.out.println("Player already exist or not valid: " + name);
      }
    }
  }

  // init the GameManager
  public void init() {
    Level level = this.gameState.levels.get(this.gameState.curLevel);
    for (Player player : players) {
      player.position = level.setPlayer();
    }

    int zombieAmount = ((gameState.curLevel + 1)/ 2) + 1;
    for (int ii = 0; ii < gameState.levels.size(); ++ii) {
      gameState.levels.get(ii).setZombiesInLevel(zombieAmount);
    }

    int ghostAmount = gameState.curLevel/ 2;
    for (int ii = 0; ii < gameState.levels.size(); ++ii) {
      gameState.levels.get(ii).setGhostInLevel(ghostAmount);
    }
  }

  // check if the name is valid
  public boolean isValidName(String name) {
    for (Player p: players) {
      if (p.name.equals(name)) {
        return false;
      }
    }
    return true;
  }

  // move each adversary
  public void adversaryMove() {
    Level curLevel = gameState.levels.get(gameState.curLevel);
    for (int ii = 0; ii < curLevel.ads.size(); ++ii) {
      Adversary ad = curLevel.ads.get(ii);
      int[] dst = RuleChecker.getAdNextMove(ad, curLevel, players);

      curLevel.moveAds(ii, dst);

      if (dst != null) {
        this.gameState.levels.get(gameState.curLevel).moveAds(ii, dst);
      }
    }
  }

  // get the view of player in specific position
  public int[][] getViewOfPlayer(Player p, int[] pos) {
    int[][] view = new int[5][5];
    boolean ifInHW = true;
    int[] position = gameState.levels.get(gameState.curLevel).getRoomPosition(pos);
    for (Room r: gameState.levels.get(gameState.curLevel).rooms) {
      if (position == r.position) {
        ifInHW = false;
        List<int[]> hallwaysPoints =
                RuleChecker.findHallwayPoints(gameState.levels.get(gameState.curLevel).hallways);
        view =
                RuleChecker.getPlayerView(pos, r, hallwaysPoints, gameState.levels.get(gameState.curLevel).rooms);
      }
    }
    if (ifInHW) {
      List<int[]> hallwaysPoints =
              RuleChecker.findHallwayPoints(gameState.levels.get(gameState.curLevel).hallways);
      view = RuleChecker.getPlayerView(pos, null, hallwaysPoints, gameState.levels.get(gameState.curLevel).rooms);
    }
    return view;
  }

  // interact with different object, change the status of player (to dead or got key or cross the exit)
  public void interact(Player p, int[] pos) {
    String result = RuleChecker.hasInteractionPlayer(p, gameState.levels.get(gameState.curLevel), pos);
    switch (result) {
      case "Adversary":
        System.out.println("Player " + p.name + " was expelled");
        p.status = -1;
        playerExpelled(p);
        this.checkAllPlayerStatus();
        this.nextPlayer();
        break;
      case "Key":
        ++p.numOfKeys;
        System.out.println("Player " + p.name + " found the key");
        gameState.levels.get(gameState.curLevel).isLocked = false;
        break;
      case "Exit":
        if (!gameState.levels.get(gameState.curLevel).isLocked) {
          ++p.timesExited;
          System.out.println("Player " + p.name + " exited");
          this.win();
        }
        break;


      default: break;

    }
  }

  public void playerExpelled(Player p) {
    Level level = gameState.levels.get(gameState.curLevel);
    Room r = level.inWhichRoom(p.position);
    if (r != null) {
      r.layout[p.position[0]-r.position[0]][p.position[1]-r.position[1]] = 'A';
    }

  }

  // move to the next Player's round
  public void nextPlayer() {
    // after all players' rounds, the adversaries move
    if (curPlayer == this.players.size() - 1) {
      this.adversaryMove();
      curPlayer = 0;
    }
    else {
      curPlayer++;
      if (players.get(curPlayer).status == -1) {
        nextPlayer();
      }
    }
  }

  public void checkAllPlayerStatus() {
    boolean ifAllDie = true;
    for (Player p: this.players) {
      if (p.status != -1) {
        ifAllDie = false;
        break;
      }
    }
    if (ifAllDie) this.lost();
  }

  // move a player to a position
  public void movePlayer(Player p, int[] pos) {
//    System.out.println("move to:" + pos[0] + ":" + pos[1]);
    if (RuleChecker.isValidMove(p, gameState.levels.get(gameState.curLevel), pos)) {
      this.interact(p, pos);
      gameState.levels.get(gameState.curLevel).movePlayer(p, pos);
    }
//    else {
//      System.out.println("Invalid move, move again!");
//    }
  }

  // change the status of the player
  public void changeStatusOfPlayer(Player p, int s) {
    p.status = s;
  }

  // run win scene
  public void win() {
    if (gameState.curLevel == gameState.levels.size() - 1) {
      System.out.println("YOU WIN!!!");
      finalPrint();
      System.exit(1);
    } else {
      System.out.println("next level");
      gameState.levels.get(gameState.curLevel).isEnd = true;

      ++gameState.curLevel;

      Level level = this.gameState.levels.get(this.gameState.curLevel);
      for (Player player : players) {
        player.position = level.setPlayer();
      }
    }

  }

  public void finalPrint() {
    for (Player p: players) {
      System.out.println(p.name + " have picked " + p.numOfKeys + " keys");
      System.out.println(p.name + " have exited " + p.timesExited + " times");
    }

    Collections.sort(players, new Comparator<Player>() {
      @Override
      public int compare(Player o1, Player o2) {
        return o2.numOfKeys - o1.numOfKeys;
      }
    });

    System.out.println("Rank by keys: ");
    for (Player p: players) {
      System.out.println(p.name);
    }

    Collections.sort(players, new Comparator<Player>() {
      @Override
      public int compare(Player o1, Player o2) {
        return o2.timesExited - o1.timesExited;
      }
    });

    System.out.println("Rank by exit times: ");
    for (Player p: players) {
      System.out.println(p.name);
    }

  }

  // run lost scene
  public void lost() {
    System.out.println("YOU LOST! ALl players are expelled in level: " + (gameState.curLevel + 1));
    finalPrint();
    System.exit(1);
  }

  public boolean isGameEnd() {
    boolean ifAllDie = true;
    for (Player p: this.players) {
      if (p.status != -1) {
        ifAllDie = false;
        break;
      }
    }
    if (ifAllDie) {
      return true;
    }

    if (gameState.curLevel == gameState.levels.size() - 1) {
      if (gameState.levels.get(gameState.curLevel).isEnd) {
        return true;
      }
    }
    return false;
  }


  //for M7 Test task
  public JSONArray objectsInView(int[] pos) {
    JSONArray objs = new JSONArray();
    int[] keyPos = gameState.levels.get(gameState.curLevel).keyPosition;
    if (pos[0] + 2 >= keyPos[0] && keyPos[0] >= pos[0] -2
            && pos[1] + 2 >= keyPos[1] && keyPos[1] >= pos[1] - 2
            && gameState.levels.get(gameState.curLevel).isLocked) {
      JSONObject obj = new JSONObject();
      obj.put("type", "key");
      obj.put("position", keyPos);
      objs.put(obj);
    }

    int[] exitPos = gameState.levels.get(gameState.curLevel).exitPosition;
    if (pos[0] + 2 >= exitPos[0] && exitPos[0] >= pos[0] -2
            && pos[1] + 2 >= exitPos[1] && exitPos[1] >= pos[1] - 2) {
      JSONObject obj = new JSONObject();
      obj.put("type", "exit");
      obj.put("position", exitPos);
      objs.put(obj);
    }

    return objs;
  }

  //for M7 Test task
  public JSONArray actorsInView(int[] pos) {
    JSONArray actors = new JSONArray();
    for (int ii = 0; ii < gameState.levels.get(gameState.curLevel).ads.size(); ++ii) {
      int[] adPos = new int[2];
      adPos[0] = gameState.levels.get(gameState.curLevel).ads.get(ii).getPosition()[0];
      adPos[1] = gameState.levels.get(gameState.curLevel).ads.get(ii).getPosition()[1];
      if (pos[0] + 2 >= adPos[0] && adPos[0] >= pos[0] - 2
              && pos[1] + 2 >= adPos[1] && adPos[1] >= pos[1] - 2) {
        JSONObject actor = new JSONObject();
        actor.put("type", "ghost");
        actor.put("name", "ghost" + (ii + 1));
        actor.put("position", adPos);
        actors.put(actor);
      }
    }
    return actors;
  }

  //for M7 Test task
  public String checkMoveResult(Player p, int[] dst) {
    if (gameState.levels.get(gameState.curLevel).checkIfOnAd(dst)) {
      p.status = -1;
      return "Eject";
    }
    else if (RuleChecker.isValidMove(p, gameState.levels.get(gameState.curLevel), dst)) {
      if (dst[0] == gameState.levels.get(gameState.curLevel).exitPosition[0]
              && dst[1] == gameState.levels.get(gameState.curLevel).exitPosition[1]) {
        if (gameState.levels.get(gameState.curLevel).isLocked) {
          return "OK";
        } else {
          return "Exit";
        }
      }
      else if (dst[0] == gameState.levels.get(gameState.curLevel).keyPosition[0]
              && dst[1] == gameState.levels.get(gameState.curLevel).keyPosition[1]) {
        if (gameState.levels.get(gameState.curLevel).isLocked) {
          gameState.levels.get(gameState.curLevel).isLocked = false;
          return "Key";
        } else {
          return "OK";
        }
      }
      else {
        return "OK";
      }
    }
    else {
      return "Invalid";
    }
  }


  //Just for Test task
  public JSONArray checkForMove(List<JSONArray> moves, int moveAmount) {
    System.out.println(gameState.levels.get(gameState.curLevel).isLocked);
    JSONArray output = new JSONArray();
    //ii represent which player's move
    int ii = 0;
    //first update
    for (int i = 0; i < 2; ++i) {
      JSONObject playerUpdate = new JSONObject();
      JSONArray firstUpdate = new JSONArray();
      firstUpdate.put(players.get(i).name);
      playerUpdate.put("type", "player-update");
      int[] playerPos = new int[2];
      playerPos[0] = players.get(i).position[0];
      playerPos[1] = players.get(i).position[1];
      int[][] view = getViewOfPlayer(players.get(i), playerPos);
      playerUpdate.put("layout", view);
      playerUpdate.put("position", playerPos);
      playerUpdate.put("objects", this.objectsInView(playerPos));
      playerUpdate.put("actors", this.actorsInView(playerPos));
      firstUpdate.put(playerUpdate);
      output.put(firstUpdate);
    }

    HashMap<Integer, Integer> moveMap = new HashMap<>();
    for (int t = 0; t < players.size(); ++t) {
      moveMap.put(t, 0);
    }

    //move-turn
    for (int zz = 0; zz < moveAmount; ) {
      while (true) {
        if (players.get(ii).status == -1) {
          break;
        }
        JSONArray move = new JSONArray();
        move.put(players.get(ii).name);
        int moveTurn = moveMap.get(ii);
        JSONObject actorMove = moves.get(ii).getJSONObject(moveTurn);
        moveMap.replace(ii, moveTurn+1);
        move.put(actorMove);
        int[] dst = new int[2];
        String result;
        if (actorMove.get("to") != JSONObject.NULL) {
          dst[0] = ((JSONArray) actorMove.get("to")).getInt(0);
          dst[1] = ((JSONArray) actorMove.get("to")).getInt(1);
          result = this.checkMoveResult(players.get(ii), dst);
        } else {
          result = "OK";
          move.put(result);
          output.put(move);
          break;
        }
        move.put(result);
        output.put(move);
//        System.out.println("pos: " + players.get(ii).position);
//        System.out.println("dst: " + Arrays.toString(dst));
//        System.out.println(players.get(ii).name + ": " + result);
        if (!result.equals("Invalid")) {
          int[] newPos = dst;
          players.get(ii).position = newPos;
          break;
        }
      }

      //update turn
      int amount = 0;
      while (amount != players.size()) {
        if (players.get(amount).status == -1) {
          ++amount;
        } else {
          JSONArray update = new JSONArray();
          update.put(players.get(amount).name);
          JSONObject playerUpdate = new JSONObject();
          playerUpdate.put("type", "player-update");
          int[] playerPos = new int[2];
          playerPos[0] = players.get(amount).position[0];
          playerPos[1] = players.get(amount).position[1];
          int[][] view = getViewOfPlayer(players.get(amount), playerPos);
          playerUpdate.put("layout", view);
          playerUpdate.put("position", playerPos);
          playerUpdate.put("objects", this.objectsInView(playerPos));
          playerUpdate.put("actors", this.actorsInView(playerPos));
          update.put(playerUpdate);
          output.put(update);
          ++amount;
        }
      }
      ++ii;

      //represent all players have move once, one turn done
      if (ii == players.size()) {
        ++zz;
        ii = 0;
      }

    }
    return output;
  }

  public void drawPlayerView(Graphics g) {
    Player p = players.get(curPlayer);
    int[] pos = new int[2];
    pos[0] = p.position[0]; pos[1] = p.position[1];
    int[][] view = this.getViewOfPlayer(p, pos);

    int row = 0;
    for (int ii = 2; ii > -3; --ii) {
      int col = 0;
      for (int zz = 2; zz > -3; --zz) {
        int xx = (2 + (pos[1] - zz)) * rectWidth;
        int yy = (2 + (pos[0] - ii)) * rectWidth;

        if (view[row][col] == 0) {
          g.setColor(Color.DARK_GRAY);
        } else if (view[row][col] == 1) {
          g.setColor(Color.GRAY);
        }  else if (view[row][col] == 2) {
        g.setColor(Color.CYAN);
        }
        g.fillRect(xx, yy, rectWidth, rectWidth);
        g.setColor(Color.black);
        g.drawRect(xx, yy, rectWidth, rectWidth);

        if (view[row][col] == 5) {
          g.setColor(Color.GRAY);
          g.drawRect(xx, yy, rectWidth, rectWidth);
          g.setColor(Color.RED);
          Font tr = new Font("TimesRoman", Font.PLAIN, 12);
          g.setFont(tr);
          g.drawString("E", xx + 10, yy + 15);
        }
        else if (view[row][col] == 4) {
          g.setColor(Color.GRAY);
          g.drawRect(xx, yy, rectWidth, rectWidth);
          g.setColor(Color.blue);
          Font tr = new Font("TimesRoman", Font.PLAIN, 12);
          g.setFont(tr);
          g.drawString("K", xx + 10, yy + 15);
        }
        else if (view[row][col] == -1) {
          try {
            URL url = new URL(ADUrl);
            ADImage = ImageIO.read(url);
          }
          catch(IOException e) {
            System.out.println("Image not found");
          }
          g.drawImage(ADImage, xx, yy,
                  rectWidth -1, rectWidth -1, null);
        }
        else if (view[row][col] == -2) {
          try {
            URL url = new URL(Ghost);
            ADImage = ImageIO.read(url);
          }
          catch(IOException e) {
            System.out.println("Image not found");
          }
          g.drawImage(ADImage, xx, yy,
                  rectWidth -1, rectWidth -1, null);
        }

        if ((ii == 0 && zz == 0) || view[row][col] == 3) {
          try {
            URL url = new URL(PlayerUrl);
            PlayerImage = ImageIO.read(url);
          }
          catch(IOException e) {
            System.out.println("Image not found");
          }

          g.drawImage(PlayerImage, xx, yy,
                  rectWidth -1, rectWidth -1, null);
        }
        ++col;
      }
      ++row;
    }
  }
}
