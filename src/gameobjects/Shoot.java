package gameobjects;

import java.io.Serializable;

/**
 * Created by Andreas on 18.06.2016.
 */
public class Shoot implements Serializable{
    private double angle = 10;
    private double currentSpeed = 0.5;

    private boolean fired = false;

    public Shoot(double currentSpeed, double angle, boolean fired) {
        this.currentSpeed = currentSpeed;
        this.angle = angle;
        this.fired = fired;
    }

    public boolean isFired() {
        return fired;
    }

    public double getAngle() {
        return angle;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public void setFired(boolean fired) {
        this.fired = fired;
    }

    @Override
    public String toString() {
        return "Shoot{" +
                "angle=" + angle +
                ", currentSpeed=" + currentSpeed +
                ", fired=" + fired +
                '}';
    }
}
