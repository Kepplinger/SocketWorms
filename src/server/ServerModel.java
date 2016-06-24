package server;

import connectionObjects.Connection;
import connectionObjects.Package;
import gameobjects.*;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by Andreas on 18.06.2016.
 */
public class ServerModel {

    private static ServerModel ourInstance = new ServerModel();
    private String serverIP;

    private Player currentPlayer;
    private Rocket rocket;

    private List<Player> players;
    private GameWorld world;
    private GameState state;

    private ServerModel() {

        try {
            serverIP = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        players = new LinkedList<>();
        state = new GameState(players);
        world = new GameWorld(1024, 576);


        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (state.readyToPlay()) {
                    applyPhysics();

                    if (rocket != null && rocket.isExploded()) {
                        //neue Runde
                        rocket = null;
                        currentPlayer = state.nextPlayer();
                    }
                }
            }
        }, 50, 30);

        Thread serverConnection = new Thread(() -> {
            state.join(new Player("Sepp"));
            state.join(new Player("Mehmet"));
            state.join(new Player("Franz"));
            state.join(new Player("Gustav"));
            state.join(new Player("Hans"));
            state.join(new Player("Günther"));

            try {
                ServerSocket socket = new ServerSocket(7918);
                while (true) {

                    Socket csocket = socket.accept();

                    Thread clientThread = new Thread(new Connection(csocket, this));
                    clientThread.setDaemon(true);
                    clientThread.start();

//======================================================================================================================
//                    Thread clientThread = new Thread(() -> {
//                        try {
//                            ObjectInputStream in = new ObjectInputStream(csocket.getInputStream());
//                            Object receivedP = in.readObject();
//
//                            ObjectOutputStream out = new ObjectOutputStream(csocket.getOutputStream());
//                            if (receivedP instanceof RequestType) {
//                                //TODO
//                                //Client will Daten
//                                if (receivedP.equals(RequestType.Player)) {
//                                    out.writeObject(new Package(state.getInfo(),changedPlayers(), null, currentPlayer));
//                                } else if (receivedP.equals(RequestType.World)) {
//                                    out.writeObject(new Package(state.getInfo(),null, world, currentPlayer));
//                                } else if (receivedP.equals(RequestType.World_a_Player)) {
//                                    out.writeObject(new Package(state.getInfo(),changedPlayers(), world, currentPlayer));
//                                }
//                            } else if (receivedP instanceof Player) {
//                                //Client schickt Daten
//                                Player pCL = (Player) receivedP;
//                                if (players.contains(pCL)) {
//                                    if (pCL.equals(currentPlayer)) {
//                                        currentPlayer = pCL;
//                                        currentShoot = pCL.getShoot();
//                                        players.set(players.indexOf(pCL), pCL);
//                                        //System.out.println(players.get(players.indexOf(pCL))); //Gesendeter Spieler
//                                    }
//                                } else {
//                                    System.out.println("[Server] Der Spieler ist nicht vorhanden!");
//                                    System.out.println(pCL.getName() +" ist dem Spiel beigetreten.");
//                                    state.join(pCL);
//                                }
//                            } else {
//                                System.out.println("Unidentifiable message from Client");
//                            }
//                        } catch (IOException ex) {
//                            ex.printStackTrace();
//                        } catch (ClassNotFoundException ex) {
//                            ex.printStackTrace();
//                        }
//                    });
//======================================================================================================================

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        serverConnection.setDaemon(true);
        serverConnection.setName("ServerConnection-Thread");
        serverConnection.start();

//        Timer debugTimer = new Timer(true);
//        debugTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                players.get(new Random().nextInt(players.size())).removeHealth(100);
//            }
//        }, 100, 3000);
    }

    public static ServerModel getInstance() {
        return ourInstance;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void applyPhysics() {

        for (Player player : getPlayers()) {
            player.applyPhysics(getWorld());
        }

        if (rocket != null) {
            Explosion explosion = rocket.fly(getWorld());

            if (explosion != null) {
                explosion.calculateDamage(getPlayers());
                getWorld().destroySurface(explosion);
            }
        }
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public GameWorld getWorld() {
        return world;
    }

    public double getRocketAngle() {
        if (rocket != null)
            return rocket.getAngle();
        else
            return 0;
    }

    public Rocket getRocket() {
        return rocket;
    }

    public GameState getState() {
        return state;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public List<Player> getOtherPlayers() {
        ArrayList<Player> p = new ArrayList<>(getPlayers());
        p.remove(currentPlayer);
        return p;
    }

    public List<Player> changedPlayers() {
        return getPlayers();

//        List<Player> pls = new ArrayList<>();
//        for (Player p : getPlayers()) {
//            if (p != null && p.hasChanged())
//                pls.add(p);
//        }
//        return pls;
    }

    public Package getPackage(){
        return new Package(state.getInfo(), players, world, currentPlayer);
    }

}
