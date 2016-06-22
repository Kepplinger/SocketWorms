package server;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Andreas on 22.06.2016.
 */
public class ServerStatController implements Initializable{

    ServerModel model;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = ServerModel.getInstance();
    }

    public ServerModel getModel() {
        return model;
    }
}
