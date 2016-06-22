package client;

import gameobjects.Player;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import server.ServerViewController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Andreas on 24.05.2016.
 */
public class LoginController implements Initializable{
    public TextField tf_serverip;
    public TextField tf_playername;
    public VBox mainPane;
    public Button bt_left;
    public Button bt_right;
    public ImageView iv_skin;

    private int skinID = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        //When you press S you start the Server, left change skin, right change skin
        mainPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                event.consume();
                changeSkin(event);
            }
        });
        //When you click enter on the ip Textfield you start the server
        tf_serverip.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startServer(null);
            }
        });


    }

    public void changeSkin(KeyEvent event) {
        if (event.getCode() == KeyCode.RIGHT) {
            nextSkin(null);
            bt_left.setDefaultButton(true);
        }
        else if (event.getCode() == KeyCode.LEFT) {
            previousSkin(null);
            bt_right.setDefaultButton(true);
        }
        else if (event.isControlDown() && event.getCode() == KeyCode.S) {
            startServer(null);
        }
        else if (event.isControlDown() && event.getCode() == KeyCode.P) {
            iv_skin.setImage(new Image("/images/worms/RPremium.png"));
        }
    }

    public void connect(ActionEvent actionEvent) {
        ClientModel.getInstance().setLocalPlayer(new Player(tf_playername.getText(),skinID));
        ClientModel.getInstance().setServerIP(tf_serverip.getText());
        //ClientModel.getInstance().sendData();

        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("view/Gamefield.fxml"));
            stage.setTitle("WORMS - "+tf_serverip.getText() + " ["+tf_playername.getText()+"]");
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            //stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) actionEvent.getSource()).getScene().getWindow());
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/server/views/ServerView.fxml"));
            root = loader.load();
            ServerViewController controller = loader.getController();
            stage.setTitle("Server - "+controller.getModel().getServerIP());
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void previousSkin(ActionEvent actionEvent) {
        skinID--;
        if(skinID<0){
            skinID = Player.WORM_SKINS-1;
        }
        iv_skin.setImage(new Image(String.format("/images/worms/worm%d.png",skinID)));
    }

    public void nextSkin(ActionEvent actionEvent) {
        skinID++;
        if(skinID>=Player.WORM_SKINS){
           skinID = 0;
        }
        iv_skin.setImage(new Image(String.format("/images/worms/Rworm%d.png", skinID)));
    }
}
