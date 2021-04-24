
// represents Adversary
public class Zombie implements Adversary{
  String type = "Zombie";
  String name;
  boolean isRemote = false;
  int[] position;

  public Zombie(String name, boolean isRemote, int[] position) {
    this.name = name;
    this.isRemote = isRemote;
    this.position = position;
  }


  // init the Adversary with given position
  public Zombie(int[] position) {
    this.position = position;
  }

  // return the position of the Adversary
  public int[] getPosition() {
    return this.position;
  }

  public String getType() {return this.type;}

  @Override
  public void setPosition(int[] pos) {
    this.position = pos;
  }

  @Override
  public boolean isRemote() {
    return isRemote;
  }

}
