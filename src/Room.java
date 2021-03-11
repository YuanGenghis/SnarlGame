import java.util.ArrayList;

import javafx.util.Pair;

public class Room {
  char[][] layout;
  Pair<Integer, Integer> position;

  public Room(char[][] tiles, Pair<Integer, Integer> position) {
    this.layout = tiles;
    this.position = position;
  }

  public Room(int row, int col) {
    this.layout = new char[col][row];
    this.position = new Pair<>(0,0);
  }

  public void addTile(int x, int y, char tile) {
    for (int ii = 0; ii < layout.length; ++ii) {
      for (int jj = 0; jj < layout[0].length; ++jj) {
        if (ii == x && jj ==y) {
          layout[ii][jj] = tile;
        }
      }
    }
  }

  public void setPosition(int x, int y) {
    this.position = new Pair<>(x,y);
  }



}
