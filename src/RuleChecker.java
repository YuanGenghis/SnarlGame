import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class RuleChecker {
    boolean isLevelEnd = false;
    int gameStatus = 0; // 0 represents ongoing, 1 represents user wins, -1 represents lost

    // is the move valid for the Player
    public boolean isValidMove(Player p, Level level, int[] pos) {
        List<int[]> validPoints = traversablePoints(p, level);
        for (int[] point: validPoints) {
            if (point[0] == pos[0] && point[1] == pos[1]) {
                return true;
            }
        }
        return false;
    }

    // return the interaction of the Player
    public String hasInteractionPlayer(Player p, Level level, int[] pos) {
        if (!isValidMove(p, level, pos)) {
            return "Invalid Move";
        }
        else if (level.checkIfOnAd(pos)) {
            return "Adversary";
        }
        else if (level.checkIfKeyOrExit(pos) == 1) {
            return "Key";
        }
        else if (level.checkIfKeyOrExit(pos) == 2) {
            return "Exit";
        }
        return "nothing";
    }

    // return the interaction of the Adversary
    public String hasInteractionAdversary(List<Player> players, Adversary a, Level level, int[] pos) {
        if (level.checkIfOnPlayer(players, pos)) {
            return "Player";
        }
        return "null";
    }

    // return all the traversable points of the given Player
    List<int[]> traversablePoints(Player p, Level level) {
        int[] position = new int[2];
        position[0] = p.getPosition().getKey();
        position[1] = p.getPosition().getValue();
        Room room = isInRoom(position, level);
        List<int[]> rev = searchTraversablePoints(position, room);
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

    public Room isInRoom(int[] position, Level level) {
        for (Room room: level.rooms) {
            int rows = room.layout.length;
            int cols = room.layout[0].length;
            int positionX = room.position.getKey();
            int positionY = room.position.getValue();

            if (positionY <= position[1] && position[1] <= positionY + rows) {
                if (positionX <= position[0] && position[0] <= positionX + cols) {
                    return room;
                }
            }
        }
        return null;
    }

    public static List<int[]> searchTraversablePoints(int[] point, Room room) {
        int[] origin = new int[2];
        origin[0] = room.position.getKey();
        origin[1] = room.position.getValue();
        int row = point[0] - origin[0];
        int col = point[1] - origin[1];
        int rows = room.layout.length;
        int cols = room.layout[0].length;
        List<int[]> output = new ArrayList<>();
        int[] p = new int[2];

        if (row - 1 >= 0 && room.layout[row - 1][col] != 'x') {
            p[0] = row-1;
            p[1] = col;
            output.add(p);
        }

        if (col - 1 >= 0 && room.layout[row][col - 1] != 'x') {
            p[0] = row;
            p[1] = col-1;
            output.add(p);
        }

        if (col + 1 < cols  && room.layout[row][col + 1] != 0) {
            p[0] = row;
            p[1] = col+1;
            output.add(p);
        }

        if (row + 1 < rows && room.layout[row + 1][col] != 0) {
            p[0] = row+1;
            p[1] = col;
            output.add(p);
        }

        return output;
    }
}
