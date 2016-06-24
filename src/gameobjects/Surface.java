package gameobjects;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Simon on 26.05.2016.
 *
 * Ein Surface Objekt stellt jeweils ein Polygon in der Welt dar. Die Welt kann aus mehreren Surfaces bestehen
 * (z.B.: schwebende Teile).
 */
public class Surface implements Serializable {

    private List<Point> border;

    // Die Arrays werden benötig, um die Oberfläche im Canvas zu zeichnen.
    private double[] xCoords;
    private double[] yCoords;

    public Surface(List<Point> border) {
        this.border = border;
        refreshValues();
    }

    public void setBorder(List<Point> border) {
        this.border = border;
        refreshValues();
    }


    public List<Point> getBorder() {
        return border;
    }

    /**
     * Synchronisiert alle Punkte von der Liste mit den Arrays
     * @return
     */
    private void refreshValues() {
        xCoords = new double[border.size()];
        yCoords = new double[border.size()];

        for (int i = 0; i < border.size(); i++) {
            xCoords[i] = border.get(i).getxCoord();
            yCoords[i] = border.get(i).getyCoord();
        }
    }

    public double[] getyCoords() {
        return yCoords;
    }

    public double[] getxCoords() {
        return xCoords;
    }

    /**
     * Liefert zurück, ob sich der Übergebene Punkt im Polygon befindet.
     * @param point
     * @return
     */
    public boolean contains(Point point) {

        int i;
        int j;
        boolean result = false;

        for (Point surfacePoint : border) {
            if (surfacePoint.getxCoord() == point.getxCoord() && surfacePoint.getyCoord() == point.getyCoord()) {
                return true;
            }
        }

        for (i = 0, j = border.size() - 1; i < border.size(); j = i++) {
            if ((border.get(i).getyCoord() > point.getyCoord()) != (border.get(j).getyCoord() > point.getyCoord()) &&
                    (point.getxCoord() < (border.get(j).getxCoord() - border.get(i).getxCoord()) * (point.getyCoord() - border.get(i).getyCoord()) / (border.get(j).getyCoord() - border.get(i).getyCoord()) + border.get(i).getxCoord())) {
                result = !result;
            }
        }
        return result;
    }

    /**
     * Liefert den Index, des am nächsten gelgenen Punktes zurück.
     * @param point
     * @return
     */
    public int getIndexofNearestPoint(Point point) {

        double smallestDistance = Double.MAX_VALUE;
        int index = 0;

        for (Point borderPoint : border){
            if (smallestDistance > getDistance(borderPoint, point)) {

                smallestDistance = getDistance(borderPoint, point);
                index = border.indexOf(borderPoint);

            }
        }
        return index;
    }

    private double getDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(Math.abs(point1.getxCoord() - point2.getxCoord()), 2) + Math.pow(Math.abs(point1.getyCoord() - point2.getyCoord()), 2));
    }

}
