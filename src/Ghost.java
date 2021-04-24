
public class Ghost implements Adversary {
  String type = "Ghost";
  String name;
  int[] position;

  public Ghost(String name) {
    this.name = name;
  }

  // init the Adversary with type, name, and position
  public Ghost(int[] position) {
    this.position = position;
  }

  // return the position of the Adversary
  public int[] getPosition() {
    return this.position;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setPosition(int[] pos) {
    this.position = pos;
  }
}
