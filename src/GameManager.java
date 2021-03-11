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
  boolean isValidName(List<Player> players, String name) {
    for (Player p: players) {
      if (p.name.equals(name)) {
        return false;
      }
    }
    return true;
  }


}
