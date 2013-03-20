/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.demo;

import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.DefaultWorkflow;
import eu.mihosoft.vrl.workflow.FlowController;
import eu.mihosoft.vrl.workflow.FlowFlowNode;
import eu.mihosoft.vrl.workflow.FlowFlowNodeImpl;
import eu.mihosoft.vrl.workflow.FlowModel;
import eu.mihosoft.vrl.workflow.FlowModelImpl;
import eu.mihosoft.vrl.workflow.FlowNode;
import eu.mihosoft.vrl.workflow.VConnections;
import eu.mihosoft.vrl.workflow.fx.FXConnectionSkinFactory;
import eu.mihosoft.vrl.workflow.fx.FXFlowNodeSkinFactory;
//import eu.mihosoft.vrl.workflow.fx.ScalableContentPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import jfxtras.labs.scene.layout.ScalableContentPane;
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

    @Override
    public void start(Stage primaryStage) {

//        connectionTest();

        ScalableContentPane canvas = new ScalableContentPane();

//        Pane root = canvas.getContentPane();

        Pane root = new Pane();

        canvas.setContentPane(root);

        root.setStyle("-fx-background-color: linear-gradient(to bottom, rgb(10,32,60), rgb(42,52,120));");

        Scene scene = new Scene(canvas, 800, 800);

        FlowController workflow = new DefaultWorkflow(
                new FXFlowNodeSkinFactory(root),
                new FXConnectionSkinFactory(root));


        workflowTest(workflow, 5);

        primaryStage.setTitle("VFXConnection Demo!");
        primaryStage.setScene(scene);
        primaryStage.show();

        Rectangle rect = new Rectangle();
        rect.setStroke(new Color(1, 1, 1, 1));
        rect.setFill(new Color(0, 0, 0, 0.5));

        MouseControlUtil.
                addSelectionRectangleGesture(root, rect);

    }

    public void workflowTest(FlowController workflow, int depth) {

        if (depth < 1) {
            return;
        }

        for (int i = 0; i < 10; i++) {

            FlowNode n;

            if (i % 2 == 0) {
                FlowController subFlow = workflow.newSubFlow();
                n = subFlow.getModel();
                workflowTest(subFlow, depth - 1);

            } else {
                n = workflow.newNode();

            }

            n.setTitle("Node " + i);
            n.setWidth(300);
            n.setHeight(200);

            n.setX((i % 5) * (n.getWidth() + 30));
            n.setY((i / 5) * (n.getHeight() + 30));

        }

//        FlowController subFlow = workflow.newSubFlow();
//        
//        FlowNode subFlowNode = subFlow.getModel();
//        subFlowNode.setTitle("SubFlow");
//        subFlowNode.setWidth(300);
//        subFlowNode.setHeight(200);
//
//        FlowNode prevN = null;
//        
//        for (int i = 0; i < 10; i++) {
//            FlowNode n = subFlow.newNode();
//            n.setTitle("SubNode " + i);
//            n.setWidth(300);
//            n.setHeight(200);
//
//            n.setX((i % 5) * (n.getWidth() + 30));
//            n.setY((i / 5) * (n.getHeight() + 30));
//            
//            if (prevN !=null) {
//                subFlow.connect(n, prevN, "control");
//            }
//            
//            prevN = n;
//        }
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
