//import java.util.ArrayList;
//import java.util.List;
//import javax.swing.*;
//import java.awt.*;
//
//import javafx.util.Pair;
//
//public class Level extends JPanel{
//  List<Room> rooms;
//  List<Hallway> hallways;
//  List<Adversary> ads;
//
//  public static final char[][] room1Tails = {
//          {'x','x','x','x','x'},
//          {'x','.','.','.','x',},
//          {'x','.','.','.','|'},
//          {'x','.','.','.','x',},
//          {'x','x','-','x','x'}};
//  public static final Pair<Integer, Integer> room1Position = new Pair<>(0,0);
//  public static final Room room1 = new Room(room1Tails, room1Position);
//
//
//  public static final char[][] room2Tails = {
//          {'x','x','x','x','x'},
//          {'x','.','.','.','x',},
//          {'|','.','.','.','x'},
//          {'x','.','.','.','x',},
//          {'x','x','x','x','x'}};
//  public static final Pair<Integer, Integer> room2Position = new Pair<>(8,0);
//  public static final Room room2 = new Room(room2Tails, room2Position);
//
//
//  public static final char[][] room3Tails = {
//          {'x','x','-','x','x'},
//          {'x','.','.','.','x',},
//          {'x','.','.','.','x'},
//          {'x','.','.','.','x',},
//          {'x','.','.','.','x'},
//          {'x','x','x','x','x'}};
//  public static final Pair<Integer, Integer> room3Position = new Pair<>(0,8);
//  public static final Room room3 = new Room(room3Tails, room3Position);
//
//  public static final char[][] hw1Tiles = {
//          {'x','x','x'},
//          {'.','.','.'},
//          {'x','x','x'}};
//  public static final Pair<Integer, Integer> room1door1 = new Pair<>(4, 2);
//  public static final Pair<Integer, Integer> room2door1 = new Pair<>(8, 2);
//  public static final Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> hw1Connect =
//          new Pair<>(room1door1, room2door1);
//  public static final Pair<Integer, Integer> hw1Position = new Pair<>(5,1);
//  public static final Hallway hw1 = new Hallway(hw1Tiles, hw1Position, hw1Connect);
//
//  public static final char[][] hw2Tiles = {
//          {'x','.','x'},
//          {'x','.','x'},
//          {'x','.','x'}};
//  public static final Pair<Integer, Integer> room1door2 = new Pair<>(2, 4);
//  public static final Pair<Integer, Integer> room3door1 = new Pair<>(2, 8);
//  public static final Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> hw2Connect =
//          new Pair<>(room1door2, room3door1);
//  public static final Pair<Integer, Integer> hw2Position = new Pair<>(1,5);
//  public static final Hallway hw2 = new Hallway(hw2Tiles, hw2Position, hw2Connect);
//
//  public static final Pair<Integer, Integer> aPosition = new Pair<>(3,12);
//  public static final Adversary ad1 = new Adversary(aPosition);
//
//
//  public Level() {
//    this.rooms = new ArrayList<>();
//    this.hallways = new ArrayList<>();
//    this.rooms.add(room1);
//    this.rooms.add(room2);
//    this.rooms.add(room3);
//    this.hallways.add(hw1);
//    this.hallways.add(hw2);
//    this.ads = new ArrayList<>();
//    this.ads.add(ad1);
//    this.init();
//  }
//
//  public void init() {
////    this.setAds();
//    this.setExit();
//  }
//
//  public void setAds(int amount) {
////    for (int ii = 0; ii < this.ads.size(); ++ii) {
////      int x = this.ads.get(ii).getPosition().getKey();
////      int y = this.ads.get(ii).getPosition().getValue();
////      Room r = this.rooms.get(2);
////      r.layout[y - r.position.getValue()][x - r.position.getKey()] = 'A';
////    }
//    Room r = this.rooms.get(2);
//    int length = r.layout[0].length;
//    for (int ii = r.layout.length; ii > 0; --ii) {
//      for (int jj = length; jj > 0; --jj) {
//        if (r.layout[ii-1][jj-1] == '.') {
//          if (amount > 0) {
//            r.layout[ii-1][jj-1] = 'A';
//            amount--;
//          } else {
//            break;
//          }
//        }
//      }
//    }
//  }
//
//  public void setExit() {
//    this.rooms.get(1).layout[3][3] = 'E';
//  }
//
//  public Level(List<Room> rooms, List<Hallway> hallways) {
//    this.rooms = rooms;
//    this.hallways = hallways;
//  }
//
//  public void addRoom(Room room) {
//    this.rooms.add(room);
//  }
//
//  public void addHallways(Hallway hw) {
//    this.hallways.add(hw);
//  }
//
//  @Override
//  public void paintComponent(Graphics g) {
//    super.paintComponent(g);
//    g.clearRect(0, 0, getWidth(), getHeight());
//    int rectWidth = 50;
//
//    for (Room r: this.rooms) {
//      int x = r.position.getKey();
//      int y = r.position.getValue();
//      int width = r.layout[0].length;
//      int height = r.layout.length;
//      for (int ii = y; ii < height+y; ++ii) {
//        for (int j = x; j < width+x; ++j) {
//          int xx = ii * rectWidth;
//          int yy = j * rectWidth;
//
//          if (r.layout[ii-y][j-x] == 'x'){
//            g.setColor(Color.DARK_GRAY);
//          } else if (r.layout[ii-y][j-x] == '.') {
//            g.setColor(Color.GRAY);
//          } else if (r.layout[ii-y][j-x] == '|' || r.layout[ii-y][j-x] == '-') {
//            g.setColor(Color.CYAN);
//          } else {
//            g.setColor(Color.GRAY);
//          }
//          g.fillRect(yy, xx, rectWidth, rectWidth);
//          g.setColor(Color.black);
//          g.drawRect(yy, xx, rectWidth, rectWidth);
//          if (r.layout[ii-y][j-x] == 'P') {
//            System.out.println("ii:" + ii + " jj:" + j);
//            g.setColor(Color.MAGENTA);
//            Font tr = new Font("TimesRoman", Font.PLAIN, 25);
//            g.setFont(tr);
//            g.drawString("P", 50* j + 20, 50 *ii + 30);
//          }
//          else if (r.layout[ii-y][j-x] == 'A') {
//            g.setColor(Color.BLACK);
//            Font tr = new Font("TimesRoman", Font.PLAIN, 25);
//            g.setFont(tr);
//            g.drawString("A", 50* j + 20, 50 *ii + 30);
//          }
//          else if (r.layout[ii-y][j-x] == 'E') {
//            g.setColor(Color.RED);
//            Font tr = new Font("TimesRoman", Font.PLAIN, 25);
//            g.setFont(tr);
//            g.drawString("E", 50* j + 20, 50 *ii + 30);
//          }
//        }
//      }
//    }
//
//    for (Hallway hw: this.hallways) {
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
//
//        }
//      }
//    }
//  }
//
//
//
//  public Pair<Integer, Integer> setPlayer() {
////    for (int ii = 0; ii < players.size(); ++ii) {
////      int x = players.get(ii).getPosition().getKey();
////      int y = players.get(ii).getPosition().getValue();
////      this.rooms.get(0).layout[y][x] = 'P';
////    }
//    Room r = this.rooms.get(0);
//    for (int ii = 0; ii < r.layout.length; ++ii) {
//      for (int jj = 0; jj < r.layout[0].length; ++jj) {
//        if (r.layout[ii][jj] == '.') {
//          r.layout[ii][jj] = 'P';
//          return new Pair<>(ii,jj);
//        }
//      }
//    }
//    return new Pair<>(0,0);
//  }
//
//  public void movePlayer(Player player, Pair<Integer, Integer> newPosition) {
//    Pair<Integer, Integer> oldPosition = player.getPosition();
//    Pair<Integer, Integer> dst;
//    for (Room r: this.rooms) {
//      if (oldPosition.getKey() > r.position.getKey() &&
//              oldPosition.getValue() > r.position.getValue()) {
//        dst = new Pair<>(newPosition.getKey() - r.position.getKey(),
//                newPosition.getValue() - r.position.getValue());
//        if (r.layout[dst.getValue() - r.position.getValue()]
//                [dst.getKey() - r.position.getKey()] == '.') {
//          r.layout[oldPosition.getValue() - r.position.getValue()]
//                  [oldPosition.getKey() - r.position.getKey()] = '.';
//          r.layout[dst.getValue()][dst.getKey()] = 'P';
//          player.position = newPosition;
//        }
//      }
//    }
//  }
//
//
//  public void renderLevel(Level level) {
//    SwingUtilities.invokeLater(new Runnable() {
//      @Override
//      public void run() {
//        JFrame frame = new JFrame("Game");
//        frame.add(level);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////        frame.pack();
//        frame.setVisible(true);
//        frame.setSize(1000,1000);
//      }
//    });
//  }
//}
