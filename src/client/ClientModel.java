package client;

import gameobjects.GameWorld;
import gameobjects.Player;
import gameobjects.Point;
import gameobjects.Rocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
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

        Timer t = new Timer(true);
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Socket csocket = null;
                try {
                    csocket = new Socket();
                    csocket.connect(new InetSocketAddress(getServerIP(), 7918), 10000);
                    ObjectOutputStream out = new ObjectOutputStream(csocket.getOutputStream());

                    out.writeObject("UpdateRequest");
                    ObjectInputStream in = new ObjectInputStream(csocket.getInputStream());
                    world = (GameWorld) in.readObject();
                    world.setWorldChanged();
                    otherPlayers = (List<Player>) in.readObject();
                    otherPlayers.remove(otherPlayers.indexOf(localPlayer));

                    System.out.println("Daten aktualisiert");
                    csocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        },0,10);

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

    public void sendData() {
        Socket csocket = null;
        try {
            csocket = new Socket();
            csocket.connect(new InetSocketAddress(getServerIP(), 7918), 10000);
            ObjectOutputStream out = new ObjectOutputStream(csocket.getOutputStream());

            out.writeObject(getLocalPlayer());

            csocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
