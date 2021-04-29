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
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import jdk.nashorn.internal.ir.LiteralNode;

public class AdversaryUser extends JPanel implements KeyListener {
    public JSONObject adversaryUpdateMessage;
    public static int moveAmount = 0;
    public int[] playerDst;

    public static BufferedImage PlayerImage;
    public static BufferedImage ADImage;
    private static final String ADUrl =
            "https://images-na.ssl-images-amazon.com/images/I/71vj4KrX%2BvL._AC_SL1500_.jpg";
    private static final String PlayerUrl =
            "https://avatars.githubusercontent.com/u/46980128?s=400&u=abab5bff473ece8159ceb6f29ebf7cf3fc132e2b&v=4";

    private static String Ghost =
            "https://image.shutterstock.com/image-photo/zombie-ghost-isolated-on-black-260nw-646738963.jpg";


    public void messageUpdate(JSONObject newMessage) {
        adversaryUpdateMessage = newMessage;
    }

    public AdversaryUser() {
        adversaryUpdateMessage = null;
    }

    public AdversaryUser(JSONObject msg) {
        adversaryUpdateMessage = msg;
        playerDst = null;
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        refreshScreen();
    }

    public void setAdversaryUpdateMessage(JSONObject adversaryUpdateMessage) {

        this.adversaryUpdateMessage = adversaryUpdateMessage;
    }


    Timer timer;
    @Override
    // render the Level
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight());
//        drawPlayerView(g);
        drawWholeView(g);
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
    }
    public int getMoveAmount() {
        return moveAmount;
    }


    public void setMoveAmount() {
        moveAmount = 0;
    }

    public int[] getMove() {
        return playerDst;
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

    public int[] getAdMove() {
        int[] adPos = new int[]{
                adversaryUpdateMessage.getJSONArray("position").getInt(0)
                , adversaryUpdateMessage.getJSONArray("position").getInt(1)};
        List<Room> rooms = getRoomsFromJSON();
        List<Hallway> hallways = getHWsFromJSON();
        Level level = new Level(rooms, hallways);
        String type = (String)adversaryUpdateMessage.get("adType");
        Adversary ad;
        if (type.equals("Zombie")) {
            ad = new Zombie(adPos);
        } else {
            ad = new Ghost(adPos);
        }

        JSONArray actors = adversaryUpdateMessage.getJSONArray("players");
        List<Player> players = new ArrayList<>();
        for (Object obj: actors) {
            int[] pos = new int[]{
                    ((JSONArray) ((JSONObject)obj).get("position")).getInt(0)
                    ,((JSONArray) ((JSONObject)obj).get("position")).getInt(1)};
            Player p = new Player(pos);
            players.add(p);
        }

        return RuleChecker.getAdNextMove(ad,level, players);
    }


    public void drawWholeView(Graphics g) {
        List<Room> rooms = getRoomsFromJSON();
        List<Hallway> hws = getHWsFromJSON();

        Level.drawRooms(rooms, g);
        Level.drawHallways(hws, g);

    }


    public List<Room> getRoomsFromJSON() {
        List<Room> rooms = new ArrayList<>();

        JSONArray roomObjects = this.adversaryUpdateMessage.getJSONArray("rooms");
        for (int ii = 0; ii < roomObjects.length(); ++ii) {
            JSONObject roomObj = roomObjects.getJSONObject(ii);
            int x = roomObj.getJSONArray("origin").getInt(0);
            int y = roomObj.getJSONArray("origin").getInt(1);
            int[] position = new int[]{x,y};
            JSONArray layout = roomObj.getJSONArray("layout");
            int rows = layout.length();
            int cols = layout.getJSONArray(0).length();
            char[][] tiles = new char[rows][cols];
            for (int rr = 0; rr < rows; ++rr) {
                for (int cc = 0; cc < cols; ++cc) {
                    tiles[rr][cc] = ((String)(layout.getJSONArray(rr)).get(cc)).charAt(0);
                }
            }
            Room room = new Room(tiles, position);
            rooms.add(room);
        }


        JSONArray objects = this.adversaryUpdateMessage.getJSONArray("objects");
        for (Object jo: objects) {
            int[] keyPos = new int[]{
                    ((JSONObject)jo).getJSONArray("position").getInt(0),
                    ((JSONObject)jo).getJSONArray("position").getInt(1)};
            for (Room room: rooms) {
                int rows = room.layout.length;
                int cols = room.layout[0].length;
                int positionX = room.position[0];
                int positionY = room.position[1];

                if (positionY <= keyPos[1] && keyPos[1] < positionY + rows) {
                    if (positionX <= keyPos[0] && keyPos[0] < positionX + cols) {
                        if (((JSONObject)jo).get("type").equals("key")) {
                            room.layout[keyPos[0]-positionX][keyPos[1]-positionY] = 'K';
                        } else {
                            room.layout[keyPos[0]-positionX][keyPos[1]-positionY] = 'E';
                        }
                    }
                }
            }
        }

        return rooms;
    }


    public List<Hallway> getHWsFromJSON() {
        List<Hallway> hws = new ArrayList<>();

        JSONArray hwObjects = this.adversaryUpdateMessage.getJSONArray("hallways");
        for (int ii = 0; ii < hwObjects.length(); ++ii) {
            JSONObject hwObj = hwObjects.getJSONObject(ii);
            JSONArray layout = hwObj.getJSONArray("layout");
            List<int[]> tiles = new ArrayList<>();
            for (int yy = 0; yy < layout.length(); ++yy) {
                int[] pos = new int[] {
                        layout.getJSONArray(yy).getInt(0)
                        ,layout.getJSONArray(yy).getInt(1)};
                tiles.add(pos);
            }
            Hallway hw = new Hallway(tiles);
            hws.add(hw);
        }
        return hws;
    }
}
