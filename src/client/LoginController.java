package client;

import gameobjects.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.setStyle("-fx-background-image: url('/images/titlemenu.png')");
    }

    public void connect(ActionEvent actionEvent) {
        ClientModel.getInstance().setLocalPlayer(new Player(tf_playername.getText()));
        ClientModel.getInstance().setServerIP(tf_serverip.getText());

        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("view/Gamefield.fxml"));
            stage.setTitle("WORMS - "+tf_serverip.getText());
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.initModality(Modality.WINDOW_MODAL);
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
}
