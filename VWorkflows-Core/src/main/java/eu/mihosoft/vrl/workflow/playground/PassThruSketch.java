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

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class PassThruSketch {
    public static void main(String[] args) {
        VFlow flow = FlowFactory.newFlow();
        
        VNode n1 = flow.newNode();
        Connector n1out = n1.addOutput("data");
        
        VNode n2 = flow.newNode();
        Connector n2in = n1.addInput("data");
        
        VFlow subflow = flow.newSubFlow();
        
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
    }
}
