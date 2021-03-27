import javafx.util.Pair;

public interface UserInterface {

    public void update(int[] position, GameState state);

    public void move(int[] dst, Player player);

}
