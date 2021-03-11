import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class RuleChecker {
    boolean isLevelEnd = false;
    int gameStatus = 0; // 0 represents ongoing, 1 represents user wins, -1 represents lost

    // is the move valid for the Player
    public boolean isValidMove(Player p, Pair<Integer, Integer> pos) {
        return true;
    }

    // return the interaction of the Player
    public String hasInteractionPlayer(Player p, Pair<Integer, Integer> pos) {
        return "nothing";
    }

    // return the interaction of the Adversary
    public String hasInteractionAdversary(Adversary a, Pair<Integer, Integer> pos) {
        return "player";
    }

    // return all the traversable points of the given Player
    List<int[]> traversablePoints(Player p) {
        List<int[]> rev = new ArrayList<>();
        return rev;
    }

    // return if the current level is end
    public boolean isCurrentLevelEnd() {
        return isLevelEnd;
    }

    // return the status of game
    public int statusOfGame() {
        return  gameStatus;
    }

    // verify if the given GameState is valid
    boolean isValidState(GameState state) {
        return false;
    }
}
