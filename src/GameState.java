import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.*;

public class GameState implements Serializable {
  List<Level> levels;
  int curLevel;
  int gameStatus;
  List<Player> players;

  //This is the initial game state that takes in a level, some amount of players(1-4),
  // and some number of adversaries.
  //YOU CAN RUN MAIN FUNCTION TO SEE THE REAL RENDER GAME!!!!
  public GameState(int playerAmount, Level level, int ads) {
    this.levels = new ArrayList<>();
    this.players = new ArrayList<>();
    this.curLevel = 0;
    this.gameStatus = 1;
    initGame(playerAmount, level, ads);
  }

  public GameState(List<Player> players) {
    this.players = players;
    this.levels = new ArrayList<>();
    this.levels.add(new Level());
    this.curLevel = 0;
    this.gameStatus = 1;
  }

  //This is a intermediate game state with given player locations,
  //the adversary locations and the status of the level exit are store in the level
  //so also have to provide whole level info
  public GameState(List<Player> players, List<Level> levels) {
    this.players = players;
    this.levels = levels;
    this.curLevel = 0;
    this.gameStatus = 1;
  }

  public GameState(List<Player> players, List<Level> levels, List<String> adNames) {
    this.players = players;
    this.levels = levels;
    this.curLevel = 0;
    this.gameStatus = 1;
  }

  //This Game state will first creat a default game state with 2 players on top-left,
  //1 ad on bottom-right. Then one player will be moved from (1,1) to (3,3) then render the whole
  //game state
  public GameState(int[] newPosition) {
    Level level = new Level();
    this.levels = new ArrayList<>();
    this.players = new ArrayList<>();
    this.curLevel = 0;
    this.gameStatus = 1;
    initGame(2, level, 1);
    int[] newPos = new int[2]; newPos[0] = 3; newPos[1] = 3;
    this.levels.get(curLevel).movePlayer(this.players.get(0), newPos);
//    this.render(this.levels.get(levelStatus));
  }

  public List<Level> getLevels() {
    return this.levels;
  }

  public void initGame(int playerAmount, Level level, int ads) {
    for (int ii = 0; ii < playerAmount; ++ii) {
      int[] position = level.setPlayer();
      Player player = new Player(position);
      this.players.add(player);
    }
    this.levels.add(level);
//    this.levels.get(curLevel).setZombiesInLevel(ads);
  }



