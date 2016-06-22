package gameobjects;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Andreas on 20.06.2016.
 *
 * Die Package Klasse behinhaltet alle Daten, die vom Server an die Clients gschickt werden.
 *
 */
public class Package implements Serializable {

    private GameInfo info;
    private List<Player> players;
    private Player currentPlayer;
    private GameWorld world;

    public Package(GameInfo info, List<Player> players, GameWorld world, Player currentPlayer) {
        this.info = info;
        this.players = players;
        this.currentPlayer = currentPlayer;
        this.world = world;
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
}
