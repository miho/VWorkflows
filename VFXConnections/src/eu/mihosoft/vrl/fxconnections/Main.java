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
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

//        ScalableContentPane canvas = new ScalableContentPane();
//
//        Pane root = canvas.getContentPane();
//
//        root.setStyle("-fx-background-color: rgb(160, 160, 160);");
//
//        Scene scene = new Scene(canvas, 800, 800);
//
////        for (int i = 0; i < 3; i++) {
////
////            Window w = WindowUtil.createWindow("Window 1");
////            StackPane content = new StackPane();
////            content.setPadding(new Insets(2));
////
////            WebView view = new WebView();
////            view.getEngine().load("http://www.google.com");
////            content.getChildren().add(view);
////            w.setContentPane(content);
////            w.setPrefSize(300, 200);
////            root.getChildren().add(w);
////        }
//        Window w = WindowUtil.createWindow("Window 1");
//        w.setPrefSize(300, 200);
//        root.getChildren().add(w);
//        
//        Connector output = new Connector();
//        
//        output.setStyle("-fx-background-color: rgb(0,0,0);");
//        
//        output.setPrefSize(30, 30);
//
//        w.getContentPane().getChildren().add(output);
//
//        Window w2 = WindowUtil.createWindow("Window 2");
//
//        w2.setPrefSize(200, 200);
//
//        w2.setLayoutX(1200);
//        w2.setLayoutY(1200);
//
//        root.getChildren().add(w2);
//
//        primaryStage.setTitle("VFXConnection Demo!");
//        primaryStage.setScene(scene);
//        primaryStage.show();
        
        
        Connections connections = VFXConnections.newConnections();
        
        connections.add("1out","2in");
        connections.add("3out","4out");
        connections.add("1out","4out");
        
        System.out.println("all-with: " + connections.getAllWith("1out"));
        
        System.out.println("\n");
        
        VFXConnections.printConnections(connections);
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
