import com.sun.java.swing.plaf.windows.WindowsTextAreaUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.util.Pair;

public class RuleChecker {
    boolean isLevelEnd = false;
    int gameStatus = 0; // 0 represents ongoing, 1 represents user wins, -1 represents lost

    // is the move valid for the Player
    public static boolean isValidMove(Player p, Level level, int[] pos) {
        List<int[]> validPoints = traversablePoints(p, level);
        List<int[]> hallwayPoints = findHallwayPoints(level.hallways);
        for (int[] point: validPoints) {
            if (point[0] == pos[0] && point[1] == pos[1]) {
                return true;
            }
        }
        for (int[] point: hallwayPoints) {
            if (point[0] == pos[0] && point[1] == pos[1]) {
                return true;
            }
        }
        return false;
    }

    public static List<int[]> findHallwayPoints(List<Hallway> hws) {
        List<int[]> hwps = new ArrayList<>();
        for (Hallway hw: hws) {
            for (Pair<Integer, Integer> p: hw.layout) {
                int[] point = new int[2];
                point[0] = p.getKey(); point[1] = p.getValue();
                hwps.add(point);
            }
        }
        return hwps;
    }

    // return the interaction of the Player
    public static String hasInteractionPlayer(Player p, Level level, int[] pos) {
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
    static List<int[]> traversablePoints(Player p, Level level) {
        int[] position = new int[2];
        position[0] = p.getPosition().getKey();
        position[1] = p.getPosition().getValue();
        Room room = inWhichRoom(position, level);
        List<int[]> hallwayPoints = findHallwayPoints(level.hallways);
        if (room == null) {
//            System.out.println("in hallway");
            return searchTraversablePointsInHallway(position, level.hallways, level);
        } else {
            return searchTraversablePoints(position, room, hallwayPoints, level.rooms);
        }
    }


    // return if the current level is end
    public boolean isCurrentLevelEnd() {
        return isLevelEnd;
    }

    // return the status of game
    public int statusOfGame() {
        return  gameStatus;
    }

    // find which room the given position in
    public static Room inWhichRoom(int[] position, Level level) {
        return level.inWhichRoom(position);
    }


    public static List<int[]> searchTraversablePointsInHallway(int[] pos, List<Hallway> hws, Level level) {

        List<int[]> output = new ArrayList<>();
        List<int[]> hallwaysPoints = findHallwayPoints(level.hallways);
        int [][] view = getPlayerView(pos, null, hallwaysPoints, level.rooms);

        for (int ii = 0; ii < view.length; ++ii) {
            for (int yy = 0; yy < view[ii].length; ++yy) {
                if (view[ii][yy] !=  0) {
                    int[] p = new int[2];
                    p[0] = ii + pos[0] - 2;
                    p[1] = yy + pos[1] - 2;
                    output.add(p);
                }
            }
        }

        return output;

    }

    // search traversable points from a position in a Room
    public static List<int[]> searchTraversablePoints(int[] point, Room room,
                                                      List<int[]> hallwayPoints, List<Room> rooms) {
        List<int[]> output = new ArrayList<>();
        int[][] viewOfPlayer = getPlayerView(point, room, hallwayPoints, rooms);

//        for (int[] ii : viewOfPlayer) {
//            System.out.println(Arrays.toString(ii));
//        }

        for (int ii = 0; ii < viewOfPlayer.length; ++ii) {
            for (int yy = 0; yy < viewOfPlayer[ii].length; ++yy) {
                if (viewOfPlayer[ii][yy] !=  0) {
                    int[] p = new int[2];
                    p[0] = ii + point[0] - 2;
                    p[1] = yy + point[1] - 2;
                    output.add(p);
                }
            }
        }

//        for (int ii = 0; ii < output.size(); ++ii) {
//            System.out.println(Arrays.toString(output.get(ii)));
//        }

        return output;
    }

    public static boolean getPlayerViewInHallway(int[] pos, List<int[]> hallwayPoints) {
        for (int[] haps: hallwayPoints) {
            if (pos[0] == haps[0] && pos[1] == haps[1]) return true;
        }
        return false;
    }

    public static int checkRoomTailInsideHW(int[] pos, List<Room> rooms) {
        for (Room r: rooms) {
            int x = pos[0] - r.position.getKey();
            int y = pos[1] - r.position.getValue();
//            System.out.println("X:" + x);
//            System.out.println("y:" + x);
            if (x >= 0 && x < r.layout.length
                    && y >= 0 && y < r.layout[0].length) {
                if (r.layout[x][y] == '-' || r.layout[x][y] == '|') {
                    return 2;
                }
                else if (r.layout[x][y] == '.') {
                    return 1;
                }
            }
        }
        return 0;
    }

    public static int[][] getPlayerView(int[] pos, Room r, List<int[]> hallwayPoints, List<Room> rooms) {
        int[][] view = new int[5][5];

        int rows = 0;
        for (int ii = 2; ii > -3; --ii) {
            int cols = 0;
            for (int yy = 2; yy > -3; --yy) {
                int[] hp = new int[2];
                hp[0] = pos[0] - (2 - rows);
                hp[1] = pos[1] - (2 - cols);
                if (getPlayerViewInHallway(hp, hallwayPoints)) {
                    view[rows][cols] = 1;
                }
                else if (r == null) {
                    view[rows][cols] = checkRoomTailInsideHW(hp, rooms);
                }
                else if (pos[0] - ii < r.position.getKey()
                        || pos[1] - yy < r.position.getValue()
                        || pos[0] - ii >= r.position.getKey() + r.layout.length
                        || pos[1] - yy >= r.position.getValue() + r.layout[0].length) {
                    view[rows][cols] = 0;
                }
                else if (r.layout[(pos[0] - ii) - r.position.getKey()]
                        [(pos[1] - yy) - r.position.getValue()] == 'x') {
                    view[rows][cols] = 0;
                } else if (r.layout[(pos[0] - ii) - r.position.getKey()]
                        [(pos[1] - yy) - r.position.getValue()] == '-'
                        || r.layout[(pos[0] - ii) - r.position.getKey()]
                        [(pos[1] - yy) - r.position.getValue()] == '|') {
                    view[rows][cols] = 2;
                } else if (r.layout[(pos[0] - ii) - r.position.getKey()]
                        [(pos[1] - yy) - r.position.getValue()] == 'P') {
                    view[rows][cols] = 3;
                }
                else if (r.layout[(pos[0] - ii) - r.position.getKey()]
                        [(pos[1] - yy) - r.position.getValue()] == 'A') {
                    view[rows][cols] = -1;
                }
                else if (r.layout[(pos[0] - ii) - r.position.getKey()]
                        [(pos[1] - yy) - r.position.getValue()] == 'E') {
                    view[rows][cols] = 5;
                }
                else {
                    view[rows][cols] = 1;
                }
                ++cols;
            }
            ++rows;
        }
        return view;
    }
}
