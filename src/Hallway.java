import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

// represents a Hallway
public class Hallway {
  List<Pair<Integer,Integer>> layout;
  Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> connection;
  List<Pair<Integer, Integer>> waypoints;

  // construct the Hallway
  public Hallway(List<Pair<Integer,Integer>> tilesPosition,
                 Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> connection,
                 List<Pair<Integer, Integer>> waypoints) {
    this.layout = tilesPosition;
    this.connection = connection;
    this.waypoints = waypoints;
  }

  // init a default Hallway
  public Hallway() {
    this.layout = new ArrayList<>();
    this.connection = new Pair<>(new Pair<>(0,0), new Pair<>(0,0));
    this.waypoints = new ArrayList<>();
  }

  // set the connection of the Hallway
  public void setConnection(int x1, int y1, int x2, int y2) {
    this.connection = new Pair<>(new Pair<>(x1,y1), new Pair<>(x2,y2));
  }

  // set the waypoints of the Hallway
  public void setWaypoints(List<Pair<Integer, Integer>> ways) {
    this.waypoints = ways;
  }

  // set the layout of the Hallway
  public void setLayout(List<Pair<Integer,Integer>> layout) {
    this.layout = layout;
  }
}
