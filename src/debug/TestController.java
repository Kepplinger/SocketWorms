package debug;

import connectionObjects.Package;
import connectionObjects.Request;
import connectionObjects.RequestType;
import gameobjects.Explosion;
import gameobjects.GameWorld;
import gameobjects.Point;
import gameobjects.Rocket;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class TestController implements Initializable {


    @FXML
    private Canvas canvas;

    private GraphicsContext gc;
    private GameWorld gameWorld;
    private Timer timer;
    private Rocket rocket1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        gameWorld = new GameWorld((int) canvas.getWidth(), (int) canvas.getHeight());
        gc = canvas.getGraphicsContext2D();

        Socket csocket;

        try {

            csocket = new Socket();
            csocket.connect(new InetSocketAddress("localhost", 7918), 10000);

            Timer t = new Timer(true);
            t.scheduleAtFixedRate(new TimerTask() {

                ObjectOutputStream outputStream;
                ObjectInputStream inputStream;

                @Override
                public void run() {

                    try {

                        if (csocket == null || csocket.isClosed()){
                            csocket.connect(new InetSocketAddress("localhost", 7918), 10000);
                        }

                        outputStream = new ObjectOutputStream(csocket.getOutputStream());
                        outputStream.writeObject(new Request(RequestType.RETURN_PACKAGE));

                        inputStream = new ObjectInputStream(csocket.getInputStream());
                        Package serverPackage = (Package) inputStream.readObject();

                        System.out.println("Punkte der Hauptoberfläche: " + serverPackage);

                    } catch (ConnectException e) {
                        System.out.println("[C] Verbindung verloren!");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 30);

        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR,"Master Caution!!!!").showAndWait();
        }



        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (rocket1 != null) {
                    Explosion explosion = rocket1.fly(gameWorld);
                    if (explosion != null) {
                        rocket1 = null;
                        gameWorld.destroySurface(explosion);
                    }
                }
                drawSurface();
            }
        }, new Date(), 20);

        canvas.setOnMousePressed(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                Explosion explosion = new Explosion(new Point((int) event.getX(), (int) event.getY()));
                gameWorld.destroySurface(explosion);

                double[] xCoord = new double[explosion.getBorder().length];
                double[] grasYCoord = new double[explosion.getBorder().length];

                for (int i = 0; i < explosion.getBorder().length; i++) {
                    xCoord[i] = explosion.getBorder()[i].getxCoord();
                    grasYCoord[i] = explosion.getBorder()[i].getyCoord();
                }

                gc.setFill(Color.RED);
                gc.fillPolygon(xCoord, grasYCoord, explosion.getBorder().length);
            } else if (event.getButton().equals(MouseButton.MIDDLE)) {
                rocket1 = new Rocket(new Point((int) event.getX(), (int) event.getY()), 45, 45);
            } else {
                System.out.println(gameWorld.containsPoint(new Point((int) event.getX(), (int) event.getY())));
                gameWorld.getNearestPoint(new Point((int) event.getX(), (int) event.getY()));
            }

        });
    }

    private void drawSurface() {


        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

//        for (Surface surface : gameWorld.getGameWorld()) {
//            gc.setFill(Color.GREEN);
//            gc.strokePolygon(surface.getxCoords(), surface.getyCoords(), surface.getxCoords().length);
//        }

        for (int i = 0; i < gameWorld.getGameWorld().size(); i++) {
//            if (i == 0) {
//                gc.setStroke(Color.GREEN);
//            } else
//                gc.setStroke(Color.RED);
            gc.setFill(Color.GREEN);
            gc.strokePolygon(gameWorld.getGameWorld().get(i).getxCoords(), gameWorld.getGameWorld().get(i).getyCoords(), gameWorld.getGameWorld().get(i).getxCoords().length);
        }

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        //gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (rocket1 != null) {
            gc.setFill(Color.RED);
            gc.fillOval(rocket1.getPosition().getxCoord() - 3, rocket1.getPosition().getyCoord() - 3, 6, 6);
        }
    }
}