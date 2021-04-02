import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.Timer;

public class Observer implements ObserverInterface {
  GameManager gm;

  public Observer(List<String> names) {
    this.gm = new GameManager(names);
    gm.init();
  }


  public static void main(String[] args) {
    Observer ob = new Observer(Arrays.asList("s"));
    ob.render(ob);
    ob.printInfo();
  }

  @Override
  public void printInfo() {
    for (Player p: gm.players) {
      System.out.println(p.name + ", Position: " + p.position);
    }

    for (Adversary ad: gm.curLevel.ads) {
      System.out.println("Ad: " + ", Position: " + ad.position);
    }

    System.out.println(gm.curLevel.ifLocked);
    System.out.println("GameStatus: " + gm.gameState.gameStatus);

  }

  @Override
  public void render(Observer ob) {


    class Panel extends JPanel{
      Timer timer;
      Panel() {
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        refreshScreen();
      }
      @Override
      // render the Level
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight());

        ob.gm.gameState.levels.get(ob.gm.gameState.curLevel).drawRooms(g);
        ob.gm.gameState.levels.get(ob.gm.gameState.curLevel).drawHallways(g);
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

    }

    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Panel p = new Panel();
    frame.add(p);
    frame.pack();
    frame.setVisible(true);
  }
}
