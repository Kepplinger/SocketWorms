package client;

import connectionObjects.Package;
import connectionObjects.Request;
import connectionObjects.RequestType;
import gameobjects.*;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

/**
 * Created by Andreas on 24.05.2016.
 */
public class ClientModel extends Observable {

    private String serverIP;

    private Player localPlayer;
    private Player currentPlayer;

    private List<Player> otherPlayers;
    private Rocket rocket;
    private GameWorld world;

    private Socket csocket = null;

    public ClientModel(String ip, Player player) {

        serverIP = ip;
        localPlayer = player;

        otherPlayers = new LinkedList<>();

        try {

            csocket = new Socket();
            csocket.connect(new InetSocketAddress(getServerIP(), 7918), 10000);

            ObjectOutputStream outputStream = new ObjectOutputStream(csocket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(csocket.getInputStream());

            Timer t = new Timer(true);
            t.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {

                    double initial = System.nanoTime();
                    try {

                        if (csocket == null || csocket.isClosed()) {
                            csocket.connect(new InetSocketAddress(getServerIP(), 7918), 10000);
                        }

                        outputStream.writeObject(new Request(RequestType.RETURN_PACKAGE));
                        Package serverPackage = (Package) inputStream.readObject();

                        currentPlayer = serverPackage.getCurrentPlayer();
                        world = serverPackage.getWorld();
                        rocket = serverPackage.getRocket();

                        setChanged();
                        notifyObservers();

                    } catch (ConnectException e) {
                        System.out.println("[C] Verbindung verloren!");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    System.out.println(System.nanoTime() - initial);
                }

            }, 0, 30);


        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Master Caution!!!!").showAndWait();
        }
    }


    public Player getLocalPlayer() {
        return localPlayer;
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

    public Rocket getRocket() {
        return rocket;
    }

    public void setRocket(Rocket rocket) {
        this.rocket = rocket;
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
        Thread thread = new Thread(() -> {
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
        });
        thread.setDaemon(true);
        thread.start();
    }
}
