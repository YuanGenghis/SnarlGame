import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import javafx.util.Pair;

// represents the GameManager
public class GameManager {
  List<Player> players;
  Level level;
  int curPlayer;

  // construct the GameManager with given parameters
  public GameManager(List<Player> players, Level level, int curPlayer) {
    this.players = players;
    this.level = level;
    this.curPlayer = curPlayer;
  }

  //Start a simple one level game
  public GameManager(List<String> names) {
    this.players = new ArrayList<>();
    this.level = new Level();
    this.register(names);
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
    for (Player player : players) {
      player.position = this.level.setPlayer();
    }
    this.level.setAds(2);
    level.renderLevel(this.level);
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

  // get the view of player in specific position
  public int[][] getViewOfPlayer(Player p, int[] pos) {
    int[][] view = new int[5][5];
    Pair<Integer, Integer> position = level.getRoomPosition(pos);
    for (Room r: level.rooms) {
      if (position == r.position) {
        view = RuleChecker.getPlayerView(pos, r);
      }
    }
    return view;
  }

  // interact with different object, change the status of player (to dead or got key or cross the exit)
  public void interact(Player p, int[] pos) {
    String result = RuleChecker.hasInteractionPlayer(p, this.level, pos);
    switch (result) {
      case "Invalid Move":
        System.out.println(result);
      case "Adversary":
        p.status = -1;
        this.checkAllPlayerStatus();
        this.nextPlayer();
      case "Key":
        this.level.ifLocked = false;
        this.nextPlayer();
      case "Exit":
        if (!this.level.ifLocked) this.win();
        this.nextPlayer();
      //represent a valid move
      case "nothing":
        this.nextPlayer();
    }
  }

  public void nextPlayer() {
    if (curPlayer == this.players.size() - 1) {
      curPlayer = 0;
    }
    else {
      curPlayer++;
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
    if (RuleChecker.isValidMove(p, level, pos)) {
      this.interact(p, pos);
      level.movePlayer(p,new Pair<>(pos[0], pos[1]));
    }
  }

  // change the status of the player
  public void changeStatusOfPlayer(Player p, int s) {
    p.status = s;
  }

  // run win scene
  public void win() {
    System.out.println("YOU WIN!");
    System.exit(1);
  }

  // run lost scene
  public void lost() {
    System.out.println("YOU LOST!");
    System.exit(1);
  }


  //for M7 Test task
  public JSONArray objectsInView(int[] pos) {
    JSONArray objs = new JSONArray();
    if (pos[0] + 2 > this.level.keyPosition[0] && level.keyPosition[0] > pos[0] -2
            && pos[1] + 2 > level.keyPosition[1] && level.keyPosition[1] > pos[1] - 2) {
      JSONObject obj = new JSONObject();
      obj.put("type", "key");
      obj.put("position", level.keyPosition);
      objs.put(obj);
    }

    if (pos[0] + 2 > level.exitPosition[0] && level.exitPosition[0] > pos[0] -2
            && pos[1] + 2 > level.exitPosition[1] && level.exitPosition[1] > pos[1] - 2) {
      JSONObject obj = new JSONObject();
      obj.put("type", "exit");
      obj.put("position", level.exitPosition);
      objs.put(obj);
    }

    return objs;
  }

  public JSONArray actorsInView(int[] pos) {
    JSONArray actors = new JSONArray();
    for (int ii = 0; ii < level.ads.size(); ++ii) {
      int[] adPos = new int[2];
      adPos[0] = level.ads.get(ii).getPosition().getKey();
      adPos[1] = level.ads.get(ii).getPosition().getValue();
      if (pos[0] + 2 > adPos[0] && adPos[0] > pos[0] - 2
              && pos[1] + 2 > adPos[1] && adPos[1] > pos[1] - 2) {
        JSONObject actor = new JSONObject();
        actor.put("type", "ghost");
        actor.put("name", "ghost" + ii);
        actor.put("position", adPos);
        actors.put(actor);
      }
    }
    return actors;
  }

  public String checkMoveResult(Player p, int[] dst) {
    if (p.status == -1) {
      return "Eject";
    }
    else if (RuleChecker.isValidMove(p, level, dst)) {
      if (dst[0] == level.exitPosition[0] && dst[1] == level.exitPosition[1]) {
        return "Exit";
      }
      else if (dst[0] == level.keyPosition[0] && dst[1] == level.keyPosition[1]) {
        return "Key";
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
    JSONArray output = new JSONArray();
    //ii represent which player's move
    int ii = 0;

    for (int i = 0; i < 2; ++i) {
      JSONObject playerUpdate = new JSONObject();
      JSONArray firstUpdate = new JSONArray();
      firstUpdate.put(players.get(i));
      playerUpdate.put("type", "player-update");
      int[] playerPos = new int[2];
      playerPos[0] = players.get(i).position.getKey();
      playerPos[1] = players.get(i).position.getValue();
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

    for (int zz = 0; zz < moveAmount; ) {
      while (true) {
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
        }
        move.put(result);
        output.put(move);
        System.out.println(Arrays.toString(dst));
        System.out.println(players.get(ii).name + ":" + result);
        if (!result.equals("Invalid")) {
          Pair<Integer, Integer> newPos = new Pair<>(dst[0], dst[1]);
          players.get(ii).position = newPos;
          break;
        }
      }

      int amount = 0;
      while (amount != players.size()) {
        JSONArray update = new JSONArray();
        update.put(players.get(amount).name);
        JSONObject playerUpdate = new JSONObject();
        playerUpdate.put("type", "player-update");
        int[] playerPos = new int[2];
        playerPos[0] = players.get(amount).position.getKey();
        playerPos[1] = players.get(amount).position.getValue();
        int[][] view = getViewOfPlayer(players.get(amount), playerPos);
        playerUpdate.put("layout", view);
        playerUpdate.put("position", playerPos);
        playerUpdate.put("objects", this.objectsInView(playerPos));
        playerUpdate.put("actors", this.actorsInView(playerPos));
        update.put(playerUpdate);
        output.put(update);
        ++amount;
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


  public static void main(String[] args) {

    GameManager game = new GameManager(Arrays.asList("JC", "hollis"));
    game.init();
  }
}
