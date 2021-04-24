import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;

public class RemoteUser extends JPanel implements KeyListener {
    public static JSONObject playerUpdateMessage;
    public static int moveAmount = 0;
    public int[] playerDst;
    public int[] position;

    public static BufferedImage PlayerImage;
    public static BufferedImage ADImage;
    private static String ADUrl =
//          "https://avatars.githubusercontent.com/u/60799921?s=400&u=4d68d8d6c5acd9a4b48ef35dc7d3a0b9a8164d04&v=4";
            "https://images-na.ssl-images-amazon.com/images/I/71vj4KrX%2BvL._AC_SL1500_.jpg";
    private static String PlayerUrl =
//          "https://avatars.githubusercontent.com/u/46980128?s=400&u=abab5bff473ece8159ceb6f29ebf7cf3fc132e2b&v=4";
//          "https://avatars.githubusercontent.com/u/60799921?s=400&u=4d68d8d6c5acd9a4b48ef35dc7d3a0b9a8164d04&v=4";
//  "https://media-exp1.licdn.com/dms/image/C4E03AQFk3SizfWyASg/profile-displayphoto-shrink_800_800/0/1581017250813?e=1622678400&v=beta&t=Lw93auRr4x3oh9HvykxpqGsGVTjnrf547ApLp9NB3TA";
            "https://avatars.githubusercontent.com/u/46980128?s=400&u=abab5bff473ece8159ceb6f29ebf7cf3fc132e2b&v=4";

    private static String Ghost =
            "https://image.shutterstock.com/image-photo/zombie-ghost-isolated-on-black-260nw-646738963.jpg";


    public void messageUpdate(JSONObject newMessage) {
        playerUpdateMessage = newMessage;
    }

    public RemoteUser() {
        playerUpdateMessage = null;
    }

    public RemoteUser(JSONObject msg) {
        playerUpdateMessage = msg;
        playerDst = null;
        JSONArray pos = playerUpdateMessage.getJSONArray("position");
        position = new int[]{pos.getInt(0), pos.getInt(1)};
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        refreshScreen();
    }
    public void setPlayerUpdateMessage(JSONObject playerUpdateMessage) {

        RemoteUser.playerUpdateMessage = playerUpdateMessage;
//        playerDst = null;
//        JSONArray pos = playerUpdateMessage.getJSONArray("position");
//        position = new int[]{pos.getInt(0), pos.getInt(1)};
//        setBackground(Color.BLACK);
//        setForeground(Color.WHITE);
//        refreshScreen();
    }


    Timer timer;
    @Override
    // render the Level
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight());
        drawPlayerView(g);
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
//    Player p = gm.players.get(gm.curPlayer);
        switch( keyCode ) {
            case KeyEvent.VK_UP:
                // handle up
                position[0]--;
                break;
            case KeyEvent.VK_DOWN:
                // handle down
                position[0] ++;
                break;
            case KeyEvent.VK_LEFT:
                // handle left
                position[1] --;
                break;
            case KeyEvent.VK_RIGHT :
                // handle right
                position[1] ++;
                break;
        }
//    if (keyCode == KeyEvent.VK_ENTER) {
//      gm.nextPlayer();
//      moveAmount = 0;
//    }
        playerDst = position;
    }

    public int getMoveAmount() {
        return moveAmount;
    }

    public void addMoveAmount() {moveAmount++;}

    public void setMoveAmount() {
        moveAmount = 0;
    }

    public int[] getMove() {
        return playerDst;
    }

    public void setMoveToNull() {
        playerDst = null;
    }

    /**
     * Invoked when a key has been released. See the class description for {@link KeyEvent} for a
     * definition of a key released event.
     */
    @Override
    public void keyReleased(KeyEvent e) {}

    public void render() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(this);
        frame.pack();
        frame.setVisible(true);
    }

    public void drawPlayerView(Graphics g) {
        int rectWidth = 25;

        JSONArray position = playerUpdateMessage.getJSONArray("position");
        int[] pos = new int[2];
        pos[0] = position.getInt(0); pos[1] = position.getInt(1);

        int[][] view = new int[5][5];
        JSONArray layouts = playerUpdateMessage.getJSONArray("layout");
        for (int ii = 0; ii < 5; ++ii) {
            for (int yy = 0; yy < 5; ++yy) {
                view[ii][yy] = layouts.getJSONArray(ii).getInt(yy);
            }
        }

        int row = 0;
        for (int ii = 2; ii > -3; --ii) {
            int col = 0;
            for (int zz = 2; zz > -3; --zz) {
                int xx = (2 + (pos[1] - zz)) * rectWidth;
                int yy = (2 + (pos[0] - ii)) * rectWidth;

                if (view[row][col] == 0) {
                    g.setColor(Color.DARK_GRAY);
                } else if (view[row][col] == 1) {
                    g.setColor(Color.GRAY);
                }  else if (view[row][col] == 2) {
                    g.setColor(Color.CYAN);
                }
                g.fillRect(xx, yy, rectWidth, rectWidth);
                g.setColor(Color.black);
                g.drawRect(xx, yy, rectWidth, rectWidth);

                if (view[row][col] == 5) {
                    g.setColor(Color.GRAY);
                    g.drawRect(xx, yy, rectWidth, rectWidth);
                    g.setColor(Color.RED);
                    Font tr = new Font("TimesRoman", Font.PLAIN, 12);
                    g.setFont(tr);
                    g.drawString("E", xx + 10, yy + 15);
                }
                else if (view[row][col] == 4) {
                    g.setColor(Color.GRAY);
                    g.drawRect(xx, yy, rectWidth, rectWidth);
                    g.setColor(Color.blue);
                    Font tr = new Font("TimesRoman", Font.PLAIN, 12);
                    g.setFont(tr);
                    g.drawString("K", xx + 10, yy + 15);
                }
                else if (view[row][col] == -1) {
                    try {
                        URL url = new URL(ADUrl);
                        ADImage = ImageIO.read(url);
                    }
                    catch(IOException e) {
                        System.out.println("Image not found");
                    }
                    g.drawImage(ADImage, xx, yy,
                            rectWidth -1, rectWidth -1, null);
                }
                else if (view[row][col] == -2) {
                    try {
                        URL url = new URL(Ghost);
                        ADImage = ImageIO.read(url);
                    }
                    catch(IOException e) {
                        System.out.println("Image not found");
                    }
                    g.drawImage(ADImage, xx, yy,
                            rectWidth -1, rectWidth -1, null);
                }

                if ((ii == 0 && zz == 0) || view[row][col] == 3) {
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
                ++col;
            }
            ++row;
        }
    }


}
