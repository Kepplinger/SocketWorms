package client;

import gameobjects.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Andreas on 24.05.2016.
 */
public class GamefieldController implements Initializable {

    @FXML
    public Canvas canvas_gamefield;
    @FXML
    private Canvas canvas;
    @FXML
    public Canvas cv_hud;
    @FXML
    public AnchorPane pane;

    private GraphicsContext gc;
    private GraphicsContext hudgc;

    private ClientModel model;

    private double currentSpeed = 0.5;
    private double angle = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = ClientModel.getInstance();
        gc = canvas.getGraphicsContext2D();
        hudgc = cv_hud.getGraphicsContext2D();

        pane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                currentSpeed = currentSpeed >= 1 ? 0 : currentSpeed + 0.01;
            }
            if (event.getCode() == KeyCode.UP) {
                angle = angle <= -180 ? 0 : angle >= 180 ? -179 : angle + 1;
            }
            if (event.getCode() == KeyCode.DOWN) {
                angle = angle <= -179 ? 180 : angle == 0 ? -1 : angle - 1;
            }
            if (event.getCode() == KeyCode.LEFT) {
                model.getLocalPlayer().movePlayer(-2);
            }
            if (event.getCode() == KeyCode.RIGHT) {
                model.getLocalPlayer().movePlayer(2);
            }

            drawBackground();
            drawPlayers();
            drawRockets();
            drawForground();
        });
        pane.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                double speed = currentSpeed * 90;
                model.getRockets().add(new Rocket(model.getLocalPlayer().getPosition(), speed, angle));
            }

            drawBackground();
            drawPlayers();
            drawRockets();
            drawForground();
        });
        drawBackground();
        drawPlayers();
        drawRockets();

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    hudgc.setFont(new Font("System", 14));
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
                    hudgc.strokeText(String.format("Player: X: %d Y: %d", model.getLocalPlayer().getPosition().getxCoord(),
                            model.getLocalPlayer().getPosition().getyCoord()), 400, 15);
                    hudgc.setStroke(Color.ORANGE);
                    hudgc.strokeText(String.format("Winkel: %.2f", angle), 200, 35);
                    hudgc.setFill(Color.RED);
                    hudgc.setFont(new Font("System", 24));
                    hudgc.fillText(String.format("♥ %d", model.getLocalPlayer().getHealth()), 930, 35);

                    drawBackground();
                    drawPlayers();
                    drawRockets();
                    drawForground();
                });
                model.applyPhysics();
            }
        }, 100, 10);
    }

    private void drawRockets() {
        for(Rocket r:model.getRockets()){
            double speed = currentSpeed * 90;
            Explosion explosion = r.fly(model.getWorld());
            if(explosion == null){
                gc.setFill(Color.RED);
                gc.fillOval(r.getPosition().getxCoord()-3,r.getPosition().getyCoord()-3,6,6);
            }
            else{
                model.getRockets().remove(r);
                explosion.calculateDamage(model.getPlayers());
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
            //Point destination = new Rocket(model.getLocalPlayer().getPosition(), speed, angle).calculateFlightPath(gc, model.getWorld());
        }
    }

    private void drawPlayers() {
        if (model != null) {
            for (Player p : model.getPlayers()) {
                int x = p.getPosition().getxCoord();
                int y = p.getPosition().getyCoord();

                if (!p.isDead()) {
                    gc.drawImage(new Image("/images/worm.png"), x - 4, y - 16, 8, 16);
                } else {
                    gc.drawImage(new Image("/images/grave.png"), x - 4, y - 12, 8, 12);
                }
            }
        }

    }

    private void drawBackground() {
        if (ClientModel.getInstance().getWorld().isWorldChanged()) {
            ClientModel.getInstance().getWorld().setWorldChanged();
            GraphicsContext gcgf = canvas_gamefield.getGraphicsContext2D();
            gcgf.clearRect(0, 0, canvas_gamefield.getWidth(), canvas_gamefield.getHeight());
            for (Surface surface : ClientModel.getInstance().getWorld().getGameWorld()) {
                gcgf.setFill(Color.GREEN);
                gcgf.strokePolygon(surface.getxCoords(), surface.getyCoords(), surface.getxCoords().length);
            }


            for (int i = 0; i < ClientModel.getInstance().getWorld().getGameWorld().size(); i++) {
                gcgf.setFill(Color.DARKGRAY);
                gcgf.fillPolygon(ClientModel.getInstance().getWorld().getGameWorld().get(i).getxCoords(),
                        ClientModel.getInstance().getWorld().getGameWorld().get(i).getyCoords(),
                        ClientModel.getInstance().getWorld().getGameWorld().get(i).getxCoords().length);
            }
        }
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawForground() {
        for (Player p : model.getOtherPlayers()) {
            if (!p.isDead()) {
                gc.setFill(Color.BLACK);
                gc.setFont(new Font("System", 14));
                gc.fillText(p.getName(), p.getPosition().getxCoord() - (getStringWidth(p.getName(),new Font("System",14))/2), p.getPosition().getyCoord() - 45);
                gc.setFill(Color.RED);
                gc.setFont(new Font("System", 10));
                gc.fillText(String.format("%d%%", p.getHealth()), p.getPosition().getxCoord() -
                        (getStringWidth(String.format("%d%%", p.getHealth()),new Font("System",10))/2), p.getPosition().getyCoord() - 30);
            }
            else {
                gc.setFill(Color.RED);
                gc.setFont(new Font("System", 10));
                gc.fillText(p.getName(), p.getPosition().getxCoord() - (getStringWidth(p.getName(),new Font("System",10))/2), p.getPosition().getyCoord() - 15);
            }
        }

        //Targetmarker
        int x = model.getLocalPlayer().getPosition().getxCoord();
        int y = model.getLocalPlayer().getPosition().getyCoord();

        double angle360 = 0;
        double a = 0;
        double b = 0;

        if (angle > 0) {
            angle360 = angle;
            a = Math.sin(Math.toRadians(angle)) * 50;
            b = Math.cos(Math.toRadians(angle)) * 50;
        } else if (angle < 0) {
            angle360 = (180 - (angle * -1)) + 180;
            a = Math.sin(Math.toRadians(angle360)) * 50;
            b = Math.cos(Math.toRadians(angle360)) * 50;
        }
        gc.setFill(Color.BLACK);
        gc.fillOval(x + b - 4, y - a - 4, 8, 8);
        gc.setFill(Color.GOLD);
        gc.fillOval(x + b - 3, y - a - 3, 6, 6);
        //gc.drawImage(new Image("/images/crossfade.png"), mouse.getxCoord() - 11, mouse.getyCoord() - 11, 21, 21);

        //Localplayersign
        gc.drawImage(new Image("/images/local_arrow.png"), model.getLocalPlayer().getPosition().getxCoord() - 6,
                model.getLocalPlayer().getPosition().getyCoord() - 40, 11, 10);

    }

    public double getStringWidth(String text,Font font){
        Text l = new Text(text);
        l.setFont(font);
        return l.getLayoutBounds().getWidth();
    }
}
