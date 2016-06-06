package gameobjects;

/**
 * Created by Kepplinger on 24.05.2016.
 */
public class Point {

    private int xCoord;
    private int yCoord;

    public Point(int xCoord, int yCoord){
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

}
