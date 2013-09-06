/*
 * Main.java
 * 
 * Copyright 2012-2013 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 * 
 * Please cite the following publication(s):
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
 */ 

package eu.mihosoft.vrl.workflow.demo5;

import eu.mihosoft.vrl.workflow.Connector;
import javafx.application.Application;
import javafx.stage.Stage;

import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import eu.mihosoft.vrl.workflow.fx.FXValueSkinFactory;
import eu.mihosoft.vrl.workflow.fx.ScalableContentPane;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import java.util.Random;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
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

        // create a new flow object
        VFlow flow = FlowFactory.newFlow();

        // make it visible
        flow.setVisible(true);

        // create two nodes:
        // one leaf node and one subflow which is returned by createNodes
        createFlow(flow, 3, 6);

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
        
        fXSkinFactory.addSkinClassForValueType(Integer.class, IntegerFlowNodeSkin.class);

        // generate the ui for the flow
        flow.addSkinFactories(fXSkinFactory);

        // the usual application setup
        Scene scene = new Scene(canvas, 1024, 600);
        
        //scene.getStylesheets().setAll("/eu/mihosoft/vrl/workflow/tutorial05/resources/dark.css");
        
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public void createFlow(VFlow workflow, int depth, int width) {

        if (depth < 1) {
            return;
        }

        // connection types
        String[] connectionTypes = {"control", "data", "event"};
        
        Random rand = new Random();

        for (int i = 0; i < width; i++) {

            VNode n;

            // every second node shall be a subflow
            if (i % 2 == 0) {
                // creates a subflow node
                VFlow subFlow = workflow.newSubFlow();
                n = subFlow.getModel();
                // adds content to the subflow
                createFlow(subFlow, depth - 1, width);
            } else {
                // creates a regular node
                n = workflow.newNode();
            }

            // defines the node title
            n.setTitle("Node " + n.getId());
            
            // adding values between 0 and 100
            n.getValueObject().setValue((int)(rand.nextDouble()*100.0));

            for (int k = 0; k < connectionTypes.length; k++) {
                String type = connectionTypes[k % connectionTypes.length];
                for (int j = 0; j < 3; j++) {

                    // adds an input to the node
                    Connector input = n.addInput(type);

                    // adds an output to the node
                    Connector output = n.addOutput(type);

                    // the first input/output of each type shall be defined
                    // as main/default connector that will be connected if
                    // we drop on the node instead of connectors
                    if (j == 0) {
                        n.setMainInput(input);
                        n.setMainOutput(output);
                    }
                }
            }

            // defines how many nodes per row
            int numNodesPerRow = 3;

            // defines the gap between the nodes
            double gap = 30;

            // defines the node dimensions
            n.setWidth(300);
            n.setHeight(200);

            // defines the node location
            n.setX(gap + (i % numNodesPerRow) * (n.getWidth() + gap));
            n.setY(gap + (i / numNodesPerRow) * (n.getHeight() + gap));
        }
    }
}
