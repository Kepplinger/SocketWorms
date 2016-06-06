package gameobjects;

import java.io.Serializable;

public class Player implements Serializable{
    private String name;
    private Point position;

    public Player(String playername){
        name = playername;
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
}
