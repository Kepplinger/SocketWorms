package gameobjects;

import javafx.scene.shape.Polygon;
import sun.awt.image.ImageWatched;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Kepplinger on 24.05.2016.
 */
public class GameWorld {

    public final static int EXPECTEDVARIATIONS = 10;    // Die erwartete Anzahl an Änderungen im Terrain
    public final static int MAXELEVATION = 1000;        // Die maximale Höhe einer Änderung im Terrain
    public final static int MINVARIATIONYWIDTH = 100;   // Die minimale Breite einer Änderung im Terrain
    public final static int EXPLOSIONRADIUS = 50;       // Radius einer Explosion
    public final static int EXPLOSIONPOINTS = 120;      // Anzahl der Punkte einer Explosion

    private List<Surface> gameWorld;    // Liste aller Oberflächen im Spiel
    private final int width;            // Breite der Spielwelt
    private final int height;           // Höhe der Spielwelt

    private boolean worldChanged = false;

    public GameWorld(int width, int height) {
        this.width = width;
        this.height = height;
        gameWorld = Collections.synchronizedList(new LinkedList<>());
        generateSurface();
    }

    /**
     * Eine Oberfläche, die als Spielwelt dienen soll, wird zufällig generiert.
     */
    private void generateSurface() {

        List<Point> generatedWorld = new LinkedList<>();

        Random random = new Random();

        int relativHeight = random.nextInt(251) + 100;                       // Bestimmt einen Anfangswert zwischen 100 und 350
        int remainingHorizontal = width;                                     // Diese Variable speichert wie viel Platz noch bis zum Ende in X-Richtung vorhanden ist
        int averageWaveLength = remainingHorizontal / EXPECTEDVARIATIONS;    // Die erwartete Länge (in X-Richtung) eines Hügels vom Tal bis zur Spitze

        int direction;  // Gibt die Richtung der Änderung an
        int waveLength; // Länge der Änderung
        int waveHeight; // Höhe der Änderung

        //Alle Punkte auf der linken Seite der Welt werden hinzugefügt
        for (int i = height; i >= height - relativHeight; i--) {
            generatedWorld.add(new Point(0, i));
        }

        while (remainingHorizontal > 0) {
            direction = random.nextInt((relativHeight - 20) * 2) - 190;     // Wenn direction negativ ist kommt eine Berg, wenn es positiv ist ein Tal
            waveLength = Math.max(random.nextInt(averageWaveLength) + averageWaveLength / 2, MINVARIATIONYWIDTH);   // Ermittelt die Länge der Änderung
            remainingHorizontal -= waveLength;      // Die verbleibende Platz in X-Richtung wird ermittelt

            if (remainingHorizontal <= averageWaveLength / 2) {
                waveLength += remainingHorizontal;  // Wenn der verbleibende Platz zu klein ist, wird er zur derzeitigen Länge addiert
                remainingHorizontal = 0;
            }

            if (direction < 0) {
                /* VERGRÖSSERN */
                waveHeight = Math.min(random.nextInt(Math.abs(360 - relativHeight - 20)), MAXELEVATION);

                for (int i = 0; i <= waveLength; i++) {
                    generatedWorld.add(new Point(width - remainingHorizontal - waveLength + i, (int) (576 - (relativHeight + 20 + waveHeight * mountainFormula((double) i / (double) waveLength)))));
                }
                relativHeight += waveHeight;
            } else {
                /* VERKLEINERN */
                waveHeight = Math.min(random.nextInt(relativHeight - 20), MAXELEVATION);

                for (int i = 0; i <= waveLength; i++) {
                    generatedWorld.add(new Point(width - remainingHorizontal - waveLength + i, (int) (576 - (relativHeight + 20 + waveHeight * valleyFormula((double) i / (double) waveLength) - waveHeight))));
                }
                relativHeight -= waveHeight;
            }
        }

        //Alle Punkte auf der rechten Seite der Welt werden hinzugefügt
        for (int i = height; i > height - relativHeight; i--) {
            generatedWorld.add(new Point(width, i));
        }

        //Alle Punkte auf der Unterseite der Welt werden hinzugefügt
        for (int i = width; i >= 0; i--) {
            generatedWorld.add(new Point(i, height));
        }

        getGameWorld().add(new Surface(generatedWorld));
        worldChanged=true;
    }

