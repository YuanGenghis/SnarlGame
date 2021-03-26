import javafx.util.Pair;

// represents a Player
public class Player implements User{
  String name;
  Pair<Integer, Integer> position;
  int status;

  // construct the Player
  public Player(String name, Pair<Integer, Integer> position, int status) {
    this.name = name;
    this.position = position;
    this.status = status;
  }

  // construct the Player
  public Player(String name, Pair<Integer, Integer> position) {
    this.name = name;
    this.position = position;
    this.status = 1;
  }
  // construct the Player
  public Player(Pair<Integer, Integer> position, int status) {
    this.position = position;
    this.status = status;
  }
  // construct the Player
  public Player(Pair<Integer, Integer> position) {
    this.position = position;
    this.status = 1;
  }

  public Player(String name) {
    this.name = name;
  }

  // get the position of the Player
  public Pair<Integer, Integer> getPosition() {
    return this.position;
  }

  @Override
  public void update(int[] position, Level level) {
    Pair<Integer, Integer> newPos = new Pair<>(position[0], position[1]);
    this.position = newPos;
  }
}
