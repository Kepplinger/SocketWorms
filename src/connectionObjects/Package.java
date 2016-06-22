package connectionObjects;

import gameobjects.GameInfo;
import gameobjects.GameWorld;
import gameobjects.Player;
import gameobjects.Rocket;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Andreas on 20.06.2016.
 * <p>
 * Die Package Klasse behinhaltet alle Daten, die vom Server an die Clients gschickt werden.
 */
public class Package implements Serializable {

    private GameInfo info;
    private GameWorld world;

    private List<Player> players;
    private Player currentPlayer;
    private Rocket rocket;

    public Package(GameInfo info, List<Player> players, GameWorld world, Player currentPlayer) {
        this.info = info;
        this.players = players;
        this.currentPlayer = currentPlayer;
        this.world = world;
    }

    public Package(GameInfo info, GameWorld world, List<Player> players, Player currentPlayer, Rocket rocket) {
        this(info, players, world, currentPlayer);
        this.rocket = rocket;
    }

    public GameWorld getWorld() {
        return world;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public GameInfo getInfo() {
        return info;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Rocket getRocket() {
        return rocket;
    }

    @Override
    public String toString() {

        if (world != null)
            return String.valueOf(world.getGameWorld().get(0).getBorder().size());
        else
            return "";

    }
}
