package client;

import controller.Painter;
import gameobjects.*;
import gameobjects.Point;
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
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

import java.awt.*;
import java.awt.Rectangle;
import java.net.URL;
import java.util.*;

/**
 * Created by Andreas on 24.05.2016.
 */
public class GamefieldController implements Initializable {

    @FXML
    public Canvas canvas_gamefield;
    public Canvas canvas_player;
    @FXML
    private Canvas canvas;
    @FXML
    public Canvas cv_hud;
    @FXML
    public AnchorPane pane;

    private GraphicsContext gc;
    private GraphicsContext hudgc;

    private ClientModel model;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = ClientModel.getInstance();
        gc = canvas.getGraphicsContext2D();
        hudgc = cv_hud.getGraphicsContext2D();

        pane.setOnKeyPressed(event -> {
            if (model.getCurrentPlayer().equals(model.getLocalPlayer())) {
                if (event.getCode() == KeyCode.SPACE) {
                    model.getCurrentPlayer().getShoot().setCurrentSpeed(model.getCurrentPlayer().getShoot().getCurrentSpeed() >= 1 ? 0 : model.getCurrentPlayer().getShoot().getCurrentSpeed() + 0.01);
                }
                if (event.getCode() == KeyCode.UP) {
                    model.getLocalPlayer().getShoot().setAngle(model.getLocalPlayer().getShoot().getAngle() <= -180 ? 0 :
                            model.getLocalPlayer().getShoot().getAngle() >= 180 ? -179 : model.getLocalPlayer().getShoot().getAngle() + 1);
                }
                if (event.getCode() == KeyCode.DOWN) {
                    model.getLocalPlayer().getShoot().setAngle(model.getLocalPlayer().getShoot().getAngle() <= -179 ? 180 :
                            model.getLocalPlayer().getShoot().getAngle() == 0 ? -1 : model.getLocalPlayer().getShoot().getAngle() - 1);
                }
                if (event.getCode() == KeyCode.LEFT) {
                    model.getLocalPlayer().movePlayer(-2);
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    model.getLocalPlayer().movePlayer(2);
                }
                model.sendData();
            }
        });
        pane.setOnKeyReleased(event -> {
            if (model.getCurrentPlayer().equals(model.getLocalPlayer())) {
                if (event.getCode() == KeyCode.ENTER) {
                    double speed = model.getCurrentPlayer().getShoot().getCurrentSpeed() * 90;
                    model.getRockets().add(new Rocket(model.getLocalPlayer().getPosition(), speed, model.getCurrentPlayer().getShoot().getAngle()));
                    model.getLocalPlayer().getShoot().setFired(true);
                    model.sendData();
                }
            }
        });

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

