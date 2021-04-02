import javafx.util.Pair;

public interface Adversary {

  public String type = null;

  public Pair<Integer, Integer> position = null;

  // return the position of the Adversary
  public Pair<Integer, Integer> getPosition();
}
