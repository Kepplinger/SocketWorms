package client;

import gameobjects.GameWorld;
import gameobjects.Player;
import gameobjects.Point;
import gameobjects.Rocket;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

                        out.writeObject("UpdateRequest" + (world == null ? "+WorldRequest" : ""));
                        ObjectInputStream in = new ObjectInputStream(csocket.getInputStream());
                        if (world == null) {
                            world = (GameWorld) in.readObject();
                        }
                        otherPlayers = (List<Player>) in.readObject();
                        if (otherPlayers.size() > 0 && otherPlayers.contains(localPlayer)) {
                            localPlayer = otherPlayers.get(otherPlayers.indexOf(localPlayer));
                            otherPlayers.remove(otherPlayers.indexOf(localPlayer));
                            currentPlayer = (Player) in.readObject();
                            //System.out.println("Daten aktualisiert");
                            csocket.close();
                        }
                        else {
                            sendData();
                        }
                    }
                } catch (SocketTimeoutException e) {
                    new Alert(Alert.AlertType.WARNING, "Keine Antwort vom Server!", ButtonType.OK).showAndWait();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5);

    }


    public Player getLocalPlayer() {
        return localPlayer;
    }

    public void setLocalPlayer(Player player) {
        this.localPlayer = player;
    }

    public List<Player> getOtherPlayers() {
        ArrayList<Player> pl = new ArrayList<>(otherPlayers);
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

    public void applyPhysics() {
        for (Player p : getPlayers()) {
            p.applyPhysics(world);
        }
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
    }
}