    /**
     * Alle Oberflächen werden durch die übergebene Explosion "zertsört".
     * Es werden also alle Punkte in der Explsion gelöscht und die Randpunkte der Explosion als neue Oberflächenpunkte gespeichert.
     *
     * @param explosion
     */
    public void destroySurface(Explosion explosion) {

        List<Surface> newSurfaces = new LinkedList<>(); //List für alle neuen "schwebenden" Teile

        for (Surface surface : getGameWorld()) {

            List<Point> pointsToRemove = new LinkedList<>();    //Liste der Punkte die von der Explosion zerstört werden
            List<Point> pointsToAdd = new LinkedList<>();       //Liste der Punkte die sich an der Grenze der Explosion befinden
            int initialIndex = -1;      //Index bei dem die Explosion die Oberfläche zum ersten Mal berührt
            Point initialPoint = null;  //Punkt bei dem die Explosion die Oberfläche zum ersten Mal berührt

            /* Alle Punkte der Oberfläche inerhalb der Explosion werden entfernt */
            for (Point point : surface.getBorder()) {
                if (explosion.contains(point)) {

                    /* Sollte der aktulle Punkte der erste Schnittpunkte sein, werden die Initial-Variablen gesetzt */
                    if (initialIndex == -1) {
                        initialIndex = surface.getBorder().indexOf(point);
                        initialPoint = point;
                    }
                    pointsToRemove.add(point);
                }
            }

            /* Bei einer Überschneidung der Punkte, werden alle dafür vorgesehenen Berchenungen durchgeführt */
            if (initialIndex != -1) {

                int indexOfNearestPoint = explosion.getIndexofNearestPoint(initialPoint);   //Index der Explosion der sich am nähesten beim Schnittpunkte befindet
                int maxLength = indexOfNearestPoint + explosion.getBorder().length;         //Maximale Anzahl die von der folgenden Schleife iteriert werden kann

                /* Der indexOfNearestPoint wird auf einen Punkt gebracht der sich in der Oberfläche befindet */
                while (!surface.contains(explosion.getBorder()[indexOfNearestPoint % explosion.getBorder().length]) && indexOfNearestPoint < maxLength) {
                    indexOfNearestPoint++;
                }

                indexOfNearestPoint %= explosion.getBorder().length;
                int index = indexOfNearestPoint;

                /* Alle Punkte der Explosion die sich in der Oberfläche befinden werden ausgehend vom indexOfNearestPoint zu der pointsToAdd Liste hinzugefügt */
                while (surface.contains(explosion.getBorder()[index % explosion.getBorder().length]) && index < explosion.getBorder().length + indexOfNearestPoint) {
                    pointsToAdd.add(explosion.getBorder()[index % explosion.getBorder().length]);
                    index++;
                }

                int newSurfaceInitial = -1; //Sollte eine neue "schwebende" Oberfläche entstehen, gibt dieser Index ihren Anfangspunkte an
                int newSurfaceFinal;        //Endpunkte der neu Enstandenen Oberfläche
                List<Point> newSurfacePoints = new LinkedList<>();

                /* Alle restlichen Punkte der Explosion werden durchiteriert */
                while (index < explosion.getBorder().length + indexOfNearestPoint) {

                    /*
                    * Es wird nach einem Punkte gesucht der als Anfangspunkt einer neuen Oberfläche dient.
                    * Dieser Punkte ist nur vorhanden, falls es eine neue "schwebende" Oberffläche gibt.
                    */
                    if (newSurfaceInitial == -1 && surface.contains(explosion.getBorder()[index % explosion.getBorder().length])) {

                        newSurfaceInitial = surface.getIndexofNearestPoint(explosion.getBorder()[index % explosion.getBorder().length]);

                         /* Der newSurfaceInitial-Punkt wird auf einen Punkt gebracht der sich in der Explosion befindet */
                        while (!explosion.contains(surface.getBorder().get(newSurfaceInitial)) && surface.getBorder().size() > newSurfaceInitial + 1)
                            newSurfaceInitial++;
                    }

                    /* Alle Grenzpunkte der Explosion werden zur Oberfläche hinzugefügt */
                    if (newSurfaceInitial != -1 && surface.contains(explosion.getBorder()[index % explosion.getBorder().length])) {
                        newSurfacePoints.add(explosion.getBorder()[index % explosion.getBorder().length]);
                    }

                    /* Der Punkt wo die neue Oberfläche endet wird gesucht */
                    if (newSurfaceInitial != -1 && !surface.contains(explosion.getBorder()[index % explosion.getBorder().length])) {

                        newSurfaceFinal = surface.getIndexofNearestPoint(explosion.getBorder()[(index - 1) % explosion.getBorder().length]);

                        /* Die neue Oberfläche wird erstellt */
                        if (newSurfaceInitial < newSurfaceFinal) {
                            newSurfaces.add(new Surface(surface.getBorder().subList(newSurfaceInitial, newSurfaceFinal)));
                        } else {
                            newSurfacePoints.addAll(surface.getBorder().subList(newSurfaceFinal, newSurfaceInitial));
                            newSurfaces.add(new Surface(newSurfacePoints));
                        }

                        newSurfaceInitial = -1; //newSurfaceInitial wird auf -1 gesetzt um es zu ermöglichen, dass wieder neue Oberflächen enstehen können
                        newSurfacePoints = new LinkedList<>();
                    }

                    index++;
                }

                /* Die neue Oberfläche der ursrpünglichen Oberfläche wird geformt */
                List<Point> newBorder = new LinkedList<>();
                newBorder.addAll(surface.getBorder());
                newBorder.removeAll(pointsToRemove);
                newBorder.addAll(initialIndex, pointsToAdd);

                /* Alle "schwebenden" Oberflächen werden von der ursprünglichen Oberfläche entfernt */
                for (int i = 0; i < newSurfaces.size(); i++) {
                    newBorder.removeAll(newSurfaces.get(i).getBorder());
                }

                surface.setBorder(newBorder);
            }
        }

        gameWorld.addAll(newSurfaces);
        worldChanged=true;
    }

