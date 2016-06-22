package gameobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreas on 20.06.2016.
 */
public class Package implements Serializable {
    private GameInfo info;
    private List<Player> changedPlayers;
    private GameWorld world;
    private Player current;

    public Package(GameInfo info, List<Player> changedPlayers, GameWorld world, Player current) {
        this.info = info;
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

    public GameInfo getInfo() {
        return info;
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
