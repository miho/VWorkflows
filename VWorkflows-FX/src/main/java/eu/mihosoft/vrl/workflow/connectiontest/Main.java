/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.connectiontest;

import eu.mihosoft.vrl.workflow.ConnectionEvent;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
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
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VCanvas canvas = new VCanvas();

        createFlow(canvas);

        Scene scene = new Scene(canvas, 1024, 768);

        primaryStage.setTitle("Connection Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void createFlow(VCanvas canvas) {
        VFlow flow = FlowFactory.newFlow();

        VNode n1 = flow.newNode();
        Connector output = n1.addOutput("data");
        VFlow n2 = flow.newSubFlow();
        Connector input = n2.addInput("data");
        VNode n3 = n2.newNode();
        Connector inputN3 = n3.addInput("data");
        
        input.addConnectionEventListener(new EventHandler<ConnectionEvent>() {

            @Override
            public void handle(ConnectionEvent event) {
                if (event.getEventType() == ConnectionEvent.ADD) {
                    Connector c = n2.addInput("data");
                    c.addConnectionEventListener(this);
                } else if (event.getEventType() == ConnectionEvent.REMOVE) {
                    n2.getModel().getConnectors().remove(event.getReceiverConnector());
                }
            }
        });
        
        flow.setVisible(true);
        flow.setSkinFactories(new FXValueSkinFactory(canvas));
    }

}
