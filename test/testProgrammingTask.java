import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.util.Pair;

import static org.junit.Assert.assertEquals;

public class testProgrammingTask {

  @Test
  public void testInitGameState() {
    Level level = new Level();
    GameState game = new GameState(4, level, 2);
    assertEquals(game.getLevels().size(), 1);
    assertEquals(game.getLevels().get(0).rooms.size(), 3);
    assertEquals(game.players.size(), 4);
  }

  @Test
  public void testIntermediate() {
    Level level = new Level();
    List<Level> levels = new ArrayList<>(Arrays.asList(level));
    Pair<Integer, Integer> position1 = new Pair<>(1,1);
    Pair<Integer, Integer> position2 = new Pair<>(2,1);
    Pair<Integer,Integer> position3 = new Pair<>(3,3);
    Player p1 = new Player(position1);
    Player p2 = new Player(position2);
    Player p3 = new Player(position3);
    List<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3));
    GameState game = new GameState(players, levels);
    assertEquals(game.getLevels().size(), 1);
    assertEquals(game.getLevels().get(0).rooms.size(), 3);
    assertEquals(game.players.size(), 3);
    assertEquals(game.players.get(0).getPosition(), new Pair<>(1,1));
    assertEquals(game.players.get(2).getPosition(), new Pair<>(3,3));
  }

  @Test
  public void testMove() {
    GameState game = new GameState(new Pair<>(3,3));
    Pair<Integer, Integer> newPosition = game.players.get(0).getPosition();
    assertEquals(newPosition, new Pair<>(3,3));
  }


}