                    if (model.getCurrentPlayer() != null && model.getLocalPlayer() != null) {
                        hudgc.setFill(Color.DARKRED);
                        hudgc.fillRoundRect(12, 12, model.getLocalPlayer().getShoot().getCurrentSpeed() * 100, 20, 5, 5);
                        hudgc.setStroke(Color.WHITE);
                        hudgc.strokeText(String.format("%d%%", (int) (model.getLocalPlayer().getShoot().getCurrentSpeed() * 100)), 49, 26);

                        if (model.getLocalPlayer().getPosition() != null) {
                            hudgc.setStroke(Color.WHITE);
                            hudgc.strokeText(String.format("Player: X: %d Y: %d", model.getLocalPlayer().getPosition().getxCoord(),
                                    model.getLocalPlayer().getPosition().getyCoord()), 400, 15);
                        }
                        hudgc.setStroke(Color.ORANGE);
                        hudgc.strokeText(String.format("Winkel: %.2f", model.getLocalPlayer().getShoot().getAngle()), 200, 35);
                        hudgc.setFill(Color.RED);
                        hudgc.setFont(new Font("System", 24));
                        hudgc.fillText(String.format("♥ %d", model.getLocalPlayer().getHealth()), 930, 35);

                        drawBackground();
                        drawPlayers();
                        drawRockets();
                        drawForground();
                    }
                });
            }
        }, 100, 10);
    }

    private void drawRockets() {
        for (Rocket r : model.getRockets()) {
            Explosion explosion = r.fly(model.getWorld());
            if (explosion == null) {
                gc.setFill(Color.RED);
                gc.fillOval(r.getPosition().getxCoord() - 3, r.getPosition().getyCoord() - 3, 6, 6);
            } else {
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
        GraphicsContext gcPl = canvas_player.getGraphicsContext2D();
        gcPl.clearRect(0, 0, canvas.getWidth(), canvas.getWidth());
        if (model != null && model.getPlayers() != null) {
            for (Player p : model.getPlayers()) {
                if (p != null && p.getPosition() != null) {
                    int x = p.getPosition().getxCoord();
                    int y = p.getPosition().getyCoord();

                    if (!p.isDead()) {
                        if (p.getShoot().getAngle() < 90 && p.getShoot().getAngle() > -90) {
                            gcPl.drawImage(new Image(String.format("/images/worms/Rworm%d.png", p.getWormSkin())), x - 4, y - 16, 8, 16);
                        } else {
                            gcPl.drawImage(new Image(String.format("/images/worms/worm%d.png", p.getWormSkin())), x - 4, y - 16, 8, 16);
                        }
                    } else {
                        gcPl.drawImage(new Image("/images/grave.png"), x - 4, y - 12, 8, 12);
                    }
                }
            }
        }

    }

    private void drawBackground() {
        if (model.getWorld() != null) {
            if (ClientModel.getInstance().getWorld().isWorldChanged()) {
                //System.out.println("[Client] Welt gezeichnet!");
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
        }
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawForground() {
        //gc.clearRect(0, 0, canvas.getWidth(), canvas.getWidth());
        for (Player p : model.getOtherPlayers()) {
            if (p != null && p.getPosition() != null) {
                if (!p.isDead()) {
                    if (p.getTeam() != null && model.getLocalPlayer().getTeam() != null && !p.getTeam().equals(model.getLocalPlayer().getTeam())) {
                        gc.drawImage(new Image("/images/enemy_arrow.png"), p.getPosition().getxCoord() - 6,
                                p.getPosition().getyCoord() - 70, 11, 10);
                    }
                    gc.setFill(Color.BLACK);
                    gc.setFont(new Font("System", 12));
                    gc.fillText(p.getName(), p.getPosition().getxCoord() - (getStringWidth(p.getName(), new Font("System", 12)) / 2), p.getPosition().getyCoord() - 45);
                    gc.setFill(Color.RED);
                    gc.setFont(new Font("System", 10));
                    gc.fillText(String.format("%d%%", p.getHealth()), p.getPosition().getxCoord() -
                            (getStringWidth(String.format("%d%%", p.getHealth()), new Font("System", 10)) / 2), p.getPosition().getyCoord() - 30);
                } else {
                    gc.setFill(Color.RED);
                    gc.setFont(new Font("System", 10));
                    gc.fillText(p.getName(), p.getPosition().getxCoord() - (getStringWidth(p.getName(), new Font("System", 10)) / 2), p.getPosition().getyCoord() - 15);
                }
            }
        }
        //Localplayersign
        if (model.getCurrentPlayer() != null && model.getCurrentPlayer().getPosition() != null)
            if (!model.getCurrentPlayer().equals(model.getLocalPlayer()))
                gc.drawImage(new Image("/images/current_arrow.png"), model.getCurrentPlayer().getPosition().getxCoord() - 6,
                        model.getCurrentPlayer().getPosition().getyCoord() - 70, 11, 10);
        if (model.getLocalPlayer() != null && model.getLocalPlayer().getPosition() != null)
            gc.drawImage(new Image("/images/local_arrow.png"), model.getLocalPlayer().getPosition().getxCoord() - 6,
                    model.getLocalPlayer().getPosition().getyCoord() - 70, 11, 10);


        if (model.getCurrentPlayer() != null && model.getCurrentPlayer().getPosition() != null) {
            //Targetmarker
            int x = model.getCurrentPlayer().getPosition().getxCoord();
            int y = model.getCurrentPlayer().getPosition().getyCoord();

            double angle360 = 0;
            double a = 0;
            double b = 0;
            double curAngle = model.getCurrentPlayer().getShoot().getAngle();

            if (curAngle > 0) {
                angle360 = curAngle;
                a = Math.sin(Math.toRadians(curAngle)) * 50;
                b = Math.cos(Math.toRadians(curAngle)) * 50;
            } else if (curAngle < 0) {
                angle360 = (180 - (curAngle * -1)) + 180;
                a = Math.sin(Math.toRadians(angle360)) * 50;
                b = Math.cos(Math.toRadians(angle360)) * 50;
            }
            gc.setFill(Color.BLACK);
            gc.fillOval(x + b - 4, y - a - 4, 8, 8);
            gc.setFill(Color.GOLD);
            gc.fillOval(x + b - 3, y - a - 3, 6, 6);
            //gc.drawImage(new Image("/images/crossfade.png"), mouse.getxCoord() - 11, mouse.getyCoord() - 11, 21, 21);

        }

    }

    public double getStringWidth(String text, Font font) {
        Text l = new Text(text);
        l.setFont(font);
        return l.getLayoutBounds().getWidth();
    }
}
