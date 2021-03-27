import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

import javafx.util.Pair;

// represents a Level of the game
public class Level extends JPanel implements KeyListener {
  List<Room> rooms;
  List<Hallway> hallways;
  List<Adversary> ads;
  int[] keyPosition;
  int[] exitPosition;
  boolean ifLocked;

  public static BufferedImage PlayerImage;
  public static BufferedImage ADImage;
  private static String ADUrl =
//          "https://avatars.githubusercontent.com/u/60799921?s=400&u=4d68d8d6c5acd9a4b48ef35dc7d3a0b9a8164d04&v=4";
          "https://images-na.ssl-images-amazon.com/images/I/71vj4KrX%2BvL._AC_SL1500_.jpg";
  private static String PlayerUrl =
          "https://avatars.githubusercontent.com/u/46980128?s=400&u=abab5bff473ece8159ceb6f29ebf7cf3fc132e2b&v=4";

  public static final char[][] room1Tails = {
          {'x','x','x','x','x'},
          {'x','.','.','.','x',},
          {'x','.','.','.','|'},
          {'x','.','.','.','x',},
          {'x','x','-','x','x'}};
  public static final Pair<Integer, Integer> room1Position = new Pair<>(0,0);
  public static final Room room1 = new Room(room1Tails, room1Position);


  public static final char[][] room2Tails = {
          {'x','x','x','x','x'},
          {'x','.','.','.','x',},
          {'|','.','.','.','x'},
          {'x','.','.','.','x',},
          {'x','x','x','x','x'}};
  public static final Pair<Integer, Integer> room2Position = new Pair<>(8,0);
  public static final Room room2 = new Room(room2Tails, room2Position);


  public static final char[][] room3Tails = {
          {'x','x','-','x','x'},
          {'x','.','.','.','x',},
          {'x','.','.','.','x'},
          {'x','.','.','.','x',},
          {'x','.','.','.','x'},
          {'x','x','x','x','x'}};
  public static final Pair<Integer, Integer> room3Position = new Pair<>(0,8);
  public static final Room room3 = new Room(room3Tails, room3Position);

  public static final List<Pair<Integer, Integer>> tilesPosition1 =
          new ArrayList<>(Arrays.asList(
          new Pair<>(5,2), new Pair<>(6,2), new Pair<>(7,2)
  ));
  public static final Pair<Integer, Integer> room1door1 = new Pair<>(4, 2);
  public static final Pair<Integer, Integer> room2door1 = new Pair<>(8, 2);
  public static final Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> hw1Connect =
          new Pair<>(room1door1, room2door1);
  public static final List<Pair<Integer, Integer>> hw1Waypoints =
          new ArrayList<>(Arrays.asList(new Pair<>(6,2)));
  public static final Hallway hw1 = new Hallway(tilesPosition1, hw1Connect, hw1Waypoints);

  public static final List<Pair<Integer, Integer>> tilesPosition2 =
          new ArrayList<>(Arrays.asList(
                  new Pair<>(2,5), new Pair<>(2,6), new Pair<>(2,7)
          ));
  public static final Pair<Integer, Integer> room1door2 = new Pair<>(2, 4);
  public static final Pair<Integer, Integer> room3door1 = new Pair<>(2, 8);
  public static final Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> hw2Connect =
          new Pair<>(room1door2, room3door1);
  public static final List<Pair<Integer, Integer>> hw2Waypoints =
          new ArrayList<>(Arrays.asList(new Pair<>(2,6)));
  public static final Hallway hw2 = new Hallway(tilesPosition2, hw2Connect, hw2Waypoints);

  public static final Pair<Integer, Integer> aPosition = new Pair<>(3,12);
  public static final Adversary ad1 = new Adversary(aPosition);


  // construct the level example
  public Level() {
    this.rooms = new ArrayList<>();
    this.hallways = new ArrayList<>();
    this.rooms.add(room1);
    this.rooms.add(room2);
    this.rooms.add(room3);
    this.hallways.add(hw1);
    this.hallways.add(hw2);
    this.ads = new ArrayList<>();
    this.ads.add(ad1);
    this.init();
    this.keyPosition = new int[2];
    this.exitPosition = new int[2];
    this.ifLocked = true;
  }

  // constructs the level with Given rooms and hallways
  public Level(List<Room> rooms, List<Hallway> hallways) {
    this.rooms = rooms;
    this.hallways = hallways;
    this.ads = new ArrayList<>();
    this.ifLocked = true;
  }

  // init the level
  public void init() {
//    this.setAds();
    this.setExit();
  }

  public void addAd(Adversary ad) {
    this.ads.add(ad);
  }

