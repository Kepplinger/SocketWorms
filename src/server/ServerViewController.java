package server;

import gameobjects.Explosion;
import gameobjects.Player;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Andreas on 18.06.2016.
 */
public class ServerViewController implements Initializable {
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

    private ServerModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = ServerModel.getInstance();
        gc = canvas.getGraphicsContext2D();
        hudgc = cv_hud.getGraphicsContext2D();

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    hudgc.setFont(new Font("System", 14));
                    hudgc.drawImage(new Image("/images/hud_background.png"), 0, 0, 1024, 50);

                    drawBackground();
                    drawPlayers();
                    drawRockets();
                    drawForground();
                });
            }
        }, 100, 10);

        drawBackground();
        drawPlayers();
        drawRockets();
    }

    private void drawForground() {
        for (Player p : model.getOtherPlayers()) {
            if (!p.isDead()) {
                gc.setFill(Color.BLACK);
                gc.setFont(new Font("System", 14));
                gc.fillText(p.getName(), p.getPosition().getxCoord() - (getStringWidth(p.getName(), new Font("System", 14)) / 2), p.getPosition().getyCoord() - 45);
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

        if (model.getCurrentPlayer() != null) {
            //Targetmarker
            int x = model.getCurrentPlayer().getPosition().getxCoord();
            int y = model.getCurrentPlayer().getPosition().getyCoord();

            double angle360 = 0;
            double a = 0;
            double b = 0;
            double angle = model.getCurrentShoot().getAngle();

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
            gc.drawImage(new Image("/images/local_arrow.png"), model.getCurrentPlayer().getPosition().getxCoord() - 6,
                    model.getCurrentPlayer().getPosition().getyCoord() - 40, 11, 10);
        }
    }

    private void drawRockets() {
        for (Rocket r : model.getRockets()) {
            gc.setFill(Color.RED);
            gc.fillOval(r.getPosition().getxCoord() - 3, r.getPosition().getyCoord() - 3, 6, 6);
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
        if (model.getWorld().isWorldChanged()) {
            model.getWorld().setWorldChanged();
            GraphicsContext gcgf = canvas_gamefield.getGraphicsContext2D();
            gcgf.clearRect(0, 0, canvas_gamefield.getWidth(), canvas_gamefield.getHeight());
            for (Surface surface : model.getWorld().getGameWorld()) {
                gcgf.setFill(Color.GREEN);
                gcgf.strokePolygon(surface.getxCoords(), surface.getyCoords(), surface.getxCoords().length);
            }

            for (int i = 0; i < model.getWorld().getGameWorld().size(); i++) {
                gcgf.setFill(Color.DARKGRAY);
                gcgf.fillPolygon(model.getWorld().getGameWorld().get(i).getxCoords(),
                        model.getWorld().getGameWorld().get(i).getyCoords(),
                        model.getWorld().getGameWorld().get(i).getxCoords().length);
            }
        }
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public ServerModel getModel() {
        return model;
    }

    public double getStringWidth(String text, Font font) {
        Text l = new Text(text);
        l.setFont(font);
        return l.getLayoutBounds().getWidth();
    }
}
