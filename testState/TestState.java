import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class TestState {
  public static int[] point = new int[2];
  public static String name;

  public static GameState stateBuilder(JSONObject jo) {
    JSONObject l = jo.getJSONObject("level");
    //build a level using json input
    Level level = TestLevel.levelBuilder(l);

    //transfer players and adversaries
    JSONArray jsonPlayers = jo.getJSONArray("players");
    JSONArray jsonAds = jo.getJSONArray("adversaries");

    List<Zombie> ads = new ArrayList<>();
    for (int ii = 0; ii < jsonAds.length(); ++ii) {
      JSONObject jAd = jsonAds.getJSONObject(ii);
      String name = jAd.getString("name");
      String type = jAd.getString("type");
      int x = ((JSONArray) jAd.get("position")).getInt(0);
      int y = ((JSONArray) jAd.get("position")).getInt(1);
      int[] p = new int[2]; p[0] = x; p[1] = y;
      Zombie ad = new Zombie(p);
      ads.add(ad);
    }
    level.setZombiesInLevel(ads.size(), new ArrayList<>());
    List<Level> levels = new ArrayList<>();
    levels.add(level);

    List<Player> players = new ArrayList<>();
    for (int ii = 0; ii < jsonPlayers.length(); ++ii) {
      JSONObject p = jsonPlayers.getJSONObject(ii);
      String name = p.getString("name");
      int x = ((JSONArray) p.get("position")).getInt(0);
      int y = ((JSONArray) p.get("position")).getInt(1);
      int[] p2 = new int[2]; p2[0] = x; p2[1] = y;
      Player player = new Player(name, p2);
      players.add(player);
    }

    return new GameState(players, levels);

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

    String name = (String) ((JSONArray) obj).get(1).toString();
    point[0] = (int) ((JSONArray) (((JSONArray) obj).get(2))).get(0);
    point[1] = (int) ((JSONArray) (((JSONArray) obj).get(2))).get(1);

    JSONObject jo = (JSONObject) (((JSONArray) obj).get(0));

    //build a state by using json input
    GameState state = stateBuilder(jo);
    //check if the move valid
    JSONArray output = state.checkForPoint(name, point, jo);
    System.out.println(output);
  }
}
