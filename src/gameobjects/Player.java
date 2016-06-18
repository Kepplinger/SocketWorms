package gameobjects;

import java.io.Serializable;

public class Player implements Serializable {
    private String name;
    private Point position;
    private int fallingspeed;
    private int health;

    private Shoot ownShoot;

    public Player(String playername) {
        name = playername;
        fallingspeed = 0;
        health = 100;
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
}
