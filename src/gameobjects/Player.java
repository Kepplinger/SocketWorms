package gameobjects;

import java.io.Serializable;

public class Player implements Serializable {
    private String name;
    private Point position;
    private int fallingspeed;

    public Player(String playername) {
        name = playername;
        fallingspeed = 0;
    }

    public String getName() {
        return name;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void applyPhysics(GameWorld gameWorld) {
        if (getDistance(gameWorld.getNearestPoint(position),position) > 3) {
            position.setyCoord(position.getyCoord() + 5);
        }

        if (gameWorld.containsPoint(position)){
            Point point = gameWorld.getNearestPoint(position);
            point.setyCoord(point.getyCoord() - 2);
            position = point;
        }
    }

    public void movePlayer(int value){
        position.setxCoord(position.getxCoord() + value);
    }

    private double getDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(Math.abs(point1.getxCoord() - point2.getxCoord()), 2) + Math.pow(Math.abs(point1.getyCoord() - point2.getyCoord()), 2));
    }
}
