/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.playground;

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
        
        n.addInput("control"); // returns 0
        n.addOutput("control"); // returns 0
        
        n.addInput("data");  // returns 0
        n.addInput("data");  // returns 1
        n.addInput("data");  // returns 2
        
        n.addOutput("data"); // returns 0
        
        
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
