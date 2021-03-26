import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

// represents the GameManager
public class GameManager {
  List<Player> players;
  Level level;
  Player curPlayer;

  // construct the GameManager with given parameters
  public GameManager(List<Player> players, Level level, Player curPlayer) {
    this.players = players;
    this.level = level;
    this.curPlayer = curPlayer;
  }

  // init the level?
  public void init(Level level) {
    this.level = level;
  }

  // check if the name is valid
  public boolean isValidName(List<Player> players, String name) {
    for (Player p: players) {
      if (p.name.equals(name)) {
        return false;
      }
    }
    return true;
  }

  // add player with given name to the player list if it’s valid
  public void addPlayer(String name) {
    Player p = new Player(name);
    this.players.add(p);
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
    if (level.checkIfKeyOrExit(pos) != -0) {
      //update key or exit status
    }
    else if (level.checkIfOnAd(pos)) {
      //player dead, maybe removed
      p.status = -1;
    }
  }

  // move a player to a position
  public void movePlayer(Player p, int[] pos) {
    level.movePlayer(p,new Pair<>(pos[0], pos[1]));
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
    if (p.status == 0) {
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
        return "Ok";
      }
    }
    else {
      return "Invalid";
    }
  }

  public JSONArray checkForMove(List<JSONArray> moves) {
    JSONArray output = new JSONArray();
    boolean ifUpdateTurn = true;
    boolean ifAllPlayersEnd = false;
    //ii represent which player
    int ii = 0;
    //yy represent which move, if all move done, end
    int yy = 0;
    while (true) {
      JSONArray turn = new JSONArray();
      boolean ifEnd = true;
      for (JSONArray move : moves) {
        if (move.length() > yy) {
          ifEnd = false;
        }
      }

      if (ifEnd) break;

      turn.put(players.get(ii).name);
      if (ifUpdateTurn) {
        JSONObject playerUpdate = new JSONObject();
        playerUpdate.put("type", "player-update");
        int[] playerPos = new int[2];
        playerPos[0] = players.get(ii).position.getKey();
        playerPos[1] = players.get(ii).position.getValue();
        int[][] view = getViewOfPlayer(players.get(ii), playerPos);
        playerUpdate.put("layout", view);
        playerUpdate.put("position", playerPos);
        playerUpdate.put("objects", this.objectsInView(playerPos));
        playerUpdate.put("actors", this.actorsInView(playerPos));
        turn.put(playerUpdate);
      }
      else {
        JSONObject actorMove = moves.get(ii).getJSONObject(yy);
        turn.put(actorMove);
        int[] dst = new int[2];
        dst[0] = ((JSONArray)actorMove.get("to")).getInt(0);
        dst[1] = ((JSONArray)actorMove.get("to")).getInt(1);
        turn.put(this.checkMoveResult(players.get(ii),dst));
      }

      ++ii;
      //represent one turn finish, all players move once
      if (ii >= players.size()) {
        ii = 0;
        yy++;
        ifUpdateTurn = !ifUpdateTurn;
      }
      output.put(turn);
    }
    return output;
  }
}
