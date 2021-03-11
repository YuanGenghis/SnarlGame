import javafx.util.Pair;

public class Player {
  String name;
  Pair<Integer, Integer> position;
  int status;

  public Player(String name, Pair<Integer, Integer> position, int status) {
    this.name = name;
    this.position = position;
    this.status = status;
  }

  public Player(Pair<Integer, Integer> position, int status) {
    this.position = position;
    this.status = status;
  }

  public Player(Pair<Integer, Integer> position) {
    this.position = position;
    this.status = 1;
  }

  public Pair<Integer, Integer> getPosition() {
    return this.position;
  }
}
