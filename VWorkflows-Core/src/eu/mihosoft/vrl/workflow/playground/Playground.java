/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.playground;

import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
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

        ConnectionResult result = flow.connect(c2, c4);
        
//        ConnectionResult result2 = flow.tryConnect(c1, c3);
        
        if (!result.getStatus().isCompatible()) {
            System.out.println("ERROR:" + result.getStatus().getMessage());
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
