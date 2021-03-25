import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.*;

import javafx.util.Pair;

public class TestManager {

  public static GameManager managerBuilder(JSONArray ja) {
    JSONArray nameList = (JSONArray) ja.get(0);
    List<String> names = new ArrayList<>();
    for (int ii = 0; ii < nameList.length(); ++ii) {
      names.add(nameList.getString(ii));
    }
    Level level = TestLevel.levelBuilder(ja.getJSONObject(1));
    int natural = ja.getInt(2);
    JSONArray pointList = (JSONArray) ja.get(3);
    List<int[]> points = new ArrayList<>();
    for (int ii = 0; ii < pointList.length(); ++ii) {
      int[] p = new int[2];
      p[0] = ((JSONArray)pointList.get(ii)).getInt(0);
      p[1] = ((JSONArray)pointList.get(ii)).getInt(1);
      points.add(p);
    }
    JSONArray moveList = ja.getJSONArray(4);
    List<Player> players = new ArrayList<>();
    for (int ii = 0; ii < names.size(); ++ii) {
      Pair<Integer, Integer> position = new Pair<>(points.get(ii)[0], points.get(ii)[1]);
      Player p = new Player(names.get(ii), position);
      players.add(p);
    }

    GameManager gameManager = new GameManager(players, level, players.get(0));
    return gameManager;

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

    JSONArray ja = (JSONArray) jt.nextValue();

    GameManager gameManager = managerBuilder(ja);

//    //check if the move valid
//    JSONArray output = state.checkForPoint(name, point, jo);
//    System.out.println(output);
  }
}
