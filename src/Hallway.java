import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

public class Hallway {
  List<Pair<Integer,Integer>> layout;
  Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> connection;
  List<Pair<Integer, Integer>> waypoints;

  public Hallway(List<Pair<Integer,Integer>> tilesPosition,
                 Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> connection,
                 List<Pair<Integer, Integer>> waypoints) {
    this.layout = tilesPosition;
    this.connection = connection;
    this.waypoints = waypoints;
  }

  public Hallway() {
    this.layout = new ArrayList<>();
    this.connection = new Pair<>(new Pair<>(0,0), new Pair<>(0,0));
    this.waypoints = new ArrayList<>();
  }

  public void setConnection(int x1, int y1, int x2, int y2) {
    this.connection = new Pair<>(new Pair<>(x1,y1), new Pair<>(x2,y2));
  }

  public void setWaypoints(List<Pair<Integer, Integer>> ways) {
    this.waypoints = ways;
  }

  public void setLayout(List<Pair<Integer,Integer>> layout) {
    this.layout = layout;
  }

}
