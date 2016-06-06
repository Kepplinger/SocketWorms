package client;

import gameobjects.Explosion;
import gameobjects.Point;
import gameobjects.Rocket;
import gameobjects.Surface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Andreas on 24.05.2016.
 */
public class GamefieldController implements Initializable {
    public AnchorPane pane;
    public Canvas cv_hud;
    @FXML
    private Canvas canvas;
    private GraphicsContext gc;
    private GraphicsContext hudgc;

    private ClientModel model;

    private Point mouse;
    private double currentSpeed = 0.5;
    private double angle = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mouse = new Point(-1, -1);
        model = ClientModel.getInstance();
        gc = canvas.getGraphicsContext2D();
        hudgc = cv_hud.getGraphicsContext2D();
        canvas.setOnMouseMoved((event -> {
            drawBackground();
            drawPlayers();
            drawRockets();
            drawForground();
        }));
        pane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                currentSpeed = currentSpeed >= 1 ? 0 : currentSpeed + 0.01;
            }
            if (event.getCode() == KeyCode.LEFT) {
                angle = angle <= -180 ? 0 : angle >= 180 ? -179 : angle + 1;
            }
            if (event.getCode() == KeyCode.RIGHT) {
                angle = angle <= -179 ? 180 : angle == 0 ? -1 : angle - 1;
            }
            if(event.getCode() == KeyCode.ENTER){
                Explosion explosion = new Explosion(new Point((int) mouse.getxCoord(), (int) mouse.getyCoord()));
                model.getWorld().destroySurface(explosion);

                double[] xCoord = new double[explosion.getBorder().length];
                double[] grasYCoord = new double[explosion.getBorder().length];

                for (int i = 0; i < explosion.getBorder().length; i++) {
                    xCoord[i] = explosion.getBorder()[i].getxCoord();
                    grasYCoord[i] = explosion.getBorder()[i].getyCoord();
                }

                gc.setFill(Color.RED);
                gc.fillPolygon(xCoord, grasYCoord, explosion.getBorder().length);
            }

            drawBackground();
            drawPlayers();
            drawRockets();
            drawForground();
            drawPreShoot();
        });
        drawBackground();
        drawPlayers();
        drawRockets();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                hudgc.drawImage(new Image("/images/hud_background.png"), 0, 0, 1024, 50);

                hudgc.setFill(Color.DARKGRAY);
                hudgc.fillRoundRect(10, 10, 104, 24, 5, 5);
                hudgc.setStroke(Color.BLACK);
                hudgc.strokeRoundRect(10, 10, 104, 24, 5, 5);

                hudgc.setFill(Color.DARKRED);
                hudgc.fillRoundRect(12, 12, currentSpeed * 100, 20, 5, 5);
                hudgc.setStroke(Color.WHITE);
                hudgc.strokeText(String.format("%d%%", (int) (currentSpeed * 100)), 49, 26);

                hudgc.setStroke(Color.WHITE);
                hudgc.strokeText(String.format("Mouse: X: %d Y: %d", mouse.getxCoord(), mouse.getyCoord()), 200, 15);
                hudgc.strokeText(String.format("Player: X: %d Y: %d", model.getLocalPlayer().getPosition().getxCoord(),
                        model.getLocalPlayer().getPosition().getyCoord()), 400, 15);
                hudgc.setStroke(Color.ORANGE);
                hudgc.strokeText(String.format("Winkel: %.2f", angle), 200, 35);
            }
        }, 100, 50);
    }

    private void drawRockets() {
        /*for(Rocket r:model.getRockets()){

        }*/
    }

    private void drawPreShoot() {
        double x = model.getLocalPlayer().getPosition().getxCoord();
        double y = model.getLocalPlayer().getPosition().getyCoord();
        double speed = currentSpeed * 90;
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        //gc.setLineDashes(25d, 10d);

        mouse = new Rocket(model.getLocalPlayer().getPosition(), speed, angle).drawFlightPath(gc,model.getWorld());
    }

    private void drawPlayers() {
        if (model != null) {
            int x = model.getLocalPlayer().getPosition().getxCoord();
            int y = model.getLocalPlayer().getPosition().getyCoord();

            gc.drawImage(new Image("/images/worm.png"), x - 4, y - 16, 8, 16);
        }

    }

    private void drawBackground() {
        gc.drawImage(new Image("/images/background.png"), 0, 0, 1024, 576);

        for (Surface surface : ClientModel.getInstance().getWorld().getGameWorld()) {
            gc.setFill(Color.GREEN);
            gc.strokePolygon(surface.getxCoords(), surface.getyCoords(), surface.getxCoords().length);
        }


        for (int i = 0; i < ClientModel.getInstance().getWorld().getGameWorld().size(); i++) {
            gc.setFill(Color.DARKGRAY);
            gc.fillPolygon(ClientModel.getInstance().getWorld().getGameWorld().get(i).getxCoords(),
                    ClientModel.getInstance().getWorld().getGameWorld().get(i).getyCoords(),
                    ClientModel.getInstance().getWorld().getGameWorld().get(i).getxCoords().length);
        }
    }

    private void drawForground() {


        //Targetmarker
        gc.drawImage(new Image("/images/crossfade.png"), mouse.getxCoord() - 11, mouse.getyCoord() - 11, 21, 21);

        //Localplayersign
        gc.drawImage(new Image("/images/local_arrow.png"), model.getLocalPlayer().getPosition().getxCoord() - 6,
                model.getLocalPlayer().getPosition().getyCoord() - 40, 11, 10);


    }
}
