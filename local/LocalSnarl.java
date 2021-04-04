import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class LocalSnarl {

  public static void main(String[] args) {
    String fileName = "snarl.levels";
    int numberOfPlayer = 1;
    int levelToStart = 1;
    boolean isObserverMode = false;
    List<String> usernames = new ArrayList<>();
    int naturalNum = 0;

    // read the arguments
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--levels")) {
        fileName = args[++i];
        System.out.println("filename: " + fileName);
      } else if (args[i].equals("--players")) {
        int num = Integer.parseInt(args[++i]);
        if (num <= 4 && num >= 1) {
          numberOfPlayer = num;
          System.out.println("numberOfPlayer: " + numberOfPlayer);
        } else {
          System.out.println("numberOfPlayer should be 1 <= N <= 4");
        }
      } else if (args[i].equals("--start")) {
        levelToStart = Integer.parseInt(args[++i]);
        System.out.println("levelToStart: " + levelToStart);

      } else if (args[i].equals("--observe")) {
        isObserverMode = true;
        System.out.println("ObserverMode: " + isObserverMode);
      }
    }

    // load user names
    Scanner scanner = new Scanner(System.in);
    for (int i = 0; i < numberOfPlayer; i++) {
      //  prompt for the user's name
      System.out.print("Enter player" + (i + 1) + "'s name: ");

      // get user input name as a String
      String name = scanner.next();
      usernames.add(name);
      System.out.println("Player" + (i + 1) + "'s name is " + name);
    }


    StringBuilder jsonFile = new StringBuilder();
    List<Level> levels = new ArrayList<>();
    try {
      File myObj = new File(fileName);
      Scanner myReader = new Scanner(myObj);
      naturalNum = myReader.nextInt();

      int index = 0;
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        if (data.contains("level")) {
          if (index != 0) {
            String jsonString = jsonFile.toString();
            JSONObject jo = new JSONObject(jsonString);
            Level l = TestLevel.levelBuilder(jo);
            levels.add(l);

            jsonFile = new StringBuilder();
          }
          ++index;
        }
        jsonFile.append(data);
      }
      String jsonString = jsonFile.toString();
      JSONObject jo = new JSONObject(jsonString);
      Level l = TestLevel.levelBuilder(jo);
      levels.add(l);

      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    // render user mode
    if (!isObserverMode) {
      User user1 = new User(naturalNum, levels, usernames);
      user1.pickLevel(levelToStart);
      user1.render();
    } else {
      Observer observer = new Observer(naturalNum, levels, usernames);
      observer.render();
    }

  }
}
