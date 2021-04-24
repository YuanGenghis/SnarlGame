
public class Ghost implements Adversary {
  String type = "Ghost";
  String name;
  boolean isRemote = false;
  int[] position;

  public Ghost(String name, boolean isRemote, int[] position) {
    this.name = name;
    this.isRemote = isRemote;
    this.position = position;
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
