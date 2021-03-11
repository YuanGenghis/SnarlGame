import java.util.*;
import javafx.util.Pair;

public class GameState {
  List<Level> levels;
  int levelStatus;
  int gameStatus;
  List<Player> players;

  //This is the initial game state that takes in a level, some amount of players(1-4),
  // and some number of adversaries.
  //YOU CAN RUN MAIN FUNCTION TO SEE THE REAL RENDER GAME!!!!
  public GameState(int playerAmount, Level level, int ads) {
    this.levels = new ArrayList<>();
    this.players = new ArrayList<>();
    this.levelStatus = 0;
    this.gameStatus = 1;
    initGame(playerAmount, level, ads);
  }

  //This is a intermediate game state with given player locations,
  //the adversary locations and the status of the level exit are store in the level
  //so also have to provide whole level info
  public GameState(List<Player> players, List<Level> levels) {
    this.players = players;
    this.levels = levels;
    this.levelStatus = 0;
    this.gameStatus = 1;
  }

  //This Game state will first creat a default game state with 2 players on top-left,
  //1 ad on bottom-right. Then one player will be moved from (1,1) to (3,3) then render the whole
  //game state
  public GameState(Pair<Integer, Integer> newPosition) {
    Level level = new Level();
    this.levels = new ArrayList<>();
    this.players = new ArrayList<>();
    this.levelStatus = 0;
    this.gameStatus = 1;
    initGame(2, level, 1);
    this.levels.get(levelStatus).movePlayer(this.players.get(0), new Pair<>(3,3));
    this.render(this.levels.get(levelStatus));
  }

  public List<Level> getLevels() {
    return this.levels;
  }

  public void initGame(int playerAmount, Level level, int ads) {
    for (int ii = 0; ii < playerAmount; ++ii) {
      Pair<Integer, Integer> position = level.setPlayer();
      Player player = new Player(position);
      this.players.add(player);
    }
    this.levels.add(level);
    this.levels.get(levelStatus).setAds(ads);

    this.render(level);
  }

  public void render(Level level) {
    level.renderLevel(level);
  }



  public static void main(String[] args) {
    Level level = new Level();
    GameState game = new GameState(2, level, 2);
//    level.movePlayer(game.players.get(0), new Pair<>(3,3));
    level.renderLevel(level);
  }
}
