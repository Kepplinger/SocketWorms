package gameobjects;

import javafx.scene.canvas.GraphicsContext;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Andreas on 28.05.2016.
 */
public class Rocket {
    private Point start;
    private double initSpeed;
    private double angle;


    public Rocket(Point startPoint, double initialSpeed, double angle) {
        start = startPoint;
        initSpeed = initialSpeed;
        this.angle = angle;
    }

    public Point drawFlightPath(GraphicsContext gc, GameWorld world) {
        double GRAVITATIONAL_CONSTANT = 9.81;
        int x = start.getxCoord();
        int y = start.getyCoord();


        double xSpeed = Math.cos(Math.toRadians(angle)) * initSpeed;
        double ySpeed = Math.sin(Math.toRadians(angle)) * initSpeed;
        List<Integer> xList = new LinkedList<>();
        List<Integer> yList = new LinkedList<>();

        xList.add(x);
        yList.add(y);

        Point b = null;

        int cnt = 1;
        while (y <= 576) {
            x = (int) Math.round((double) x + xSpeed);
            ySpeed = (ySpeed - GRAVITATIONAL_CONSTANT);
            y -= ySpeed;
            xList.add(x);
            yList.add(y);
            cnt++;
            if (world.containsPoint(new Point(x, y))) {
                b = new Point(x,y);
                break;
            }
        }

        int u = 0;

        double[] xPoints = new double[cnt];
        double[] yPoints = new double[cnt];
        for (int i = 0; i < cnt; i++) {
            xPoints[i] = xList.get(i);
            yPoints[i] = yList.get(i);
            if(b.getxCoord() == xList.get(i) && b.getxCoord() == xList.get(i)){
                System.out.println(cnt-i-1);
            }
        }
        gc.strokePolyline(xPoints, yPoints, cnt);
        return new Point((int) xPoints[cnt - 1], (int) yPoints[cnt - 1]);
    }
}
