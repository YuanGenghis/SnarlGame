
// represents Adversary
public class Zombie implements Adversary{
  String type = "Zombie";
  int[] position;


  // init the Adversary with given position
  public Zombie(int[] position) {
    this.position = position;
  }

  // return the position of the Adversary
  public int[] getPosition() {
    return this.position;
  }

  public String getType() {return this.type;}

}
