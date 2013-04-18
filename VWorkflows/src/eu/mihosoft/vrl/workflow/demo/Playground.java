/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.demo;

import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.DefaultWorkflow;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.NodeLookup;
import eu.mihosoft.vrl.workflow.VConnections;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Playground {

    public static void search01() {

        VFlow flow = FlowFactory.newFlow();

        VNode n1 = flow.newNode();
        VNode n2 = flow.newNode();

        VFlow subFlow = flow.newSubFlow();

        VNode sn1 = subFlow.newNode();
        VNode sn2 = subFlow.newNode();
        
        String globalId = sn1.getId();
        
        NodeLookup lookup = flow.getNodeLookup();
        
        System.out.println("lookup: " + lookup.getById(globalId));
        System.out.println("   sn1: " + sn1);

    }
    
     public static void connection01() {

        Connections connections = VConnections.newConnections("default");

        connections.add("1out", "2in");
        connections.add("3out", "4out");
        connections.add("1out", "4out");
        connections.add("1out", "2in");
        connections.add("1out", "2in");
        connections.add("3out", "4out");

        System.out.println("all-with: " + connections.getAllWith("1out"));

        System.out.println("all: " + connections.getAll("1out", "2in"));

        System.out.println("\n");

        VConnections.printConnections(connections);
    }
}
