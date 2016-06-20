package gameobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreas on 20.06.2016.
 */
public class Package implements Serializable {
    List<Player> changedPlayers;
    GameWorld world;
    Player current;

    public Package(List<Player> changedPlayers, GameWorld world,Player current) {
        this.changedPlayers = changedPlayers;
        this.world = world;
        this.current = current;
    }

    public GameWorld getWorld() {
        return world;
    }

    public Player getCurrentPlayer() {
        return current;
    }

    public List<Player> updatePlayerList(List<Player> listToUpdate){
        List<Player> newList = new ArrayList<>(changedPlayers);
        for(Player p:changedPlayers){
            if(!listToUpdate.contains(p)){
                newList.add(p);
            }
            else{
                newList.set(newList.indexOf(p),p);
            }
        }
        return newList;
    }
}
