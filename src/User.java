import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.Timer;

// represents a User class
public class User extends JPanel implements KeyListener, UserInterface {
    private static User user;
    GameManager gm;
    public int moveAmount = 0;

    // init gameManager, and set settings of GameManager
    public User(List<String> names) {
        this.gm = new GameManager(names);
        gm.init();
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        refreshScreen();
    }

    public User(int natural, List<Level> levels, List<String> names) {
        this.gm = new GameManager(levels, names);
        gm.init();
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        refreshScreen();
    }

    @Override
    public void update(int[] position, GameState state) {
        gm.movePlayer(state.players.get(gm.curPlayer), position);

    }

    Timer timer;
    @Override
    // render the Level
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight());

        gm.drawPlayerView(g);
//        user.gm.curLevel.drawRooms(g);
//        user.gm.curLevel.drawHallways(g);
    }
    public void refreshScreen() {
        timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        timer.setRepeats(true);
//        timer.setDelay(17);
        timer.start();
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(650, 480);
    }

    /**
     * Invoked when a key has been typed. See the class description for {@link KeyEvent} for a
     * definition of a key typed event.
     */
    @Override
    public void keyTyped(KeyEvent e) {}

    // deal with arrow movement of user
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        Player p = gm.players.get(gm.curPlayer);
        int[] dst = new int[2];
        switch( keyCode ) {
            case KeyEvent.VK_UP:
                // handle up
                dst[0] = p.position[0] -1;
                dst[1] = p.position[1];
                ++moveAmount;
                break;
            case KeyEvent.VK_DOWN:
                // handle down
                dst[0] = p.position[0] + 1;
                dst[1] = p.position[1];
                ++moveAmount;
                break;
            case KeyEvent.VK_LEFT:
                // handle left
                dst[0] = p.position[0];
                dst[1] = p.position[1] -1;
                ++moveAmount;
                break;
            case KeyEvent.VK_RIGHT :
                // handle right
                dst[0] = p.position[0];
                dst[1] = p.position[1] + 1;
                ++moveAmount;
                break;
        }
        if (keyCode == KeyEvent.VK_ENTER) {
            gm.nextPlayer();
            moveAmount = 0;
        }
        if (moveAmount <= 2) {
            move(dst, gm.players.get(gm.curPlayer));
        }
    }


    /**
     * Invoked when a key has been released. See the class description for {@link KeyEvent} for a
     * definition of a key released event.
     */
    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void move(int[] dst, Player player) {
        gm.movePlayer(player, dst);
    }

    public void render() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(this);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        user = new User(Arrays.asList("JC", "hollis"));
        user.render();
    }
}