    /**
     * Formel für ein "Tal"
     *
     * @param x
     * @return
     */
    private double valleyFormula(double x) {
        return (2 * Math.pow(x, 3) - 3 * Math.pow(x, 2) + 1);
    }

    /**
     * Formel für einen "Berg"
     *
     * @param x
     * @return
     */
    private double mountainFormula(double x) {
        return (-2 * Math.pow(x, 3) + 3 * Math.pow(x, 2));
    }

    public synchronized List<Surface> getGameWorld() {
        return gameWorld;
    }

    public boolean containsPoint(Point p) {
        for (Surface surface : getGameWorld()) {
            if (surface.contains(p))
                return true;
        }
        return false;
    }

    public Point getNearestPoint(Point p) {

        Point nearestPoint = null;
        Point currentPoint;

        for (int i = 0;i<getGameWorld().size();i++) {
            Surface surface = getGameWorld().get(i);
            currentPoint = surface.getBorder().get(surface.getIndexofNearestPoint(p));

            if (nearestPoint == null || getDistance(p, nearestPoint) > getDistance(p, currentPoint))
                nearestPoint = currentPoint;
        }
        if (nearestPoint == null)
            return p;
        else
            return new Point(nearestPoint.getxCoord(),nearestPoint.getyCoord());
    }

    private double getDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(Math.abs(point1.getxCoord() - point2.getxCoord()), 2) + Math.pow(Math.abs(point1.getyCoord() - point2.getyCoord()), 2));
    }

    public boolean isWorldChanged() {
        return worldChanged;
    }
    public void setWorldChanged(){
        worldChanged=false;
    }
}
