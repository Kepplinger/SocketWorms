package client;

import gameobjects.*;
import gameobjects.Package;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * Created by Andreas on 24.05.2016.
 */
public class ClientModel extends Observable {
    private static ClientModel singelton;


    private String serverIP;

    private Player localPlayer;
    private Player currentPlayer;

    private List<Player> otherPlayers;
    private List<Rocket> rockets;
    private GameWorld world;

    int worldRequest = 32;

    private ClientModel() {
        otherPlayers = new LinkedList<>();
        rockets = new LinkedList<>();

        Timer t = new Timer(true);
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                Socket csocket = null;
                try {
                    //System.out.println("Neue Datenabfrage");
                    if (getServerIP() != null) {
                        csocket = new Socket();
                        csocket.connect(new InetSocketAddress(getServerIP(), 7918), 10000);
                        ObjectOutputStream out = new ObjectOutputStream(csocket.getOutputStream());

                        synchronized (otherPlayers) {

                            if (world == null && otherPlayers.size() == 0 || worldRequest == 0) {
                                out.writeObject(UpdateInformation.World_a_Player);
                            } else if (world == null) {
                                out.writeObject(UpdateInformation.World);
                            } else {
                                out.writeObject(UpdateInformation.Player);
                            }
                            ObjectInputStream in = new ObjectInputStream(csocket.getInputStream());

                            Package p = (Package) in.readObject();
                            if (p.getWorld() != null)
                                world = p.getWorld();
                            if (p.getCurrentPlayer() != null)
                                currentPlayer = p.getCurrentPlayer();
                            otherPlayers = p.updatePlayerList(otherPlayers);

                            if (otherPlayers != null && otherPlayers.size() > 0 && otherPlayers.contains(localPlayer)) {
                                localPlayer = otherPlayers.get(otherPlayers.indexOf(localPlayer));
                                otherPlayers.remove(otherPlayers.indexOf(localPlayer));
                            } else {
                                csocket.close();
                                sendData();
                            }
                        }
                        //System.out.println("[Client] Daten empfangen.");
                    }
                } catch (ConnectException e) {
                    System.out.println("[C] Verbindung verloren!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                worldRequest--;
                if(worldRequest<0)
                    worldRequest = 32;
            }
        }, 0, 20);

    }


    public Player getLocalPlayer() {
        return localPlayer;
    }

    public void setLocalPlayer(Player player) {
        this.localPlayer = player;
    }

    public synchronized List<Player> getOtherPlayers() {
        ArrayList<Player> pl = new ArrayList<>(otherPlayers);
        if (pl.contains(localPlayer))
            pl.remove(pl.indexOf(localPlayer));
        return pl;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }


    public static ClientModel getInstance() {
        if (singelton == null)
            singelton = new ClientModel();
        return singelton;
    }

    public List<Rocket> getRockets() {
        return rockets;
    }

    public GameWorld getWorld() {
        return world;
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>(getOtherPlayers());
        players.add(getLocalPlayer());
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void sendData() {
        Thread t = new Thread(() -> {
            Socket csocket = null;
            try {
                csocket = new Socket();
                csocket.connect(new InetSocketAddress(getServerIP(), 7918), 10000);
                ObjectOutputStream out = new ObjectOutputStream(csocket.getOutputStream());

                out.writeObject(getLocalPlayer());
                //System.out.println("[Client] Daten gesendet!");
                csocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