  public JSONArray checkForPoint(String pName, int[] point, JSONObject jo) {
    boolean exitLocked = (boolean) jo.get("exit-locked");
    JSONObject updateState = new JSONObject();
    JSONArray output = new JSONArray();
    Level level = this.levels.get(curLevel);
    //if the player is part of the players
    if (playerInGame(pName)) {
      //if this the destination position is invalid(a wall or outside the room or hallway)
      if ((level.checkTailType(point) == -1 || level.checkTailType(point) == 0)
          && (level.checkIfInHallways(point) == -1)) { {
          output.put("Failure");
          output.put("The destination position ");
          output.put(point);
          output.put(" is invalid.");
        }
      }
      //the destination position is valid
      else {
        //if the destination is a exit
        if (level.checkIfKeyOrExit(point) == 2) {
          //if the exit is locked, can just get on it, but can not exit
          if (exitLocked) {
            updateState.put("type", "state");
            updateState.put("level", jo.getJSONObject("level"));
            JSONArray jsonPlayers = jo.getJSONArray("players");
            JSONArray newPlayers = new JSONArray();
            for (int ii = 0; ii < jsonPlayers.length(); ++ii) {
              if (!jsonPlayers.getJSONObject(ii).getString("name").equals(pName)) {
                newPlayers.put(jsonPlayers.getJSONObject(ii));
              }
            }
            updateState.put("players", newPlayers);
            updateState.put("adversaries", jo.getJSONArray("adversaries"));
            updateState.put("exit-locked", exitLocked);

            output.put("Success");
            output.put(updateState);
          }
          //player successfully exit
          else {
            updateState.put("type", "state");
            updateState.put("level", jo.getJSONObject("level"));
            JSONArray jsonPlayers = jo.getJSONArray("players");
            JSONArray newPlayers = new JSONArray();
            for (int ii = 0; ii < jsonPlayers.length(); ++ii) {
              if (!jsonPlayers.getJSONObject(ii).getString("name").equals(pName)) {
                newPlayers.put(jsonPlayers.getJSONObject(ii));
              }
            }
            updateState.put("players", newPlayers);
            updateState.put("adversaries", jo.getJSONArray("adversaries"));
            updateState.put("exit-locked", exitLocked);

            output.put("Success");
            output.put("Player ");
            output.put(pName);
            output.put(" exited");
            output.put(updateState);
          }
        }
        //if player move onto a adversary, remove the player, success
        else if (level.checkIfOnAd(point)) {
          updateState.put("type", "state");
          updateState.put("level", jo.getJSONObject("level"));
          JSONArray jsonPlayers = jo.getJSONArray("players");
          JSONArray newPlayers = new JSONArray();
          for (int ii = 0; ii < jsonPlayers.length(); ++ii) {
            if (!jsonPlayers.getJSONObject(ii).getString("name").equals(pName)) {
              newPlayers.put(jsonPlayers.getJSONObject(ii));
            }
          }
          updateState.put("players", newPlayers);
          updateState.put("adversaries", jo.getJSONArray("adversaries"));
          updateState.put("exit-locked", exitLocked);

          output.put("Success");
          output.put("Player ");
          output.put(pName);
          output.put(" was ejected.");
          output.put(updateState);
        }
        //move to a valid place and no exit and adversary
        else {
          //if move onto a player, invalid move
          if (level.checkIfOnPlayer(this.players, point)) {
            output.put("Failure");
            output.put("The destination position ");
            output.put(point);
            output.put(" is invalid.");
          }
          //it is a valid move, success! change player position
          else {
            updateState.put("type", "state");
            updateState.put("level", jo.getJSONObject("level"));
            JSONArray jsonPlayers = jo.getJSONArray("players");
            JSONArray newPlayers = new JSONArray();
            for (int ii = 0; ii < jsonPlayers.length(); ++ii) {
              if (jsonPlayers.getJSONObject(ii).getString("name").equals(pName)) {
                JSONObject newPlayer = new JSONObject();
                newPlayer.put("type", "player");
                newPlayer.put("name", pName);
                newPlayer.put("position", point);
                newPlayers.put(newPlayer);
              } else {
                newPlayers.put(jsonPlayers.getJSONObject(ii));
              }
            }
            updateState.put("players", newPlayers);
            updateState.put("adversaries", jo.getJSONArray("adversaries"));
            updateState.put("exit-locked", exitLocked);
            output.put("Success");
            output.put(updateState);
          }
        }
      }
    }
    //the player isn’t part of the input state
    else {
      output.put("Failure");
      output.put("Player ");
      output.put(pName);
      output.put(" is not a part of the game.");
    }
    return output;
  }

  public boolean playerInGame(String name) {
    for (int ii = 0; ii < players.size(); ++ii) {
      if (players.get(ii).name.equals(name)) {
        return true;
      }
    }
    return false;
  }





  public static void main(String[] args) {
    Level level = new Level();

    int[] pos = new int[2];
    pos[0] = 1; pos[1] = 8;
    int [][] view = RuleChecker.getPlayerView(pos, level.rooms.get(1),
            RuleChecker.findHallwayPoints(level.hallways), level.rooms);
//    System.out.println(level.rooms.get(1).position);

    for (int ii = 0; ii < 5; ++ii) {
      System.out.println(Arrays.toString(view[ii]));
    }


    GameState game = new GameState(2, level, 2);
//    level.renderLevel(level);
  }
}
