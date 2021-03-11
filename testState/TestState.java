import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.util.Pair;

public class TestState {
  public static int[] point = new int[2];
  public static String name;

  public static void stateBuilder(JSONObject jo) {
    JSONObject l = jo.getJSONObject("level");
    Level level = TestLevel.levelBuilder(l);
    List<Level> levels = new ArrayList<>();
    levels.add(level);

    JSONArray jsonPlayers = jo.getJSONArray("players");
    JSONArray jsonAds = jo.getJSONArray("adversaries");

    List<Adversary> ads = new ArrayList<>();
    for (int ii = 0; ii < jsonPlayers.length(); ++ii) {
      JSONObject jAd = jsonPlayers.getJSONObject(ii);
      String name = jAd.getString("name");
      String type = jAd.getString("type");
      int x = ((JSONArray) jAd.get("position")).getInt(0);
      int y = ((JSONArray) jAd.get("position")).getInt(1);
      Adversary ad = new Adversary(type, name, new Pair<>(x,y));
      ads.add(ad);
    }

    List<Player> players = new ArrayList<>();
    for (int ii = 0; ii < jsonPlayers.length(); ++ii) {
      JSONObject p = jsonPlayers.getJSONObject(ii);
      String name = p.getString("name");
      int x = ((JSONArray) p.get("position")).getInt(0);
      int y = ((JSONArray) p.get("position")).getInt(1);
      Player player = new Player(name, new Pair<>(x,y));
      players.add(player);
    }


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
