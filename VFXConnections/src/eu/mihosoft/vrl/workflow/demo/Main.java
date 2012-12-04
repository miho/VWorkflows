/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.demo;

import eu.mihosoft.vrl.fxwindows.ScalableContentPane;
import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.ConnectionSkin;
import eu.mihosoft.vrl.workflow.ConnectionSkinFactory;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.ControlFlow;
import eu.mihosoft.vrl.workflow.DefaultWorkflow;
import eu.mihosoft.vrl.workflow.Flow;
import eu.mihosoft.vrl.workflow.FlowNode;
import eu.mihosoft.vrl.workflow.FlowNodeSkin;
import eu.mihosoft.vrl.workflow.FlowNodeSkinFactory;
import eu.mihosoft.vrl.workflow.VConnections;
import eu.mihosoft.vrl.workflow.WorkFlow;
import eu.mihosoft.vrl.workflow.fx.FXConnectionSkin;
import eu.mihosoft.vrl.workflow.fx.FXConnectionSkinFactory;
import eu.mihosoft.vrl.workflow.fx.FXFlowNodeSkin;
import eu.mihosoft.vrl.workflow.fx.FXFlowNodeSkinFactory;
import javafx.application.Application;
import javafx.scene.Parent;
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

        Flow workflow = new DefaultWorkflow(
                new FXFlowNodeSkinFactory(root),
                new FXConnectionSkinFactory(root));

        workflowTest(workflow);

        primaryStage.setTitle("VFXConnection Demo!");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void workflowTest(Flow workflow) {

        FlowNode n1 = workflow.newNode();
        FlowNode n2 = workflow.newNode();
        FlowNode n3 = workflow.newNode();
        FlowNode n4 = workflow.newNode();
        FlowNode n5 = workflow.newNode();

        workflow.connect(n1, n2, "control");
        workflow.connect(n2, n3, "control");
        
        workflow.connect(n3, n4, "control");
        workflow.connect(n4, n5, "control");

        n1.setTitle("MyTitle 1");
        n1.setWidth(300);
        n1.setHeight(200);

        n2.setTitle("MyTitle 2");
        n2.setWidth(300);
        n2.setHeight(200);

        n3.setTitle("MyTitle 3");
        n3.setWidth(300);
        n3.setHeight(200);

        n4.setTitle("MyTitle 4");
        n4.setWidth(300);
        n4.setHeight(200);
        
        n5.setTitle("MyTitle 5");
        n5.setWidth(300);
        n5.setHeight(200);
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
