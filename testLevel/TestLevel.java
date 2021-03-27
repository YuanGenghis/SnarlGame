import com.sun.corba.se.spi.ior.ObjectKey;

import java.util.*;
import org.json.*;

import javafx.util.Pair;


public class TestLevel {
  public static int[] point = new int[2];

  //transfer json file to level
  public static Level levelBuilder(JSONObject jo) {
    //create a empty list of rooms and hallways
    List<Room> rooms = new ArrayList<>();
    List<Hallway> hallways = new ArrayList<>();

    //transfer json file to real rooms and add into list
    JSONArray jRooms = jo.getJSONArray("rooms");
    for (int ii = 0; ii < jRooms.length(); ++ii) {
      JSONObject j = (JSONObject) jRooms.get(ii);
      JSONArray layout = (JSONArray) j.get("layout");
      JSONArray row = (JSONArray) layout.get(0);
      JSONObject bound = j.getJSONObject("bounds");
      int rowNum = bound.getInt("rows");
      int colNum = bound.getInt("columns");
      Room room = new Room(rowNum, colNum);
      for (int jj = 0; jj < layout.length(); ++jj) {
        for (int zz = 0; zz < row.length(); ++zz) {
          int tile = ((JSONArray)layout.get(jj)).getInt(zz);
          if (tile == 0) {
            room.addTile(jj,zz,'x');
          } else if (tile == 1) {
            room.addTile(jj,zz,'.');
          } else if (tile == 2) {
            room.addTile(jj,zz,'-');
          }
        }
      }
      int x = ((JSONArray) j.get("origin")).getInt(0);
      int y = ((JSONArray) j.get("origin")).getInt(1);
      room.setPosition(x,y);
      rooms.add(room);
    }

    //transfer json file to read hallways and add into list
    JSONArray hws = jo.getJSONArray("hallways");
    for (int ii = 0; ii < hws.length(); ++ii) {
      JSONObject h = (JSONObject) hws.get(ii);
      Hallway hw = new Hallway();
      List<Pair<Integer, Integer>> waypoints = new ArrayList<>();
      int fromX = ((JSONArray) h.get("from")).getInt(0);
      int fromY = ((JSONArray) h.get("from")).getInt(1);
      int toX = ((JSONArray) h.get("to")).getInt(0);
      int toY = ((JSONArray) h.get("to")).getInt(1);
      hw.setConnection(fromX, fromY, toX, toY);

      List<Pair<Integer, Integer>> layout = new ArrayList<>();
      JSONArray wps = h.getJSONArray("waypoints");
      for (int jj = 0; jj < wps.length(); ++jj) {
        int x = ((JSONArray) wps.get(jj)).getInt(0);
        int y = ((JSONArray) wps.get(jj)).getInt(1);
        Pair<Integer, Integer> point = new Pair<>(x, y);
        waypoints.add(point);
      }
      Pair<Integer, Integer> startPoint = new Pair<>(fromX, fromY);
      int rowDif, colDif;
      int val = 1;
      //check for waypoints and use waypoints to save to path and add to hallway layout
      for (int zz = 0; zz < waypoints.size(); ++zz) {
        rowDif = waypoints.get(zz).getKey() - startPoint.getKey();
        colDif = waypoints.get(zz).getValue() - startPoint.getValue();
        if (rowDif != 0) {
          if (rowDif < 0) {
            val = -1;
          } else {val = 1;}
          for (int i = 0; i < Math.abs(rowDif) - 1; ++i) {
            Pair<Integer, Integer> point = new Pair<>(startPoint.getKey() + val, startPoint.getValue());
            layout.add(point);
            startPoint = point;
          }
        } else {
          if (colDif < 0) {
            val = -1;
          } else {val = 1;}
          for (int i = 0; i < Math.abs(colDif) - 1; ++i) {
            Pair<Integer, Integer> point = new Pair<>(startPoint.getKey(), startPoint.getValue() + val);
            layout.add(point);
            startPoint = point;
          }
        }
        startPoint = waypoints.get(zz);
        layout.add(waypoints.get(zz));
      }
      rowDif = toX - startPoint.getKey();
      colDif = toY - startPoint.getValue();
      if (rowDif != 0) {
        if (rowDif < 0) {
          val = -1;
        } else {val = 1;}
        for (int jj = 0; jj < Math.abs(rowDif) - 1; ++jj) {
          Pair<Integer, Integer> point = new Pair<>(startPoint.getKey() + val, startPoint.getValue());
          layout.add(point);
          startPoint = point;
        }
      }
      else {
        if (colDif < 0) {
          val = -1;
        } else {val = 1;}
        for (int jj = 0; jj < Math.abs(colDif) - 1; ++jj) {
          Pair<Integer, Integer> point = new Pair<>(startPoint.getKey(), startPoint.getValue() + val);
          layout.add(point);
          startPoint = point;
        }
      }
      hw.setWaypoints(waypoints);
      hw.setLayout(layout);
      hallways.add(hw);
    }

    //find key and exit position and add to level
    JSONArray objects = jo.getJSONArray("objects");
    int[] keyPosition = new int[2];
    int[] exitPosition = new int[2];
    for (int ii = 0; ii < objects.length(); ++ii) {
      JSONObject jsonO = objects.getJSONObject(ii);
      if (jsonO.getString("type").equals("key")) {
        keyPosition[0] = ((JSONArray) jsonO.get("position")).getInt(0);
        keyPosition[1] = ((JSONArray) jsonO.get("position")).getInt(1);
      }
      else if (jsonO.getString("type").equals("exit")) {
        exitPosition[0] = ((JSONArray) jsonO.get("position")).getInt(0);
        exitPosition[1] = ((JSONArray) jsonO.get("position")).getInt(1);
      }
    }

    //build level depend on previous transfer
    Level level = new Level(rooms, hallways);
    level.keyPosition = keyPosition;
    level.exitPosition = exitPosition;
    return level;
  }



  public static void main(String[] args) {
    // read input
    Scanner sc = new Scanner(System.in);
    StringBuilder sb = new StringBuilder();
    while (sc.hasNext()) {
      String s1 = sc.next();
      if (s1.equals("exit")) {
        break;
      }
      sb.append(s1);
    }
    String json = sb.toString();
    JSONTokener jt = new JSONTokener(json);

    Object obj = jt.nextValue();

    point[0] = (int) ((JSONArray) (((JSONArray) obj).get(1))).get(0);
    point[1] = (int) ((JSONArray) (((JSONArray) obj).get(1))).get(1);

    //get level info
    JSONObject jo = (JSONObject) (((JSONArray) obj).get(0));

    //build a level use the read info
    Level level = levelBuilder(jo);

    //render the level
//    level.renderLevel(level);
    JSONObject output = level.checkForPoint(point);
    System.out.println(output);
  }
}

