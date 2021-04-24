import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.Timer;

public class Observer extends JPanel implements ObserverInterface {
  GameManager gm;

  public Observer(List<String> names) {
    this.gm = new GameManager(names);
    gm.init();
    setBackground(Color.BLACK);
    setForeground(Color.WHITE);
    refreshScreen();
  }

  public Observer(int natural, List<Level> levels, List<String> names) {
    this.gm = new GameManager(levels, names);
    gm.init();
    setBackground(Color.BLACK);
    setForeground(Color.WHITE);
    refreshScreen();
  }



  @Override
  public void printInfo() {
    for (Player p: gm.players) {
      System.out.println(p.name + ", Position: " + p.position);
    }

    for (Adversary ad: gm.gameState.levels.get(gm.gameState.curLevel).ads) {
      System.out.println("Ad: " + ", Position: " + ad.position);
    }

    System.out.println(gm.gameState.levels.get(gm.gameState.curLevel).isLocked);
    System.out.println("GameStatus: " + gm.gameState.gameStatus);

  }


  Timer timer;
  @Override
  // render the Level
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.clearRect(0, 0, getWidth(), getHeight());

    List<Room> rooms = gm.gameState.levels.get(gm.gameState.curLevel).getRooms();
    Level.drawRooms(rooms, g);
    List<Hallway> hws = gm.gameState.levels.get(gm.gameState.curLevel).getHallways();
    Level.drawHallways(hws, g);
  }
  public void refreshScreen() {
    timer = new Timer(0, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        repaint();
      }
    });
    timer.setRepeats(true);
    // Aprox. 60 FPS
    timer.setDelay(17);
    timer.start();
  }
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(650, 480);
  }

  public void render() {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(this);
    frame.pack();
    frame.setVisible(true);
  }


  public static void main(String[] args) {
    Observer ob = new Observer(Arrays.asList("s"));
    ob.render();
    ob.printInfo();
  }
}
