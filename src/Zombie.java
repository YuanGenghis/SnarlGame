import javafx.util.Pair;

// represents Adversary
public class Zombie implements Adversary{
  String type = "Zombie";
  Pair<Integer, Integer> position;


  // init the Adversary with given position
  public Zombie(Pair<Integer, Integer> position) {
    this.position = position;
  }

  // return the position of the Adversary
  public Pair<Integer, Integer> getPosition() {
    return this.position;
  }

  public String getType() {return this.type;}
}
