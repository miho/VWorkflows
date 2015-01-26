/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.playground;

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.ThruConnector;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXValueSkinFactory;
import eu.mihosoft.vrl.workflow.fx.VCanvas;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ScrollPaneSketch extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VCanvas canvas = new VCanvas();
        
        canvas.setStyle("-fx-background-color: red;");
        
        canvas.setMinScaleX(0.5);
        canvas.setMinScaleY(0.5);
        
        canvas.setMaxScaleX(1.0);
        canvas.setMaxScaleY(1.0);
        
        ScrollPane scrollPane = new ScrollPane(canvas);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
//        scrollPane.setStyle("-fx-border-color: red;");
        
        Scene scene = new Scene(scrollPane,800,600);
        
        VFlow flow = createFlow();
        flow.addSkinFactories(new FXValueSkinFactory(canvas.getContent()));
        flow.setVisible(true);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("ScrollPane Test");
        primaryStage.show();
    }

    private VFlow createFlow() {
        VFlow flow = FlowFactory.newFlow();

        VNode n1 = flow.newNode();
        Connector n1out = n1.addOutput("data");

        VNode n2 = flow.newNode();
        Connector n2in = n2.addInput("data");

        VFlow subflow = flow.newSubFlow();
        subflow.setVisible(true);

        VNode sn1 = subflow.newNode();
        Connector sn1in = sn1.addInput("data");
        Connector sn1out = sn1.addOutput("data");

        // we want to connect n1 to sn1 and sn1 to n2
        ThruConnector ptIn = subflow.addThruInput("data");
        ThruConnector ptOut = subflow.addThruOutput("data");

        // connects n1 and sn1
        flow.connect(n1out, ptIn);
        subflow.connect(ptIn.getInnerConnector(), sn1in);

        // connects sn1 and n2
        subflow.connect(sn1out, ptOut.getInnerConnector());
        flow.connect(ptOut, n2in);

        return flow;
    }
}
