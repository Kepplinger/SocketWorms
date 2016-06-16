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

    public Point calculateFlightPath(GraphicsContext gc, GameWorld world) {

        double GRAVITATIONAL_CONSTANT = 9.81;
        int initialX = start.getxCoord();
        int initialY = start.getyCoord();

        double currentX;
        double currentY;

        int finalX;
        int finalY;

        double xSpeed = Math.cos(Math.toRadians(angle)) * initSpeed;
        double ySpeed = Math.sin(Math.toRadians(angle)) * initSpeed;
        List<Point> points = new LinkedList<>();

        while (initialY <= 576) {

            points.add(new Point(initialX, initialY));

            ySpeed = (ySpeed - GRAVITATIONAL_CONSTANT);
            finalX = (int) Math.round((double) initialX + xSpeed);
            finalY = (int) (initialY - ySpeed);

            if (world.containsPoint(new Point(finalX, finalY))) {

                currentX = initialX;
                currentY = initialY;

                do {
                    currentX = currentX + (finalX - initialX) / initSpeed * 5;
                    currentY = currentY + (finalY - initialY) / initSpeed * 5;

                    if (world.containsPoint(new Point((int) currentX, (int) currentY))) {
                        points.add(new Point((int) currentX, (int) currentY));
                        return drawFlightPath(points, gc);
                    }

                } while (Math.round(currentX) != finalX && Math.round(currentY) != finalY);
            }

            initialX = finalX;
            initialY = finalY;
        }

        return drawFlightPath(points, gc);
    }

    private Point drawFlightPath(List<Point> points, GraphicsContext gc) {
        double[] xPoints = new double[points.size()];
        double[] yPoints = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            xPoints[i] = points.get(i).getxCoord();
            yPoints[i] = points.get(i).getyCoord();
        }
        gc.strokePolyline(xPoints, yPoints, points.size());
        return new Point((int) xPoints[points.size() - 1], (int) yPoints[points.size() - 1]);
    }
}
