import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.Timer;

public class User implements UserInterface {
    GameManager gm;


    public User(List<String> names) {
        this.gm = new GameManager(names);
        gm.init();
    }

    @Override
    public void update(int[] position, GameState state) {
        gm.movePlayer(state.players.get(gm.curPlayer), position);

    }

    @Override
    public void move(int[] dst, Player player) {
        gm.movePlayer(player, dst);
    }

    public static void main(String[] args) {
        User user = new User(Arrays.asList("JC", "hollis"));
        System.out.println(user.gm.players.get(0).position);
        System.out.println(user.gm.players.get(1).position);

//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                JFrame frame = new JFrame("Game");
//                frame.add(user);
//                frame.addKeyListener(user);
//                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                frame.setVisible(true);
//                frame.setSize(2000,2000);
//            }
//        });


        class Panel extends JPanel implements KeyListener {
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

                user.gm.curLevel.drawRooms(g);
                user.gm.curLevel.drawHallways(g);
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


            /**
             * Invoked when a key has been typed. See the class description for {@link KeyEvent} for a
             * definition of a key typed event.
             */
            @Override
            public void keyTyped(KeyEvent e) {

            }

            // deal with arrow movement of user
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                Player p = user.gm.players.get(user.gm.curPlayer);
                int[] dst = new int[2];
                switch( keyCode ) {
                    case KeyEvent.VK_UP:
                        // handle up
                        System.out.println("up");
                        dst[0] = p.position.getKey() -1;
                        dst[1] = p.position.getValue();
                        break;
                    case KeyEvent.VK_DOWN:
                        // handle down
                        System.out.println("down");
                        dst[0] = p.position.getKey() + 1;
                        dst[1] = p.position.getValue();
                        break;
                    case KeyEvent.VK_LEFT:
                        // handle left
                        dst[0] = p.position.getKey();
                        dst[1] = p.position.getValue() -1;
                        break;
                    case KeyEvent.VK_RIGHT :
                        // handle right
                        dst[0] = p.position.getKey();
                        dst[1] = p.position.getValue() + 1;
                        break;
                }
                user.move(dst, user.gm.players.get(user.gm.curPlayer));
            }


            /**
             * Invoked when a key has been released. See the class description for {@link KeyEvent} for a
             * definition of a key released event.
             */
            @Override
            public void keyReleased(KeyEvent e) {

            }
        }

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Panel p = new Panel();
        frame.add(p);
        frame.addKeyListener(p);
        frame.pack();
        frame.setVisible(true);

    }

}

