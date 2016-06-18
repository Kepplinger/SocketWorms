package gameobjects;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Andreas on 17.06.2016.
 */
public class GameState implements Serializable {

    private HashMap<String, Player> teamA;
    private HashMap<String, Player> teamB;

    private List<Player> left_TeamA;
    private List<Player> left_TeamB;

    private List<Player> players;

    int round = -1;

    public GameState(List<Player> players) {
        this.players = players;
        teamA = new HashMap<>();
        teamB = new HashMap<>();
    }

    public boolean readyToPlay() {
        return teamA.size() == teamB.size() && teamA.size() > 0 && teamB.size() > 0;
    }

    public void join(Player newPlayer) {
        players.add(newPlayer);

        Random random = new Random();
        int x = random.nextInt(1000) + 20;
        newPlayer.setPosition(new Point(x, 20));


        if (teamA.size() <= teamB.size()) {
            teamA.put(newPlayer.getName(), newPlayer);
        } else {
            teamB.put(newPlayer.getName(), newPlayer);
        }
    }

    public void newRound() {
        round++;
        left_TeamA = new LinkedList<>(teamA.values());
        left_TeamB = new LinkedList<>(teamB.values());
    }

    public Player nextPlayer() {
        Player next = null;
        if (left_TeamA == null || left_TeamB == null || (left_TeamB.size() == 0 && left_TeamA.size() == 0))
            newRound();

        if (left_TeamA.size() >= left_TeamB.size()) {
            next = left_TeamA.get(0);
            left_TeamA.remove(0);
        } else {
            next = left_TeamB.get(0);
            left_TeamB.remove(0);
        }
        return next;
    }
}
