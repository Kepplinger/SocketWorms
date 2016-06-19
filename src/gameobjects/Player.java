package gameobjects;

import javafx.application.Application;
import org.omg.CORBA.portable.ApplicationException;

import java.io.Serializable;
import java.util.IllegalFormatException;
import java.util.Random;

public class Player implements Serializable {
    public static final int WORM_SKINS = 5;

    private String name;
    private Point position;
    private int fallingspeed;
    private int health;

    private Shoot ownShoot;
    private boolean isCurrent;
    private String team;

    private int wormSkin = 0;

    public Player(String playername) {
        name = playername;
        fallingspeed = 0;
        health = 100;
        setWormSkin(new Random().nextInt(WORM_SKINS));
    }
    public Player(String playername,int skin) {
        name = playername;
        fallingspeed = 0;
        health = 100;
        setWormSkin(skin);
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
        if(position.getyCoord()>576)
            removeHealth(100);

        if (!isDead() && gameWorld.containsPoint(position)){
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

    public int getHealth() {
        return health;
    }

    public void removeHealth(int removedHealth) {
        this.health = health-removedHealth;
    }

    public boolean isDead(){
        return health<=0;
    }

    public Shoot getShoot() {
        if(ownShoot==null){
            ownShoot = new Shoot(0.5,50,false);
        }
        return ownShoot;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player && ((Player)obj).getName().equals(this.getName());
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", position=" + position +
                ", fallingspeed=" + fallingspeed +
                ", health=" + health +
                ", ownShoot=" + ownShoot +
                '}';
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeam() {
        return team;
    }

    public int getWormSkin() {
        return wormSkin;
    }

    public void setWormSkin(int wormSkin) {
        if(wormSkin >= WORM_SKINS)
            throw new IllegalArgumentException(String.format("Es gibt nur [%d] Skins. Erhaltener Wert: %d",WORM_SKINS,wormSkin));
        if(wormSkin < 0)
            throw new IllegalArgumentException(String.format("Negativer Wert. Erhaltener Wert: %d",wormSkin));
        this.wormSkin = wormSkin;
    }
}
