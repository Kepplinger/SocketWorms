package debug;

import gameobjects.Explosion;
import gameobjects.Point;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import gameobjects.GameWorld;

import java.net.URL;
import java.util.*;

public class TestController implements Initializable {


    @FXML
    private Canvas canvas;

    private GraphicsContext gc;
    private GameWorld gameWorld;
    private Timer timer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        gameWorld = new GameWorld((int) canvas.getWidth(), (int) canvas.getHeight());
        gc = canvas.getGraphicsContext2D();

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                drawSurface();
            }
        }, new Date(), 100);

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
            }else{
                double inital = System.nanoTime();

                System.out.println(gameWorld.containsPoint(new Point((int)event.getX(),(int)event.getY())));
                gameWorld.getNearestPoint(new Point((int)event.getX(),(int)event.getY()));

                System.out.println(System.nanoTime() - inital);
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
    }
}