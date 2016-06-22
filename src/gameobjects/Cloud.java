package gameobjects;

import javafx.scene.image.Image;

import java.util.Random;

/**
 * Created by Andreas on 21.06.2016.
 */
public class Cloud {
    private Point position;
    private boolean moveLeft = false;
    private int speed;
    private Image cloud;


    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void fly() {
        position.setxCoord(position.getxCoord() + (moveLeft ? -speed : speed));
    }

    public Cloud() {
        Random r = new Random();
        if (r.nextInt(2) == 0) {
            moveLeft = true;
            position = new Point(1024, r.nextInt(150) + 10);
        } else {
            position = new Point(0, r.nextInt(150) + 10);
        }
        cloud = new Image(String.format("/images/clouds/cloud%d.png",r.nextInt(4)));
        speed = r.nextInt(4) + 1;
    }

    public boolean atTheEnd() {
        /*if ((moveLeft && position.getxCoord() == 0) || (!moveLeft && position.getxCoord() == 1024)) {
            return true;
        }*/
        return false;
    }
    public Image getCloud(){
        return cloud;
    }

    @Override
    public String toString() {
        return "Cloud{" +
                "position=" + position +
                ", moveLeft=" + moveLeft +
                ", speed=" + speed +
                ", cloud=" + cloud +
                '}';
    }
}
