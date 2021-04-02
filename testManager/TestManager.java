import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.*;

import javafx.util.Pair;

public class TestManager {

  public static JSONArray managerBuilder(JSONArray ja) {
    JSONArray output = new JSONArray();

    JSONArray moveList = ja.getJSONArray(4);
    List<JSONArray> moves = new ArrayList<>();
    for (int ii = 0; ii < moveList.length(); ++ii) {
      moves.add(moveList.getJSONArray(ii));
    }
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

    List<Player> players = new ArrayList<>();
    for (int ii = 0; ii < names.size(); ++ii) {
      Pair<Integer, Integer> position = new Pair<>(points.get(ii)[0], points.get(ii)[1]);
      Player p = new Player(names.get(ii), position);
      players.add(p);
    }

    List<int[]> adPositions = new ArrayList<>();
    int playerAmount = nameList.length();
    for (int ii = playerAmount; ii < points.size(); ++ii) {
      int[] p = new int[2];
      p[0] = ((JSONArray) pointList.get((ii))).getInt(0);
      p[1] = ((JSONArray) pointList.get((ii))).getInt(1);
      adPositions.add(p);
    }
    for (int[] position: adPositions) {
      Pair<Integer, Integer> p = new Pair<>(position[0], position[1]);
      Zombie ad = new Zombie(p);
      level.addAd(ad);
    }

    GameManager gameManager = new GameManager(players, level,0);
    JSONArray moveResults = gameManager.checkForMove(moves, natural);

    JSONObject levelObj = ja.getJSONObject(1);
    JSONObject updateLevel = new JSONObject();
    JSONObject stateObj = new JSONObject();

    if (!level.ifLocked) {
      updateLevel.put("type", "level");
      updateLevel.put("rooms", levelObj.getJSONArray("rooms"));
      updateLevel.put("hallways", levelObj.getJSONArray("hallways"));
      JSONObject exit = levelObj.getJSONArray("objects").getJSONObject(0);
      JSONArray updateObj = new JSONArray().put(exit);
      updateLevel.put("objects", updateObj);
    } else {
      updateLevel = levelObj;
    }
    stateObj.put("type", "state");
    stateObj.put("level", updateLevel);
    //for player
    JSONArray actorPositionList = new JSONArray();
    //for adversary
    JSONArray actorPositionList2 = new JSONArray();
    for (int ii = 0; ii < nameList.length(); ++ii) {
      if (gameManager.players.get(ii).status != -1) {
        JSONObject actor = new JSONObject();
        actor.put("type", "player");
        actor.put("name", names.get(ii));
        int[] updatePos = new int[2];
        updatePos[0] = gameManager.players.get(ii).position.getKey();
        updatePos[1] = gameManager.players.get(ii).position.getValue();
        actor.put("position", updatePos);
        actorPositionList.put(actor);
      }
    }
    stateObj.put("players", actorPositionList);
    int adAmount = 0;
    for (int ii = nameList.length(); ii < pointList.length(); ++ii) {
      ++adAmount;
      JSONObject actor = new JSONObject();
      actor.put("type", "ghost");
      actor.put("name", "ghost"+adAmount);
      actor.put("position", pointList.get(ii));
      actorPositionList2.put(actor);
    }
    stateObj.put("adversaries", actorPositionList2);
    stateObj.put("exit-locked", level.ifLocked);


    output.put(stateObj);
    output.put(moveResults);
    return output;

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


    System.out.println(managerBuilder(ja));

  }
}
