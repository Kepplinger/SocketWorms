package gameobjects;

import java.io.Serializable;

/**
 * Created by Andreas on 20.06.2016.
 */
public class GameInfo implements Serializable{
    public static final int MAX_POINTS = 10;

    //Team A
    private int player_A_cnt = 0;
    private int points_a = 0;
    private int curDeaths_A = 0;
    //Team B
    private int player_B_cnt = 0;
    private int points_b = 0;
    private int curDeaths_B = 0;

    public int getPlayer_A_cnt() {
        return player_A_cnt;
    }

    public int getPoints_a() {
        return points_a;
    }

    public int getPoints_b() {
        return points_b;
    }

    public int getPlayer_B_cnt() {
        return player_B_cnt;
    }

    public int getCurDeaths_A() {
        return curDeaths_A;
    }

    public int getCurDeaths_B() {
        return curDeaths_B;
    }

    public GameInfo(int player_A_cnt, int points_a, int curDeaths_A, int player_B_cnt, int points_b, int curDeaths_B) {
        this.player_A_cnt = player_A_cnt;
        this.points_a = points_a;
        this.curDeaths_A = curDeaths_A;
        this.player_B_cnt = player_B_cnt;
        this.points_b = points_b;
        this.curDeaths_B = curDeaths_B;
    }
    public GameInfo(){}

    public void setCurDeaths_A(int curDeaths_A) {
        this.curDeaths_A = curDeaths_A;
    }

    public void setCurDeaths_B(int curDeaths_B) {
        this.curDeaths_B = curDeaths_B;
    }

    public void setPlayer_A_cnt(int player_A_cnt) {
        this.player_A_cnt = player_A_cnt;
    }

    public void setPlayer_B_cnt(int player_B_cnt) {
        this.player_B_cnt = player_B_cnt;
    }

    public void setPoints_a(int points_a) {
        this.points_a = points_a;
    }

    public void setPoints_b(int points_b) {
        this.points_b = points_b;
    }
}
