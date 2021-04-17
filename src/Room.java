import java.io.Serializable;
import java.util.ArrayList;

import javafx.util.Pair;

// represents a Room
public class Room implements Serializable {
  char[][] layout;
  int[] position;

  // construct the room
  public Room(char[][] tiles, int[] position) {
    this.layout = tiles;
    this.position = position;
  }

  // construct the empty Room
  public Room(int row, int col) {
    this.layout = new char[col][row];
    this.position = new int[2];
  }

  // add tiles to empty Room
  public void addTile(int x, int y, char tile) {
    for (int ii = 0; ii < layout.length; ++ii) {
      for (int jj = 0; jj < layout[0].length; ++jj) {
        if (ii == x && jj ==y) {
          layout[ii][jj] = tile;
        }
      }
    }
  }

  // set the position of the origin of Room
  public void setPosition(int x, int y) {
    this.position = new int[2];
    this.position[0] = x;
    this.position[1] = y;
  }
}
