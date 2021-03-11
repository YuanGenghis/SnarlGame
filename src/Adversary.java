import javafx.util.Pair;

public class Adversary {
  String type;
  String name;
  Pair<Integer, Integer> position;

  public Adversary(String type, String name, Pair<Integer, Integer> position) {
    this.type = type;
    this.name = name;
    this.position = position;
  }
  public Adversary(Pair<Integer, Integer> position) {
    this.position = position;
  }

  public Pair<Integer, Integer> getPosition() {
    return this.position;
  }
}
