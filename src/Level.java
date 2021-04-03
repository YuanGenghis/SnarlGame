import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.Random;

// represents a Level of the game
public class Level extends JPanel{
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
  static String Ghost =
          "https://avatars.githubusercontent.com/u/60799921?s=400&u=4d68d8d6c5acd9a4b48ef35dc7d3a0b9a8164d04&v=4";


  public void initDefult() {
    char[][] room1Tails = {
            {'x','x','x','x','x'},
            {'x','.','.','.','x',},
            {'x','.','.','.','|'},
            {'x','.','.','.','x',},
            {'x','x','-','x','x'}};
    int[] room1Position = new int[2];
    room1Position[0] = 0; room1Position[1] = 0;
    Room room1 = new Room(room1Tails, room1Position);


    char[][] room2Tails = {
            {'x','x','x','x','x'},
            {'x','.','.','.','x',},
            {'|','.','.','.','x'},
            {'x','.','.','.','x',},
            {'x','x','x','x','x'}};
    int[] room2Position = new int[2];
    room2Position[0] = 0; room2Position[1] = 8;
    Room room2 = new Room(room2Tails, room2Position);


    final char[][] room3Tails = {
            {'x','x','-','x','x'},
            {'x','.','.','.','x',},
            {'x','.','.','.','x'},
            {'x','.','.','.','x',},
            {'x','.','.','.','x'},
            {'x','x','x','x','x'}};
    int[] room3Position = new int[2];
    room3Position[0] = 8; room3Position[1] = 0;
    Room room3 = new Room(room3Tails, room3Position);


    int[] hw1p1 = new int[2]; hw1p1[0] = 2; hw1p1[1] = 5;
    int[] hw1p2 = new int[2]; hw1p2[0] = 2; hw1p2[1] = 6;
    int[] hw1p3 = new int[2]; hw1p3[0] = 2; hw1p3[1] = 7;
    List<int[]> tilesPosition1 =
            new ArrayList<>(Arrays.asList(hw1p1, hw1p2, hw1p3));
    int[] room1door1 = new int[2]; room1door1[0] = 2; room1door1[1] = 4;
    int[] room2door1 = new int[2]; room2door1[0] = 2; room2door1[1] = 8;

    int[] hw1wp1 = new int[2]; hw1wp1[0] = 2; hw1wp1[1] = 6;
    List<int[]> hw1Waypoints =
            new ArrayList<>(Arrays.asList(hw1wp1));
    Hallway hw1 = new Hallway(tilesPosition1, hw1Waypoints);

    int[] hw2p1 = new int[2]; hw2p1[0] = 5; hw2p1[1] = 2;
    int[] hw2p2 = new int[2]; hw2p2[0] = 6; hw2p2[1] = 2;
    int[] hw2p3 = new int[2]; hw2p3[0] = 7; hw2p3[1] = 2;

    List<int[]> tilesPosition2 =
            new ArrayList<>(Arrays.asList(hw2p1, hw2p2, hw2p3));
    int[] room1door2 = new int[2]; room1door2[0] = 4; room1door2[1] = 2;
    int[] room3door1 = new int[2]; room3door1[0] = 8; room3door1[1] = 2;
    int[] hw2wp1 = new int[2]; hw2wp1[0] = 6; hw2wp1[1] = 2;
    List<int[]> hw2Waypoints =
            new ArrayList<>(Arrays.asList(hw2wp1));
    Hallway hw2 = new Hallway(tilesPosition2, hw2Waypoints);

    int[] aPosition = new int[2]; aPosition[0] = 12; aPosition[1] = 3;
    Zombie ad1 = new Zombie(aPosition);

    this.rooms = new ArrayList<>();
    this.hallways = new ArrayList<>();
    this.rooms.add(room1);
    this.rooms.add(room2);
    this.rooms.add(room3);
    this.hallways.add(hw1);
    this.hallways.add(hw2);
    this.ads = new ArrayList<>();
    this.ads.add(ad1);
    this.keyPosition = new int[2];
    this.exitPosition = new int[2];
    this.ifLocked = true;
  }


  // construct the level example
  public Level() {
    this.initDefult();
    this.ads = new ArrayList<>();
  }