  // set Adversary by amount
  public void setAds(int amount) {
//    for (int ii = 0; ii < this.ads.size(); ++ii) {
//      int x = this.ads.get(ii).getPosition().getKey();
//      int y = this.ads.get(ii).getPosition().getValue();
//      Room r = this.rooms.get(2);
//      r.layout[y - r.position.getValue()][x - r.position.getKey()] = 'A';
//    }
    Room r = this.rooms.get(2);
    int length = r.layout[0].length;
    for (int ii = r.layout.length; ii > 0; --ii) {
      for (int jj = length; jj > 0; --jj) {
        if (r.layout[ii-1][jj-1] == '.') {
          if (amount > 0) {
            r.layout[ii-1][jj-1] = 'A';
            amount--;
          } else {
            break;
          }
        }
      }
    }
  }

  // set Exit position
  public void setExit() {
    this.rooms.get(1).layout[3][3] = 'E';
  }

  // add a Room to current Level
  public void addRoom(Room room) {
    this.rooms.add(room);
  }

  // add a Hallway to current Level
  public void addHallways(Hallway hw) {
    this.hallways.add(hw);
  }

  public int rectWidth = 25;

  // Add a list of Adversary to the current Level
  public void addAds(List<Adversary> ads) {
    for (int ii = 0; ii < ads.size(); ++ii) {
      this.ads.add(ads.get(ii));
    }
  }

  @Override
  // render the Level
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.clearRect(0, 0, getWidth(), getHeight());

    drawRooms(this.rooms, g);

