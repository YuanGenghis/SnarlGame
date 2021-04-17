import java.io.Serializable;

// represents a Player
public class Player implements Serializable {
  String name;
  int[] position;
  int status;
  int timesExited = 0;
  int numOfKeys = 0;

  // construct the Player
  public Player(String name, int[] position, int status) {
    this.name = name;
    this.position = position;
    this.status = status;
  }

  // construct the Player
  public Player(String name, int[] position) {
    this.name = name;
    this.position = position;
    this.status = 1;
  }
  // construct the Player
  public Player(int[] position, int status) {
    this.position = position;
    this.status = status;
  }
  // construct the Player
  public Player(int[] position) {
    this.position = position;
    this.status = 1;
  }

  public Player(String name) {
    this.name = name;
  }

  // get the position of the Player
  public int[] getPosition() {
    return this.position;
  }

  public String getName() {
    return name;
  }

  public void update(int[] position, Level level) {
    this.position = position;
  }
}
