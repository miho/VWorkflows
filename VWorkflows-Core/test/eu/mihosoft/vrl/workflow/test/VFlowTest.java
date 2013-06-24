/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.test;

import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VFlowTest {

    public VFlowTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        //
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void initFlow() {
        VFlow flow = FlowFactory.newFlow();

        assertNotNull("Flow model missing!", flow.getModel());
        assertEquals("Flow id not 'ROOT'", flow.getModel().getId(), "ROOT");

    }

    @Test
    public void createNodes() {
        VFlow flow = FlowFactory.newFlow();

        for (int i = 0; i < 100; i++) {
            VNode n1 = flow.newNode();
        }

        // check that all nodes and their ids are unique
        for (int i = 0; i < flow.getNodes().size(); i++) {
            for (int j = 0; j < flow.getNodes().size(); j++) {

                if (i == j) {
                    continue;
                }

                assertNotEquals("flow.newNode() must not return equal nodes!",
                        flow.getNodes().get(i), flow.getNodes().get(j));
                assertNotEquals("nodes must not have equal ids!",
                        flow.getNodes().get(i).getId(), flow.getNodes().get(j).getId());
            }
        }
    }

    @Test
    public void connectNodes() {
        VFlow flow = FlowFactory.newFlow();

        String[] connectionTypes = {"control", "data", "event", "inheritance"};

        for (int i = 0; i < 1000; i++) {
            VNode n = flow.newNode();

            String type = connectionTypes[i % connectionTypes.length];

            n.addInput(type);
            n.addOutput(type);
        }

        List<VNode> prevNodes = new ArrayList<>();

        for (int i = 0; i < flow.getNodes().size(); i++) {

            VNode receiverNode = flow.getNodes().get(i);

            String type = connectionTypes[i % connectionTypes.length];

            if (prevNodes.size() >= connectionTypes.length) {

//                System.out.print(" >> prevNodes: ");
//                for (VNode vNode : prevNodes) {
//                    System.out.print(" " + vNode.getOutputs().get(0).getType());
//                }
//                System.out.println("");

                for (int j = 0; j < prevNodes.size(); j++) {

                    ConnectionResult[] results = new ConnectionResult[connectionTypes.length];

                    for (int k = 0; k < connectionTypes.length; k++) {

                        VNode senderNode = prevNodes.get(k);
                        Connector output = senderNode.getOutputs().get(0);
                        Connector inputSender = senderNode.getOutputs().get(0);
                        Connector input = receiverNode.getInputs().get(0);
                        Connector outputReceiver = receiverNode.getOutputs().get(0);

                        results[j] = flow.connect(output, input);

                        if (k == 0) {
                            assertTrue("Connection between compatible types failed: "
                                    + output.getType() + " -> " + input.getType(),
                                    results[j].getStatus().isCompatible());
                        } else {
                            assertFalse("Connection between incompatible types must fail: "
                                    + output.getType() + " -> " + input.getType(),
                                    results[j].getStatus().isCompatible());
                        }


                        assertFalse("Connection between two outputs must fail",
                                flow.connect(inputSender, outputReceiver).getStatus().isCompatible());

                        assertFalse("Connection between two inputs must fail",
                                flow.connect(output, outputReceiver).getStatus().isCompatible());

                    } // end for k
                } // end for j

                prevNodes.remove(0);
            }

            prevNodes.add(receiverNode);

        } // end for i

    }
}