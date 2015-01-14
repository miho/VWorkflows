/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.playground;

import eu.mihosoft.vrl.workflow.ConnectionEvent;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.fx.FXValueSkinFactory;
import eu.mihosoft.vrl.workflow.fx.VCanvas;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ConnectionListenerSketch extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VCanvas canvas = new VCanvas();

        Scene scene = new Scene(canvas, 800, 600);

        VFlow flow = createFlow();
        flow.addSkinFactories(new FXValueSkinFactory(canvas.getContentPane()));
        flow.setVisible(true);

        primaryStage.setScene(scene);
        primaryStage.setTitle("ThruTest");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private VFlow createFlow() {
        VFlow result = FlowFactory.newFlow();

        Connector output = result.newNode().addOutput("data");
        Connector input = result.newNode().addInput("data");

        input.addConnectionEventListener((ConnectionEvent event) -> {
            System.out.println("input-event: " + event.getEventType());
        });

        output.addConnectionEventListener((ConnectionEvent event) -> {
            System.out.println("output-event: " + event.getEventType());
        });

        return result;
    }

}
