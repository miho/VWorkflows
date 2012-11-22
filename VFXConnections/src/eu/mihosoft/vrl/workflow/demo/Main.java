/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.demo;

import eu.mihosoft.vrl.fxwindows.CloseIcon;
import eu.mihosoft.vrl.fxwindows.MinimizeIcon;
import eu.mihosoft.vrl.fxwindows.RotateIcon;
import eu.mihosoft.vrl.fxwindows.ScalableContentPane;
import eu.mihosoft.vrl.fxwindows.Window;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.ControlFlow;
import eu.mihosoft.vrl.workflow.Flow;
import eu.mihosoft.vrl.workflow.FlowNode;
import eu.mihosoft.vrl.workflow.FlowNodeBase;
import eu.mihosoft.vrl.workflow.VConnections;
import eu.mihosoft.vrl.workflow.fx.FXFlowNodeSkin;
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

    @Override
    public void start(Stage primaryStage) {

        connectionTest();

        ScalableContentPane canvas = new ScalableContentPane();

        Pane root = canvas.getContentPane();

        root.setStyle("-fx-background-color: rgb(160, 160, 160);");

        Scene scene = new Scene(canvas, 800, 800);

        workflowTest(root);

        primaryStage.setTitle("VFXConnection Demo!");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    
    public void workflowTest(Pane root) {
        
        Flow flow = new ControlFlow();

        FlowNode n1 = new FlowNodeBase();
        FXFlowNodeSkin n1Skin = new FXFlowNodeSkin(n1);

        FlowNode n2 = new FlowNodeBase();
        FXFlowNodeSkin n2Skin = new FXFlowNodeSkin(n2);

        flow.connect(n1, n2);
        
        root.getChildren().add(n1Skin.getNode());
        root.getChildren().add(n2Skin.getNode());
    }

    public void connectionTest() {

        Connections connections = VConnections.newConnections();

        connections.add("1out", "2in");
        connections.add("3out", "4out");
        connections.add("1out", "4out");
        connections.add("1out", "2in");
        connections.add("1out", "2in");
        connections.add("3out", "4out");

        System.out.println("all-with: " + connections.getAllWith("1out"));

        System.out.println("all: " + connections.getAll("1out", "2in"));

        System.out.println("\n");

        VConnections.printConnections(connections);
    }
}
