import javafx.util.Pair;

public class Ghost implements Adversary {
  String type = "Ghost";
  Pair<Integer, Integer> position;

  // init the Adversary with type, name, and position
  public Ghost(Pair<Integer, Integer> position) {
    this.position = position;
  }

  // return the position of the Adversary
  public Pair<Integer, Integer> getPosition() {
    return this.position;
  }
}
