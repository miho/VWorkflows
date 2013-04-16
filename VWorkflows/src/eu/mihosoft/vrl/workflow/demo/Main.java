/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.demo;

import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.VConnections;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import jfxtras.labs.util.event.MouseControlUtil;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Main extends Application {

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    private MainWindowFXMLController controller;

    @Override
    public void start(Stage primaryStage) {

//        connectionTest();

        StackPane canvas = new StackPane();

        Scene scene = new Scene(canvas, 800, 800);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainWindowFXML.fxml"));

        try {
            fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        controller = fxmlLoader.getController();

        canvas.getChildren().add((Node) fxmlLoader.getRoot());

        primaryStage.setTitle("VWorkflows Demo!");
        primaryStage.setScene(scene);
        primaryStage.show();

        Rectangle rect = new Rectangle();
        rect.setStroke(new Color(1, 1, 1, 1));
        rect.setFill(new Color(0, 0, 0, 0.5));
        MouseControlUtil.
                addSelectionRectangleGesture(controller.getRootPane(), rect);

        Playground.search01();
    }

   
}
