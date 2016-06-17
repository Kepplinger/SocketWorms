package client;

import gameobjects.GameWorld;
import gameobjects.Player;
import gameobjects.Point;
import gameobjects.Rocket;

import java.util.*;

/**
 * Created by Andreas on 24.05.2016.
 */
public class ClientModel extends Observable {
    private static ClientModel singelton;

    private String serverIP;

    private Player localPlayer;

    private List<Player> otherPlayers;
    private List<Rocket> rockets;
    private GameWorld world;

    private ClientModel(){
        otherPlayers = new LinkedList<>();
        rockets = new LinkedList<>();
        world = new GameWorld(1024,576);

        Player sepp = new Player("Sepp");
        sepp.setPosition(new Point(600,20));
        otherPlayers.add(sepp);
        sepp = new Player("Franz");
        sepp.setPosition(new Point(200,20));
        otherPlayers.add(sepp);
        sepp = new Player("Herbert");
        sepp.setPosition(new Point(400,20));
        otherPlayers.add(sepp);
        sepp = new Player("Gustav");
        sepp.setPosition(new Point(430,20));
        otherPlayers.add(sepp);
        sepp = new Player("Mehmet");
        sepp.setPosition(new Point(800,20));
        otherPlayers.add(sepp);

    }


    public Player getLocalPlayer() {
        return localPlayer;
    }

    public void setLocalPlayer(Player player) {
        this.localPlayer = player;
        player.setPosition(new Point(500,20));
    }

    public List<Player> getOtherPlayers() {
        return otherPlayers;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }


    public static ClientModel getInstance(){
        if(singelton==null)
            singelton = new ClientModel();
        return singelton;
    }

    public List<Rocket> getRockets() {
        return rockets;
    }

    public GameWorld getWorld() {
        return world;
    }

    public void applyPhysics(){
        for(Player p:getPlayers()){
            p.applyPhysics(world);
        }
    }
    public List<Player> getPlayers(){
        List<Player> players = new ArrayList<>(getOtherPlayers());
        players.add(getLocalPlayer());
        return players;
    }
}
