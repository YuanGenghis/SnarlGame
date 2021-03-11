import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Scanner;

public class TestState {
  public static int[] point = new int[2];
  public static String name;

  public static void stateBuilder(JSONObject jo) {
    JSONObject l = jo.getJSONObject("level");
    Level level = TestLevel.levelBuilder(l);

//    String playerName = jo.getJSONObject("players")

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

    name = (String) ((JSONArray) obj).get(2);
    point[0] = (int) ((JSONArray) (((JSONArray) obj).get(2))).get(0);
    point[1] = (int) ((JSONArray) (((JSONArray) obj).get(2))).get(1);

    JSONObject jo = (JSONObject) (((JSONArray) obj).get(0));

//    GameState state = stateBuilder(jo);
//    JSONObject output = level.checkForPoint(point);
  }
}
