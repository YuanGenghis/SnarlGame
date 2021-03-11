import javafx.util.Pair;

// represents Adversary
public class Adversary {
  String type;
  String name;
  Pair<Integer, Integer> position;

  // init the Adversary with type, name, and position
  public Adversary(String type, String name, Pair<Integer, Integer> position) {
    this.type = type;
    this.name = name;
    this.position = position;
  }

  // init the Adversary with given position
  public Adversary(Pair<Integer, Integer> position) {
    this.position = position;
  }

  // return the position of the Adversary
  public Pair<Integer, Integer> getPosition() {
    return this.position;
  }
}
