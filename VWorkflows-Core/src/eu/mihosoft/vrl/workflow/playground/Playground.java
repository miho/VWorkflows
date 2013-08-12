/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.playground;

import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.io.WorkflowIO;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public class Playground {

    public static void main(String args[]) {

        VFlow flow = FlowFactory.newFlow();

        VNode n = flow.newNode();

        Connector c1 = n.addInput("control"); // returns 
        Connector c2 = n.addOutput("control"); // returns 0

        Connector d1 = n.addInput("data");  // returns 0
        Connector d2 = n.addInput("data");  // returns 1
        Connector d3 = n.addInput("data");  // returns 2

        Connector d4 = n.addOutput("data"); // returns 0

        VNode n2 = flow.newNode();

        Connector c3 = n2.addInput("control"); // returns 
        Connector c4 = n2.addOutput("control"); // returns 

        ConnectionResult result = flow.connect(c2, c3);

//        ConnectionResult result2 = flow.tryConnect(c1, c3);

        if (!result.getStatus().isCompatible()) {
            System.out.println("ERROR:" + result.getStatus().getMessage());
        }

        try {
            WorkflowIO.saveToXML(Paths.get("/Users/miho/tmp/flow01.xml"), flow.getModel());
        } catch (IOException ex) {
            Logger.getLogger(Playground.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            VFlow flowFromFile = WorkflowIO.loadFromXML(Paths.get("/Users/miho/tmp/flow01.xml"));
            System.out.println(flowFromFile.getConnections("control").toString());
        } catch (IOException ex) {
            Logger.getLogger(Playground.class.getName()).log(Level.SEVERE, null, ex);
        }



        //        flow.connect(n,n2,"event").getStatus().getMessage();


        // the node should look like this:
        //
        //              -----------------------------
        //             |            Node           X |
        //             |-----------------------------|
        //             |                             |
        //            /| o (data)                    |\
        // (control) |O| o (data)           (data) o |O| (control)
        //            \| o (data)                    |/
        //             |                             |
        //              -----------------------------
        //



    }
}
