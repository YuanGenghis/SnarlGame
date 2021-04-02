import java.util.Scanner;

public class LocalSnarl {

  public static void main(String[] args) {
    String fileName = "snarl.levels";
    int numberOfPlayer = 1;
    int levelToStart = 1;
    boolean isObserverMode = false;


    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--levels")) {
        fileName = args[++i];
        System.out.println("filename: " + fileName);
      } else if (args[i].equals("--players")) {
        numberOfPlayer = Integer.parseInt(args[++i]);
        System.out.println("numberOfPlayer: " + numberOfPlayer);

      } else if (args[i].equals("--start")) {
        levelToStart = Integer.parseInt(args[++i]);
        System.out.println("levelToStart: " + levelToStart);

      } else if (args[i].equals("--observe")) {
        isObserverMode = true;
        System.out.println("ObserverMode: " + isObserverMode);
      }
    }
  }
}
