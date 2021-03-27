import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Observer implements ObserverInterface {
  GameState gs;

  public Observer(GameState gameState) {
    this.gs = gameState;
  }

  @Override
  public void printInfo() {
    for (Player p: gs.players) {
      System.out.println(p.name + ", Position: " + p.position);
    }

    for (Adversary ad: gs.levels.get(gs.levelStatus).ads) {
      System.out.println(ad.name + ", Position: " + ad.position);
    }

    System.out.println(gs.levels.get(gs.levelStatus).ifLocked);
    System.out.println("GameStatus: " + gs.gameStatus);

  }

  @Override
  public void render(GameState gameState) {
    Observer observer = new Observer(gameState);


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

        observer.gs.levels.get(observer.gs.levelStatus).drawRooms(g);
        observer.gs.levels.get(observer.gs.levelStatus).drawHallways(g);
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