    drawHallways(this.hallways, g);
  }

  // draw Hallways
  private void drawHallways(List<Hallway> hallways, Graphics g) {
//    for (Hallway hw: hallways) {
//      int x = hw.position.getKey();
//      int y = hw.position.getValue();
//      int width = hw.layout[0].length;
//      int height = hw.layout.length;
//      for (int ii = y; ii < height + y; ++ii) {
//        for (int j = x; j < width + x; ++j) {
//          int xx = ii * rectWidth;
//          int yy = j * rectWidth;
//
//          if (hw.layout[ii - y][j - x] == 'x') {
//            g.setColor(Color.DARK_GRAY);
//          } else if (hw.layout[ii - y][j - x] == '.') {
//            g.setColor(Color.GRAY);
//          } else if (hw.layout[ii - y][j - x] == '|' || hw.layout[ii - y][j - x] == '-') {
//            g.setColor(Color.CYAN);
//          }
//          g.fillRect(yy, xx, rectWidth, rectWidth);
//          g.setColor(Color.black);
//          g.drawRect(yy, xx, rectWidth, rectWidth);
//        }
//      }
//    }
    for (Hallway hw: hallways) {
      for (Pair p : hw.layout) {
        g.setColor((Color.GRAY));
        g.fillRect(((int)p.getKey()) * rectWidth, ((int)p.getValue()) * rectWidth, rectWidth, rectWidth);
        g.setColor((Color.blue));
        g.drawRect(((int)p.getKey()) * rectWidth, ((int)p.getValue()) * rectWidth, rectWidth, rectWidth);
      }
    }
  }

  // Draw rooms
  private void drawRooms(List<Room> rooms, Graphics g) {
    for (Room r: rooms) {
      int x = r.position.getKey();
      int y = r.position.getValue();
      int width = r.layout[0].length;
      int height = r.layout.length;
      for (int ii = y; ii < height+y; ++ii) {
        for (int j = x; j < width+x; ++j) {
          int xx = ii * rectWidth;
          int yy = j * rectWidth;

          if (r.layout[ii-y][j-x] == 'x'){
            g.setColor(Color.DARK_GRAY);
          } else if (r.layout[ii-y][j-x] == '.') {
            g.setColor(Color.GRAY);
          } else if (r.layout[ii-y][j-x] == '|' || r.layout[ii-y][j-x] == '-') {
            g.setColor(Color.CYAN);
          } else {
            g.setColor(Color.GRAY);
          }
          g.fillRect(yy, xx, rectWidth, rectWidth);
          g.setColor(Color.black);
          g.drawRect(yy, xx, rectWidth, rectWidth);
          if (r.layout[ii-y][j-x] == 'P') {
            try {
              URL url = new URL(PlayerUrl);
              PlayerImage = ImageIO.read(url);
            }
            catch(IOException e) {
              System.out.println("Image not found");
            }

            g.drawImage(PlayerImage, rectWidth* j, rectWidth *ii,
                    rectWidth -1, rectWidth -1, null);
          }
          else if (r.layout[ii-y][j-x] == 'A') {
            try {
              URL url = new URL(ADUrl);
              ADImage = ImageIO.read(url);
            }
            catch(IOException e) {
              System.out.println("Image not found");
            }
            g.drawImage(ADImage, rectWidth* j, rectWidth * ii,
                    rectWidth -1, rectWidth - 1, null);
          }
          else if (r.layout[ii-y][j-x] == 'E') {
            g.setColor(Color.RED);
            Font tr = new Font("TimesRoman", Font.PLAIN, 12);
            g.setFont(tr);
            g.drawString("E", rectWidth* j + 10, rectWidth *ii + 15);
          }
        }
      }
    }
  }

  // get the position of the room
  public Pair<Integer, Integer> getRoomPosition(int[] point) {
    for (Room room: rooms) {
      int rows = room.layout.length;
      int cols = room.layout[0].length;
      int positionX = room.position.getKey();
      int positionY = room.position.getValue();

      if (positionY <= point[1] && point[1] < positionY + rows) {
        if (positionX <= point[0] && point[0] < positionX + cols) {
          return room.position;
        }
      }
    }
    return new Pair<>(-1,-1);
  }

  public JSONObject checkForPoint(int[] point) {
    JSONObject output = new JSONObject();
    int type = checkTailType(point);
    if (type == -1) {
      if (checkIfInHallways(point) != -1) {
        Pair<Integer, Integer> from = hallways.get(checkIfInHallways(point)).connection.getKey();
        Pair<Integer, Integer> to = hallways.get(checkIfInHallways(point)).connection.getValue();
        JSONArray ja = new JSONArray();
        int[] reachable = new int[2];
        int[] reachable2 = new int[2];
        reachable[0] = from.getKey();
        reachable[1] = from.getValue();
        Pair<Integer, Integer> position1 = getRoomPosition(reachable);
        reachable[0] = position1.getKey();
        reachable[1] = position1.getValue();
        ja.put(reachable);

        reachable2[0] = to.getKey();
        reachable2[1] = to.getValue();
        Pair<Integer, Integer> position2 = getRoomPosition(reachable2);
        reachable2[0] = position2.getKey();
        reachable2[1] = position2.getValue();
        ja.put(reachable2);
        output.put("traversable", true);
        output.put("object", JSONObject.NULL);
        output.put("type", "hallway");
        output.put("reachable", ja);
      } else {
        JSONArray ja = new JSONArray();
        output.put("traversable", false);
        output.put("object", JSONObject.NULL);
        output.put("type", "void");
        output.put("reachable", ja);
      }
    }
    else {
      Pair<Integer, Integer> roomPosition = getRoomPosition(point);
      Room r = new Room(0,0);
      for (Room room: rooms) {
        if (roomPosition == room.position) {
          r = room;
        }
      }
      List<int[]> reachables = reachableRooms(r);
      if (type == 1) {
        output.put("traversable", true);
        if (checkIfKeyOrExit(point) == 1) {
          output.put("object", "key");
        } else if (checkIfKeyOrExit(point) == 2) {
          output.put("object", "exit");
        } else {
          output.put("object", JSONObject.NULL);
        }
      }
      else if (type == 2) {
        output.put("traversable", true);
        output.put("object", JSONObject.NULL);
      }
      output.put("type", "room");
      output.put("reachable", reachables);
    }
    return output;
  }

  public List<int[]> reachableRooms(Room room) {
    List<int[]> neighbors = new ArrayList<>();
    List<int[]> doorsPosition = new ArrayList<>();
    for (int ii = 0; ii < room.layout.length; ++ii) {
      for (int jj = 0; jj < room.layout[0].length; ++jj) {
        if (room.layout[ii][jj] == '|' || room.layout[ii][jj] == '-') {
          int[] p = new int[2];
          p[0] = ii + room.position.getKey(); p[1] = jj + room.position.getValue();
          doorsPosition.add(p);
        }
      }
    }
    for (int ii = 0; ii < doorsPosition.size(); ++ii) {
      int[] position = doorsPosition.get(ii);
      int hwNum = checkIfInHallways(position);
      Hallway hw = hallways.get(hwNum);
      int[] d = new int[2];
      int[] rPosition = new int[2];
      if (position[0] != hw.connection.getKey().getKey() ||
              position[1] != hw.connection.getKey().getValue()) {
        d[0] = hw.connection.getKey().getKey(); d[1] = hw.connection.getKey().getValue();
      }
      else {
        d[0] = hw.connection.getValue().getKey(); d[1] = hw.connection.getValue().getValue();
      }
      rPosition[0] = getRoomPosition(d).getKey(); rPosition[1] = getRoomPosition(d).getValue();
      neighbors.add(rPosition);
    }
    return neighbors;
  }

  public int checkIfKeyOrExit(int[] point) {
    if (point[0] == keyPosition[0] && point[1] == keyPosition[1]) {
      //represent key
      return 1;
    }
    else if (point[0] == exitPosition[0] && point[1] == exitPosition[1]) {
      //represent exit
      return 2;
    }
    return 0;
  }

  public boolean checkIfOnAd(int[] point) {
    for (Adversary ad: this.ads) {
      if (ad.getPosition().getKey() == point[0] && ad.getPosition().getValue() == point[1]) {
        return true;
      }
    }
    return false;
  }

  public boolean checkIfOnPlayer(List<Player> players, int[] point) {
    for (Player p : players) {
      if (p.getPosition().getKey() == point[0] && p.getPosition().getValue() == point[1]) {
        return true;
      }
    }
    return false;
  }


  public int checkIfInHallways(int[] point) {
    for (int ii = 0; ii < hallways.size(); ++ii) {
      for (Pair p: hallways.get(ii).layout) {
        if ((int)p.getKey() == point[0] && (int)p.getValue() == point[1]) {
          //which hallway
          return ii;
        }
      }
      if (hallways.get(ii).connection.getKey().getKey() == point[0]
              && hallways.get(ii).connection.getKey().getValue() == point[1]) {
        return ii;
      } else if (hallways.get(ii).connection.getValue().getKey() == point[0]
              && hallways.get(ii).connection.getValue().getValue() == point[1]) {
        return ii;
      }
    }
    return -1;
  }

  public int checkTailType(int[] point) {
    for (Room room: rooms) {
      int rows = room.layout.length;
      int cols = room.layout[0].length;
      int positionX = room.position.getKey();
      int positionY = room.position.getValue();


      if (positionY <= point[1] && point[1] <= positionY + rows) {
        if (positionX <= point[0] && point[0] <= positionX + cols) {
          int insideX = point[0] - positionX;
          int insideY = point[1] - positionY;
          for (int ii = 0; ii < rows; ++ii) {
            for (int jj = 0; jj < cols; ++jj) {
              if (room.layout[insideX][insideY] == 'x') {
                return 0;
              }
              else if (room.layout[insideX][insideY] == '.') {
                return 1;
              }
              else if (room.layout[insideX][insideY] == '|'
                      || room.layout[insideX][insideY] == '-') {
                return 2;
              }
            }
          }
        }
      }
    }
    return -1;
  }



  public Pair<Integer, Integer> setPlayer() {
//    for (int ii = 0; ii < players.size(); ++ii) {
//      int x = players.get(ii).getPosition().getKey();
//      int y = players.get(ii).getPosition().getValue();
//      this.rooms.get(0).layout[y][x] = 'P';
//    }
    Room r = this.rooms.get(0);
    for (int ii = 0; ii < r.layout.length; ++ii) {
      for (int jj = 0; jj < r.layout[0].length; ++jj) {
        if (r.layout[ii][jj] == '.') {
          r.layout[ii][jj] = 'P';
          return new Pair<>(ii,jj);
        }
      }
    }
    return new Pair<>(0,0);
  }

  public void movePlayer(Player player, Pair<Integer, Integer> newPosition) {
    Pair<Integer, Integer> oldPosition = player.getPosition();
    Pair<Integer, Integer> dst;
    for (Room r: this.rooms) {
      if (oldPosition.getKey() > r.position.getKey() &&
              oldPosition.getValue() > r.position.getValue()) {
        dst = new Pair<>(newPosition.getKey() - r.position.getKey(),
                newPosition.getValue() - r.position.getValue());
        if (r.layout[dst.getValue() - r.position.getValue()]
                [dst.getKey() - r.position.getKey()] == '.') {
          r.layout[oldPosition.getValue() - r.position.getValue()]
                  [oldPosition.getKey() - r.position.getKey()] = '.';
          r.layout[dst.getValue()][dst.getKey()] = 'P';
          player.position = newPosition;
        }
      }
    }
  }


  public void renderLevel(Level level) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JFrame frame = new JFrame("Game");
        frame.add(level);
        frame.addKeyListener(level);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
        frame.setVisible(true);
        frame.setSize(2000,2000);
      }
    });
  }

  @Override
  public void keyTyped(KeyEvent e) {

  }

  @Override
  public void keyPressed(KeyEvent e) {
    int keyCode = e.getKeyCode();
    switch( keyCode ) {
      case KeyEvent.VK_UP:
        // handle up
        System.out.println(1);
        break;
      case KeyEvent.VK_DOWN:
        // handle down
        break;
      case KeyEvent.VK_LEFT:
        // handle left
        break;
      case KeyEvent.VK_RIGHT :
        // handle right
        break;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {

  }
}
