import java.util.List;

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

  }

  // get the view of player in specific position
  public int[][] getViewOfPlayer(Player p, int[] pos) {
    return new int[1][1];
  }

  // interact with different object, change the status of player (to dead or got key or cross the exit)
  public void interact(Player p, int[] pos) {

  }

  // move a player to a postion
  public void movePlayer(Player p, int[] pos) {

  }

  // change the status of the player
  public void changeStatusOfPlayer(Player p, int s) {
    p.status = s;
  }

  // run win scene
  public void win() {

  }

  // run lost scene
  public void lost() {

  }
}
