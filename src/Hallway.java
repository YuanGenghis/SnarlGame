import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

// represents a Hallway
public class Hallway implements Serializable {
  List<int[]> layout;
  List<int[]> waypoints;
  List<int[]> connection;
  boolean ifPlayerInside;
  int[] playerPosition;

  // construct the Hallway
  public Hallway(List<int[]> tilesPosition, List<int[]> waypoints) {
    this.layout = tilesPosition;
    this.waypoints = waypoints;
    ifPlayerInside = false;
    playerPosition = new int[2];
  }

  public Hallway(List<int[]> tiles) {
    this.layout = tiles;
    this.waypoints = new ArrayList<>();
    ifPlayerInside = false;
    playerPosition = new int[2];
  }

  // construct the Hallway
  public Hallway(List<int[]> tilesPosition, List<int[]> connection, List<int[]> waypoints) {
    this.layout = tilesPosition;
    this.waypoints = waypoints;
    this.connection = connection;
    ifPlayerInside = false;
    playerPosition = new int[2];
  }

  // init a default Hallway
  public Hallway() {
    this.layout = new ArrayList<>();
    this.waypoints = new ArrayList<>();
    ifPlayerInside = false;
    playerPosition = new int[2];
  }

  public Hallway(Hallway hw) {
    this.connection = hw.layout;
    this.waypoints = hw.waypoints;
    this.connection = hw.connection;
    this.ifPlayerInside = hw.ifPlayerInside;
    this.playerPosition = hw.playerPosition;
  }

  public Object clone(){
    return new Hallway( this );
  }

  // set the connection of the Hallway
  public void setConnection(int x1, int y1, int x2, int y2) {
    List<int[]> lists = new ArrayList<>();
    int[] p1 = new int[2]; p1[0] = x1; p1[1] = y1;
    int[] p2 = new int[2]; p2[0] = x2; p2[1] = y2;
    lists.add(p1); lists.add(p2);
    this.connection = lists;
  }

  // set the waypoints of the Hallway
  public void setWaypoints(List<int[]> ways) {
    this.waypoints = ways;
  }

  // set the layout of the Hallway
  public void setLayout(List<int[]> layout) {
    this.layout = layout;
  }
}
