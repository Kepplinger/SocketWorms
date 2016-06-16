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
        Timer refreshTimer = new Timer(true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                localPlayer.applyPhysics(world);
            }
        },100,10);
        world = new GameWorld(1024,576);
    }


    public Player getLocalPlayer() {
        return localPlayer;
    }

    public void setLocalPlayer(Player player) {
        this.localPlayer = player;
        player.setPosition(new Point(500,200));
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
}
