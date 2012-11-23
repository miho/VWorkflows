/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.demo;

import eu.mihosoft.vrl.fxwindows.ScalableContentPane;
import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.ControlFlow;
import eu.mihosoft.vrl.workflow.Flow;
import eu.mihosoft.vrl.workflow.FlowNode;
import eu.mihosoft.vrl.workflow.VConnections;
import eu.mihosoft.vrl.workflow.fx.FXConnectionSkin;
import eu.mihosoft.vrl.workflow.fx.FXFlowNodeSkin;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
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

        ControlFlow flow = new ControlFlow();

        FlowNode n1 = flow.newNode();
        FlowNode n2 = flow.newNode();

        Connection c1 = flow.connect(n1, n2).getConnection();

        FXFlowNodeSkin n1Skin = new FXFlowNodeSkin(root, n1);
        FXFlowNodeSkin n2Skin = new FXFlowNodeSkin(root, n2);
        FXConnectionSkin c1Skin = new FXConnectionSkin(root, c1, flow);

//        n1.setSkin(n1Skin);
//        n2.setSkin(n2Skin);
//        c1.setSkin(c1Skin);

        n1Skin.add();
        n2Skin.add();
        c1Skin.add();

        n1.setTitle("MyTitle 1");
        n1.setWidth(300);
        n1.setHeight(200);

        n2.setTitle("MyTitle 2");
        n2.setWidth(300);
        n2.setHeight(200);
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
