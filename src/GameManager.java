import java.util.List;

public class GameManager {
  List<Player> players;
  Level level;
  Player curPlayer;

  public GameManager(List<Player> players, Level level, Player curPlayer) {
    this.players = players;
    this.level = level;
    this.curPlayer = curPlayer;
  }

  public void init(Level level) {
    this.level = level;
  }

  boolean isValidName(List<Player> players, String name) {
    for (Player p: players) {
      if (p.name.equals(name)) {
        return false;
      }
    }
    return true;
  }


}
