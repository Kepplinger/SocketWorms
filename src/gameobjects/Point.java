package gameobjects;

import java.io.Serializable;

/**
 * Created by Kepplinger on 24.05.2016.
 */
public class Point implements Serializable {

    private int xCoord;
    private int yCoord;

    private boolean changed = false;

    public Point(int xCoord, int yCoord) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
        changed = true;
    }

    public void setyCoord(int yCoord) {
        if(yCoord>576) {
            this.yCoord=600;
        }
        else {
            this.yCoord = yCoord;
            changed = true;
        }
    }

    @Override
    public String toString() {
        return "Point{" +
                "X:" + xCoord +
                ", Y:" + yCoord +
                '}';
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean hasChanged() {
        return changed;
    }
}
