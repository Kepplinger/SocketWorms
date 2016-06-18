package controller;

import gameobjects.Explosion;
import gameobjects.Point;
import gameobjects.Surface;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import gameobjects.GameWorld;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {


    @FXML
    private Canvas canvas;

    private GraphicsContext gc;
    private GameWorld gameWorld;
    private Timer timer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
