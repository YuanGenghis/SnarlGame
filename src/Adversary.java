
public interface Adversary {

  public String type = null;

  public int[] position = null;

  // return the position of the Adversary
  public int[] getPosition();

  public String getType();
}