  // constructs the level with Given rooms and hallways
  public Level(List<Room> rooms, List<Hallway> hallways) {
    this.rooms = rooms;
    this.hallways = hallways;
    this.ads = new ArrayList<>();
    this.ifLocked = true;
  }

  public void addAd(Zombie ad) {
    this.ads.add(ad);
  }


  public void moveAds(int adversary, int[] pos) {
    Adversary ad = this.ads.get(adversary);
    Room r = inWhichRoom(pos);
    if (r.layout[pos[0] - r.position[0]][pos[1] - r.position[1]] == 'G'
    || r.layout[pos[0] - r.position[0]][pos[1] - r.position[1]] == 'Z') {
      r.layout[pos[0] - r.position[0]][pos[1] - r.position[1]] = '.';
    }
  }

  // set Exit position
  public void setExit(int[] pos) {
    Room r = inWhichRoom(pos);
    r.layout[pos[0] - r.position[0]][pos[1] - r.position[1]] = 'E';
    exitPosition = pos;
  }

  // set Exit position
  public void setKey(int[] pos) {
    Room r = inWhichRoom(pos);
    r.layout[pos[0] - r.position[0]][pos[1] - r.position[1]] = 'K';
    keyPosition = pos;
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

//  // Add a list of Adversary to the current Level
//  public void addAds(List<Zombie> ads) {
//    for (int ii = 0; ii < ads.size(); ++ii) {
//      this.ads.add(ads.get(ii));
//    }
//  }


  // draw Hallways
  public void drawHallways(Graphics g) {
    for (Hallway hw: hallways) {
      for (int[] p : hw.layout) {
        int xx = p[0] * rectWidth;
        int yy = p[1] * rectWidth;
        if (!hw.ifPlayerInside) {
          g.setColor((Color.GRAY));
          g.fillRect(yy, xx, rectWidth, rectWidth);
          g.setColor((Color.blue));
          g.drawRect(yy, xx, rectWidth, rectWidth);
        } else {
          if (p[0] == hw.playerPosition[0] && p[1] == hw.playerPosition[1]) {
//            System.out.println("in hw");
            try {
              URL url = new URL(PlayerUrl);
              PlayerImage = ImageIO.read(url);
            }
            catch(IOException e) {
              System.out.println("Image not found");
            }
            g.drawImage(PlayerImage, yy, xx,
                    rectWidth -1, rectWidth -1, null);
          }
          else {
            g.setColor((Color.GRAY));
            g.fillRect(yy, xx, rectWidth, rectWidth);
            g.setColor((Color.blue));
            g.drawRect(yy, xx, rectWidth, rectWidth);
          }
        }
      }
    }
  }

  // Draw rooms
  public void drawRooms(Graphics g) {
    for (Room r: rooms) {
      int x = r.position[0];
      int y = r.position[1];
      int width = r.layout[0].length;
      int height = r.layout.length;
      for (int ii = x; ii < height+x; ++ii) {
        for (int j = y; j < width+y; ++j) {
          int xx = j * rectWidth;
          int yy = ii * rectWidth;

          if (r.layout[ii-x][j-y] == 'x') {
            g.setColor(Color.DARK_GRAY);
          } else if (r.layout[ii-x][j-y] == '.') {
            g.setColor(Color.GRAY);
          } else if (r.layout[ii-x][j-y] == '|' || r.layout[ii-x][j-y] == '-') {
            g.setColor(Color.CYAN);
          } else {
            g.setColor(Color.GRAY);
          }
          g.fillRect(xx, yy, rectWidth, rectWidth);
          g.setColor(Color.black);
          g.drawRect(xx, yy, rectWidth, rectWidth);
          if (r.layout[ii-x][j-y] == 'P') {
            try {
              URL url = new URL(PlayerUrl);
              PlayerImage = ImageIO.read(url);
            }
            catch(IOException e) {
              System.out.println("Image not found");
            }

            g.drawImage(PlayerImage, xx, yy,
                    rectWidth -1, rectWidth -1, null);
          }
          else if (r.layout[ii-x][j-y] == 'Z') {
            try {
              URL url = new URL(ADUrl);
              ADImage = ImageIO.read(url);
            }
            catch(IOException e) {
              System.out.println("Image not found");
            }
            g.drawImage(ADImage,  xx,yy,
                    rectWidth -1, rectWidth - 1, null);
          }
          else if (r.layout[ii-x][j-y] == 'G') {
            try {
              URL url = new URL(Ghost);
              ADImage = ImageIO.read(url);
            }
            catch(IOException e) {
              System.out.println("Image not found");
            }
            g.drawImage(ADImage,  xx,yy,
                    rectWidth -1, rectWidth - 1, null);
          }
          else if (r.layout[ii-x][j-y] == 'E') {
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
  public int[] getRoomPosition(int[] point) {
    for (Room room: rooms) {
      int rows = room.layout.length;
      int cols = room.layout[0].length;
      int positionX = room.position[0];
      int positionY = room.position[1];

      if (positionY <= point[1] && point[1] < positionY + rows) {
        if (positionX <= point[0] && point[0] < positionX + cols) {
          return room.position;
        }
      }
    }
    int[] none = new int[2]; none[0] = -1; none[1] = -1;
    return none;
  }

  //for test
  public JSONObject checkForPoint(int[] point) {
    JSONObject output = new JSONObject();
    int type = checkTailType(point);
    if (type == -1) {
      if (checkIfInHallways(point) != -1) {
        int[] from = hallways.get(checkIfInHallways(point)).connection.get(0);
        int[] to = hallways.get(checkIfInHallways(point)).connection.get(1);
        JSONArray ja = new JSONArray();
        int[] reachable = new int[2];
        int[] reachable2 = new int[2];
        reachable[0] = from[0];
        reachable[1] = from[1];
        int[] position1 = getRoomPosition(reachable);
        reachable[0] = position1[0];
        reachable[1] = position1[1];
        ja.put(reachable);

        reachable2[0] = to[0];
        reachable2[1] = to[1];
        int[] position2 = getRoomPosition(reachable2);
        reachable2[0] = position2[0];
        reachable2[1] = position2[1];
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
      int[] roomPosition = getRoomPosition(point);
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
          p[0] = ii + room.position[0]; p[1] = jj + room.position[1];
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
      if (position[0] != hw.connection.get(0)[0] ||
              position[1] != hw.connection.get(0)[1]) {
        d[0] = hw.connection.get(0)[0]; d[1] = hw.connection.get(0)[1];
      }
      else {
        d[0] = hw.connection.get(1)[0]; d[1] = hw.connection.get(1)[1];
      }
      rPosition[0] = getRoomPosition(d)[0]; rPosition[1] = getRoomPosition(d)[1];
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
      if (ad.getPosition()[0] == point[0] && ad.getPosition()[1] == point[1]) {
        return true;
      }
    }
    return false;
  }

  public boolean checkIfOnPlayer(List<Player> players, int[] point) {
    for (Player p : players) {
      if (p.getPosition()[0] == point[0] && p.getPosition()[1] == point[1]) {
        return true;
      }
    }
    return false;
  }

  // find which room the given position in
  public Room inWhichRoom(int[] position) {
    for (Room room: this.rooms) {
      int rows = room.layout.length;
      int cols = room.layout[0].length;
      int positionX = room.position[0];
      int positionY = room.position[1];

      if (positionY <= position[1] && position[1] < positionY + rows) {
        if (positionX <= position[0] && position[0] < positionX + cols) {
          return room;
        }
      }
    }
    return null;
  }

  public int checkIfInHallways(int[] point) {
    for (int ii = 0; ii < hallways.size(); ++ii) {
      for (int[] p: hallways.get(ii).layout) {
        if (p[0] == point[0] && p[1] == point[1]) {
          //which hallway
          return ii;
        }
      }
      if (hallways.get(ii).connection.get(0)[0] == point[0]
              && hallways.get(ii).connection.get(0)[1] == point[1]) {
        return ii;
      } else if (hallways.get(ii).connection.get(1)[0] == point[0]
              && hallways.get(ii).connection.get(1)[0] == point[1]) {
        return ii;
      }
    }
    return -1;
  }

  public int checkTailType(int[] point) {
    for (Room room: rooms) {
      int rows = room.layout.length;
      int cols = room.layout[0].length;
      int positionX = room.position[0];
      int positionY = room.position[1];


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


  public int[] setPlayer() {
    Random rand = new Random();
    int intRandomRoom = rand.nextInt(this.rooms.size());
    Room r = this.rooms.get(intRandomRoom);

    int rows = r.layout.length;
    int cols = r.layout[0].length;

    int ranRandomRow = rand.nextInt(rows);
    int ranRandomCol = rand.nextInt(cols);

    while (r.layout[ranRandomRow][ranRandomCol] != '.') {
      ranRandomRow = rand.nextInt(rows);
      ranRandomCol = rand.nextInt(cols);
    }
    r.layout[ranRandomRow][ranRandomCol] = 'P';

    return new int[]{ranRandomRow + r.position[0], ranRandomCol + r.position[1]};
  }

  public void setZombiesInLevel(int amount) {
    for (int ii = 0; ii < amount; ++ii) {
      Random rand = new Random();
      int intRandomRoom = rand.nextInt(this.rooms.size());
      Room r = this.rooms.get(intRandomRoom);

      int rows = r.layout.length;
      int cols = r.layout[0].length;

      int ranRandomRow = rand.nextInt(rows);
      int ranRandomCol = rand.nextInt(cols);

      while (r.layout[ranRandomRow][ranRandomCol] != '.') {

        ranRandomRow = rand.nextInt(rows);
        ranRandomCol = rand.nextInt(cols);
      }
      r.layout[ranRandomRow][ranRandomCol] = 'Z';

      Zombie z = new Zombie(new int[]{ranRandomRow+r.position[0], ranRandomCol+r.position[1]} );
      this.ads.add(z);
    }
  }

  public void setGhostInLevel(int amount) {
    for (int ii = 0; ii < amount; ++ii) {
      Random rand = new Random();
      int intRandomRoom = rand.nextInt(this.rooms.size());
      Room r = this.rooms.get(intRandomRoom);

      int rows = r.layout.length;
      int cols = r.layout[0].length;

      int ranRandomRow = rand.nextInt(rows);
      int ranRandomCol = rand.nextInt(cols);

      while (r.layout[ranRandomRow][ranRandomCol] != '.') {

        ranRandomRow = rand.nextInt(rows);
        ranRandomCol = rand.nextInt(cols);
      }
      r.layout[ranRandomRow][ranRandomCol] = 'G';

      Ghost g = new Ghost(new int[]{ranRandomRow+r.position[0], ranRandomCol+r.position[1]} );
      this.ads.add(g);
    }
  }


  public void movePlayer(Player player, int[] newPosition) {
    int[] oldPosition = player.getPosition();
    int[] old = new int[2];
    old[0] = oldPosition[0]; old[1] = oldPosition[1];
    Room oldRoom = inWhichRoom(old);

    int[] dst = new int[2];
    dst[0] = newPosition[0]; dst[1] = newPosition[1];
    Room dstRoom = inWhichRoom(dst);

    //handle old position
    for (Room r: this.rooms) {
      if (oldRoom != null) {
        if (r.position.equals(oldRoom.position)) {
          r.layout[old[0] - r.position[0]][old[1] - r.position[1]] = '.';
        }
      }
      else {
        for (Hallway hw: hallways) {
          for (int[] point: hw.layout) {
            if (oldPosition.equals(point)) {
              hw.ifPlayerInside = false;
              hw.playerPosition = new int[2];;
            }
          }
        }
      }
    }

    //handle dst position
    for (Room r: this.rooms) {
      if (dstRoom != null) {
        if (r.position.equals(dstRoom.position)) {
//          System.out.println("to new room: " + r.position);
          r.layout[dst[0] - r.position[0]][dst[1] - r.position[1]] = 'P';
        }
      }
      else {
        for (Hallway hw: hallways) {
          for (int[] point: hw.layout) {
            if (newPosition.equals(point)) {
              hw.ifPlayerInside = true;
              int [] playerPos = new int[2];
              playerPos[0] = point[0]; playerPos[1] = point[1];
              hw.playerPosition = playerPos;
            }
          }
        }
      }
    }


    player.position = newPosition;

//    System.out.println(player.name + "move from:" + oldPosition + " to: " + newPosition);
  }


}
