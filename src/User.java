import java.awt.event.KeyEvent;
import java.util.*;

public class User implements UserInterface{
    GameManager gm;


    public User(List<String> names) {
        this.gm = new GameManager(names);
        gm.init();
    }

    // deal with arrow movement of user
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch( keyCode ) {
            case KeyEvent.VK_UP:
                // handle up
                System.out.println(1);
                break;
            case KeyEvent.VK_DOWN:
                // handle down
                break;
            case KeyEvent.VK_LEFT:
                // handle left
                break;
            case KeyEvent.VK_RIGHT :
                // handle right
                break;
        }
    }
    public static void main(String[] args) {
        User user = new User(Arrays.asList("JC", "hollis"));
//        Timer t = new Timer();
//        t.scheduleAtFixedRate(
//                new TimerTask() {
//                    @Override
//                    public void run() {
//                        keyPressed();
//                    }
//                }, 0, 1000
//        );

    }

}
