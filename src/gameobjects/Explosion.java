package gameobjects;

import java.util.Date;
import java.util.List;

/**
 * Created by Simon on 26.05.2016.
 *
 * Diese Klasse ist für die Berechnung der Explosion zuständig und enthählt Methoden, damit man den Karter und den
 * Schaden der Explosion ausrechnen kann.
 */
public class Explosion {

    private Point[] border;
    private Point center;

    public Explosion(Point point) {
        center = point;
        border = new Point[GameWorld.EXPLOSIONPOINTS];
        determineBorder(point);
    }

    public Point[] getBorder() {
        return border;
    }

    /**
     * Berechnet den Rand der Explosion.
     * @param point
     */
    public void determineBorder(Point point) {
        double angle = 0;

        for (int i = GameWorld.EXPLOSIONPOINTS - 1; i >= 0; i--) {
            border[i] = new Point((int) (point.getxCoord() + Math.cos(Math.toRadians(angle)) * GameWorld.EXPLOSIONRADIUS), (int) (point.getyCoord() + Math.sin(Math.toRadians(angle)) * GameWorld.EXPLOSIONRADIUS));
            angle += (360 / GameWorld.EXPLOSIONPOINTS);
        }
    }

    /**
     * Gibt zurück ob sich der übergebene Punkt in der Explosion befindet.
     * @param point
     * @return
     */
    public boolean contains(Point point) {

        if (getDistance(center, point) < GameWorld.EXPLOSIONRADIUS) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Gitb den Index des Punktes zurück, der am nächsten ist.
     * @param point
     * @return
     */
    public int getIndexofNearestPoint(Point point) {
        double smallestDistance = Double.MAX_VALUE;
        int index = 0;

        for (int i = 0; i < border.length; i++) {
            if (smallestDistance > getDistance(border[i], point)) {
                smallestDistance = getDistance(border[i], point);
                index = i;
            }
        }

        return index;
    }

    /**
     * Berechnet den Schaden der umliegenden Würmer
     * @param players
     */
    public void calculateDamage(List<Player> players) {
        for (Player p : players) {
            if (contains(p.getPosition())) {
                p.removeHealth((int) (GameWorld.EXPLOSIONRADIUS - getDistance(p.getPosition(), center)));
            }
        }
    }

    private double getDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(Math.abs(point1.getxCoord() - point2.getxCoord()), 2) + Math.pow(Math.abs(point1.getyCoord() - point2.getyCoord()), 2));
    }
}
