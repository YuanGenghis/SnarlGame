import javafx.util.Pair;

public class Adversary {
  Pair<Integer, Integer> position;

  public Adversary(Pair<Integer, Integer> position) {
    this.position = position;
  }

  public Pair<Integer, Integer> getPosition() {
    return this.position;
  }
}
