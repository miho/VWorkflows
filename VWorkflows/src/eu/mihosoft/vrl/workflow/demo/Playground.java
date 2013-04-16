/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.demo;

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.DefaultWorkflow;
import eu.mihosoft.vrl.workflow.FlowController;
import eu.mihosoft.vrl.workflow.FlowNode;
import eu.mihosoft.vrl.workflow.NodeLookup;
import eu.mihosoft.vrl.workflow.VConnections;

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
        
        NodeLookup lookup = flow.getNodeLookup();
        
        System.out.println("lookup: " + lookup.getById(globalId));
        System.out.println("   sn1: " + sn1);
        
        
                
        Connector input1 = n1.newInput("event");
        
        Connector input2 = n1.newInput("myId", "control"); // throws exception if id exists
        
        Connector output1= n1.newOutput("control");
        
        Connector output2= n1.newOutput("myId" ,"control");
        
        

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
