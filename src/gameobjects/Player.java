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
        if (!gameWorld.containsPoint(position)) {
            position.setyCoord(position.getyCoord() + 1);
        }
        if (gameWorld.containsPoint(position)){
            //TODO überarbeiten!!!
            position = gameWorld.getGameWorld().get(0).getBorder().get(gameWorld.getGameWorld().get(0).getIndexofNearestPoint(position));
        }
    }

    public void movePlayer(int value){
        position.setxCoord(position.getxCoord() + value);
    }
}
