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
    private GameInfo info;

    int round = -1;

    public GameState(List<Player> players) {
        this.players = players;
        teamA = new HashMap<>();
        teamB = new HashMap<>();
        info = new GameInfo();
    }

    public boolean readyToPlay() {
        return teamA.size() == teamB.size() && teamA.size() > 0 && teamB.size() > 0;
    }

    public synchronized void join(Player newPlayer) {
        if (players.size() == 0 || !players.contains(newPlayer)) {


            Random random = new Random();
            int x = random.nextInt(1000) + 20;
            newPlayer.setPosition(new Point(x, 20));

            //System.out.println(newPlayer.getName()+" "+newPlayer.getPosition());

            if (teamA.size() <= teamB.size()) {
                newPlayer.setTeam("A");
                teamA.put(newPlayer.getName(), newPlayer);
            } else {
                newPlayer.setTeam("B");
                teamB.put(newPlayer.getName(), newPlayer);
            }
            players.add(newPlayer);
        }
    }

    public void newRound() {
        round++;
        left_TeamA = new LinkedList<>(teamA.values());
        left_TeamB = new LinkedList<>(teamB.values());

        if (getDeadPlayer(teamA.values()).size() == teamA.size()) {
            info.setPoints_b(info.getPoints_b() + getDeadPlayer(teamB.values()).size() == 0 ? 3 : getDeadPlayer(teamB.values()).size() < teamB.size() ? 2 : 1);
            for (Player p : teamA.values()) {
                p.heal(100);
            }
        } else if (getDeadPlayer(teamB.values()).size() == teamB.size()) {
            info.setPoints_a(info.getPoints_a() + getDeadPlayer(teamA.values()).size() == 0 ? 3 : getDeadPlayer(teamA.values()).size() < teamA.size() ? 2 : 1);
            for (Player p : teamA.values()) {
                p.heal(100);
            }
        }

    }

    public Player nextPlayer() {
        Player next = null;
        if (left_TeamA == null || left_TeamB == null)
            newRound();

        left_TeamA.removeAll(getDeadPlayer(teamA.values()));
        left_TeamB.removeAll(getDeadPlayer(teamB.values()));

        if ((left_TeamB.size() == 0 && left_TeamA.size() == 0)) {
            newRound();
            left_TeamA.removeAll(getDeadPlayer(teamA.values()));
            left_TeamB.removeAll(getDeadPlayer(teamB.values()));
        }


        if (left_TeamA.size() > left_TeamB.size()) {
            next = left_TeamA.get(0);
            left_TeamA.remove(0);
        } else if (left_TeamA.size() == left_TeamB.size()) {
            if (new Random().nextInt(2) == 0) {
                next = left_TeamB.get(0);
                left_TeamB.remove(0);
            } else {
                next = left_TeamA.get(0);
                left_TeamA.remove(0);
            }
        } else {

            next = left_TeamB.get(0);
            left_TeamB.remove(0);
        }
        next.setCurrent(true);
        return next;
    }

    public void printTeams() {
        System.out.println("TeamA:");
        for (Player p : teamA.values()) {
            System.out.println("\t" + p.getName());
        }
        System.out.println("TeamB:");
        for (Player p : teamB.values()) {
            System.out.println("\t" + p.getName());
        }
    }

    private List<Player> getDeadPlayer(Collection<Player> plyrs) {
        List<Player> pl = new ArrayList<>();
        for (Player p : players) {
            if (p.isDead()) {
                pl.add(p);
            }
        }
        return pl;
    }

    public GameInfo getInfo() {
        info.setPlayer_A_cnt(teamA.size());
        info.setPlayer_B_cnt(teamB.size());
        info.setCurDeaths_A(getDeadPlayer(teamA.values()).size());
        info.setCurDeaths_B(getDeadPlayer(teamB.values()).size());
        return info;
    }
}
