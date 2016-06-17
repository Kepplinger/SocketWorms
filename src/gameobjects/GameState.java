package gameobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Andreas on 17.06.2016.
 */
public class GameState implements Serializable {

    private HashMap<String,Player> teamA;
    private HashMap<String,Player> teamB;

    private List<Player> left_TeamA;
    private List<Player> left_TeamB;

    int round = -1;

    public GameState(){
        teamA = new HashMap<>();
        teamB = new HashMap<>();
    }

    public boolean readyToPlay(){
        return teamA.size() == teamB.size() && teamA.size()>0 && teamB.size()>0;
    }

    public void join(Player newPlayer){
        if(teamA.size()>=teamB.size()){
            teamA.put(newPlayer.getName(),newPlayer);
        }
        else{
            teamB.put(newPlayer.getName(),newPlayer);
        }
    }

    public void newRound(){
        round++;
        left_TeamA = new LinkedList<>(teamA.values());
        left_TeamB = new LinkedList<>(teamB.values());
    }

    public Player currentPlayer(){
        Player current = null;
        if(left_TeamB.size() == 0 && left_TeamA.size() == 0)
            newRound();

        if(left_TeamA.size()>=left_TeamB.size()){
            current = left_TeamA.get(0);
            left_TeamA.remove(0);
        }
        else {
            current = left_TeamB.get(0);
            left_TeamB.remove(0);
        }
        return current;
    }
}
