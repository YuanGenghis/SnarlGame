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
  public List<int[]> getViewOfPlayer(Player p, int[] pos) {
    List<int[]> view = new ArrayList<>();
    Pair<Integer, Integer> position = level.getRoomPosition(pos);
    for (Room r: level.rooms) {
      if (position == r.position) {
        view = RuleChecker.searchTraversablePoints(pos, r);
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
}
