/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.vworkflow.samples;

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import eu.mihosoft.vrl.workflow.fx.FXValueSkinFactory;
import eu.mihosoft.vrl.workflow.fx.ScalableContentPane;
import java.util.Random;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ConnectorAutoLayoutSample extends Application {

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

        // create a new flow object
        VFlow flow = FlowFactory.newFlow();

        // make it visible
        flow.setVisible(true);

        // create two nodes:
        // one leaf node and one subflow which is returned by createNodes
        createFlow(flow);

        // show the main stage/window
        showFlow(flow, primaryStage, "VWorkflows Tutorial 05: View 1");

    }

    private void showFlow(VFlow flow, Stage stage, String title) {

        // create scalable root pane
        ScalableContentPane canvas = new ScalableContentPane();

        canvas.getStyleClass().setAll("vflow-background");

        // define background style
//        canvas.setStyle("-fx-background-color: linear-gradient(to bottom, rgb(10,32,60), rgb(42,52,120));");
        // create skin factory for flow visualization
        FXValueSkinFactory fXSkinFactory = new FXValueSkinFactory(canvas.getContentPane());

        // generate the ui for the flow
        flow.addSkinFactories(fXSkinFactory);

        // the usual application setup
        Scene scene = new Scene(canvas, 1024, 600);

        //scene.getStylesheets().setAll("/eu/mihosoft/vrl/workflow/tutorial05/resources/dark.css");
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public void createFlow(VFlow flow) {

        createNodes(flow, 3);
        
        createNodes(flow.newSubFlow(), 3);

    }

    private void createNodes(VFlow flow, int numberOfNodes) {
        for (int i = 0; i < numberOfNodes; i++) {
            VNode n1 = flow.newNode();
            addConnectors(n1, "control", true);
            addConnectors(n1, "data", false);
        }
    }

    private void addConnectors(VNode n, String type, boolean autoLayout) {
        n.setMainInput(n.addInput(type)).getVisualizationRequest().
                set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, autoLayout);
        n.setMainOutput(n.addOutput(type)).getVisualizationRequest().
                set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, autoLayout);
    }
}
