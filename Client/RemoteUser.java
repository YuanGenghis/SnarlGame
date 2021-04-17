import java.util.List;

public class RemoteUser extends User {
    public RemoteUser(List<String> names) {
        super(names);
    }

    public RemoteUser(GameManager gm) {
        super(gm);
    }

    public RemoteUser(int natural, List<Level> levels, List<String> names) {
        super(natural, levels, names);
    }
}
