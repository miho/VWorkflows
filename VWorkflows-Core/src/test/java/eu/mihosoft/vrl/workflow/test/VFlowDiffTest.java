/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.test;

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.io.WorkflowIO;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VFlowDiffTest {

    @Test
    public void testSimpleDiff() {
        VFlow flow = createFlow(10, 5, 3, "control", "data", "event");
        
//        VFlow flow = FlowFactory.newFlow();
//        
//        FlowUtil.createFlow(flow, 5, 5);
        
        try {
            WorkflowIO.saveToXML(Paths.get("flow01.xml"), flow.getModel());
        } catch (IOException ex) {
            Logger.getLogger(VFlowDiffTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private VFlow createFlow(int maxWidth, int depth, int maxNumConnectors, String... types) {
        VFlow root = FlowFactory.newFlow();
        List<VFlow> flows = new ArrayList<>();
        flows.add(root);
        for (int d = 0; d < depth; d++) {

            List<VFlow> nextFlows = new ArrayList<>();
            for (VFlow flow : flows) {
                VNode prevNode = null;
                for (int w = 0;
                        w < Math.random() * (maxWidth / flows.size()) + 1;
                        w++) {
                    VNode n;

                    if (Math.random() < 0.5) {
                        VFlow f = flow.newSubFlow();
                        nextFlows.add(f);

                        n = f.getModel();
                    } else {
                        n = flow.newNode();
                    }

                    for (String type : types) {

                        for (int i = 0;
                                i < Math.random() * maxNumConnectors + 1;
                                i++) {
                            n.addInput(type);
                            n.addOutput(type);
                        }
                    }

                    for (String type : types) {

                        if (prevNode != null) {
                            List<Connector> outputs
                                    = prevNode.getOutputs().
                                    filtered(conn -> conn.getType().
                                            equals(type));

                            List<Connector> inputs
                                    = n.getInputs().
                                    filtered(conn -> conn.getType().
                                            equals(type));

                            for (Connector o : outputs) {
                                for (Connector i : inputs) {
                                    if (Math.random() < 0.5) {
                                        flow.connect(o, i);
                                    }
                                }
                            }
                        }

                    }

                    prevNode = n;
                }
            }
            flows = nextFlows;
        }

        return root;
    }

}
