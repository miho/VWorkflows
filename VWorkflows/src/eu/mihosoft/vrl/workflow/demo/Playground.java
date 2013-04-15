/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.demo;

import eu.mihosoft.vrl.workflow.DefaultWorkflow;
import eu.mihosoft.vrl.workflow.FlowController;
import eu.mihosoft.vrl.workflow.FlowNode;
import eu.mihosoft.vrl.workflow.NodeLookup;
import eu.mihosoft.vrl.workflow.NodeLookupImpl;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Playground {

    public static void search01() {

        FlowController flow = new DefaultWorkflow();

        FlowNode n1 = flow.newNode();
        FlowNode n2 = flow.newNode();

        FlowController subFlow = flow.newSubFlow();

        FlowNode sn1 = subFlow.newNode();
        FlowNode sn2 = subFlow.newNode();
        
        String globalId = sn1.getId();
        
        NodeLookup lookup = flow.newNodeLookup();
        
        System.out.println("lookup: " + lookup.getNodeById(globalId));
        System.out.println("   sn1: " + sn1);

    }
}
