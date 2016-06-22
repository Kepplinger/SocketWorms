package server;

import gameobjects.Player;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Andreas on 22.06.2016.
 */
public class ServerStatController implements Initializable {

    public Label lb_pA;
    public Label lb_pB;
    public Label lb_dA;
    public Label lb_dB;
    public ImageView iv_curSkin;
    public Label lb_curPlayer;
    public Label lb_health;
    public Label lb_angle;
    public Label lb_curX;
    public Label lb_curY;
    public ListView lv_tA;
    public ListView lv_tB;

    ServerModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = ServerModel.getInstance();
        final boolean[] end = new boolean[1];
        
        Timer t = new Timer(true);
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lb_pA.setText(String.format("%d", model.getState().getInfo().getPoints_a()));
                        lb_pB.setText(String.format("%d", model.getState().getInfo().getPoints_b()));

                        lb_dA.setText(String.format("%d", model.getState().getInfo().getCurDeaths_A()));
                        lb_dB.setText(String.format("%d", model.getState().getInfo().getCurDeaths_B()));
                        if (end[0] != true && model.getState().getInfo().getCurDeaths_A() >= model.getState().getInfo().getPlayer_A_cnt()) {
                            Alert a = new Alert(Alert.AlertType.INFORMATION, "TEAM B HAS WON", ButtonType.OK);
                            end[0] = true;
                            a.show();
                        }
                        else if (end[0] != true && model.getState().getInfo().getCurDeaths_B() >= model.getState().getInfo().getPlayer_B_cnt()) {
                            Alert a = new Alert(Alert.AlertType.INFORMATION, "TEAM A HAS WON", ButtonType.OK);
                            end[0] = true;
                            a.show();
                        }

                        if (model.getCurrentPlayer() != null) {
                            lb_curPlayer.setText(model.getCurrentPlayer().getName());
                            iv_curSkin.setImage(new Image(String.format("/images/worms/worm%d.png",model.getCurrentPlayer().getWormSkin())));

                            lb_angle.setText(String.format("%d",(int)model.getCurrentPlayer().getShoot().getAngle()));

                            lb_curX.setText(String.format("%d", model.getCurrentPlayer().getPosition().getxCoord()));
                            lb_curY.setText(String.format("%d", model.getCurrentPlayer().getPosition().getyCoord()));
                        }
                        lv_tA.getItems().clear();
                        lv_tB.getItems().clear();
                        for(Player p:model.getPlayers()){
                            if(p.getTeam().equals("A")){
                                lv_tA.getItems().add(p.getName());
                            }
                            else {
                                lv_tB.getItems().add(p.getName());
                            }
                        }
                    }
                });
            }
        }, 200, 1000);
    }

    public ServerModel getModel() {
        return model;
    }
}
