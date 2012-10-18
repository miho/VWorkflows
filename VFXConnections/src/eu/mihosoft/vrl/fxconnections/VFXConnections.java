/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import eu.mihosoft.vrl.fxwindows.CloseIcon;
import eu.mihosoft.vrl.fxwindows.MinimizeIcon;
import eu.mihosoft.vrl.fxwindows.RotateIcon;
import eu.mihosoft.vrl.fxwindows.ScalableContentPane;
import eu.mihosoft.vrl.fxwindows.Window;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VFXConnections extends Application {

    @Override
    public void start(Stage primaryStage) {

        ScalableContentPane canvas = new ScalableContentPane();

        Pane root = canvas.getContentPane();

        root.setStyle("-fx-background-color: rgb(160, 160, 160);");

        Scene scene = new Scene(canvas, 800, 800);


        Window w = WindowUtil.createWindow("Window 1");

        w.setPrefSize(200, 200);

        Window w2 = WindowUtil.createWindow("Window 1");

        w2.setPrefSize(200, 200);

        w2.setLayoutX(1200);
        w2.setLayoutY(1200);

        root.getChildren().add(w);
        root.getChildren().add(w2);


        primaryStage.setTitle("VFXConnection Demo!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

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
}
