
public interface Adversary {

  public String type = null;
  public boolean isRemote = false;

  public int[] position = null;

  // return the position of the Adversary
  public int[] getPosition();

  public String getType();

  public void setPosition(int[] pos);

  public boolean isRemote();

}
