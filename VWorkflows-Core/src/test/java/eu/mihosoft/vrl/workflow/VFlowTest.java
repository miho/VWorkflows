/*
 * Copyright 2012-2021 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
                        Connector inputSender = senderNode.getInputs().get(0);
                        Connector outputSender = senderNode.getOutputs().get(0);
                        Connector inputReceiver = receiverNode.getInputs().get(0);
                        Connector outputReceiver = receiverNode.getOutputs().get(0);

                        results[j] = flow.connect(outputSender, inputReceiver);
                        
                        if (k == 0) {
                            assertTrue("Connection between compatible types failed: "
                                    + outputSender.getType() + " -> " + inputReceiver.getType(),
                                    results[j].getStatus().isCompatible());
                        } else {
                            assertFalse("Connection between incompatible types must fail: "
                                    + outputSender.getType() + " -> " + inputReceiver.getType(),
                                    results[j].getStatus().isCompatible());
                        }

                        assertFalse("Connection between two outputs must fail",
                                flow.connect(outputSender, outputReceiver).getStatus().isCompatible());

                        assertFalse("Connection between two inputs must fail",
                                flow.connect(inputSender, inputReceiver).getStatus().isCompatible());

                    } // end for k
                } // end for j

                prevNodes.remove(0);
            }

            prevNodes.add(receiverNode);

        } // end for i
    }

    @Test
    public void createSubflows() {
        VFlow flow = FlowFactory.newFlow();

        for (int i = 0; i < 10; i++) {
            flow.newNode();
        }

        VFlow subFlow = flow.newSubFlow();

        VNode subNode = flow.getNodeLookup().getById(subFlow.getModel().getId());

        System.out.println(" --> subnode class: " + subNode.getClass());

        assertTrue("Node that represent flows must implement VFlowModel interface",
                (subNode instanceof VFlowModel));
    }

    @Test
    public void connectorsParentNode() {
        VFlow flow = FlowFactory.newFlow();

        for (int i = 0; i < 10; i++) {
            flow.newNode();
        }

        VFlow subFlow = flow.newSubFlow();

        VNode subNode = subFlow.getModel();

        Connector connector = subNode.addInput("control");

        System.out.println(" --> subnode class (as referenced by connector): " + connector.getNode().getClass());

        assertTrue("Node that represent flows must implement VFlowModel interface",
                (connector.getNode() instanceof VFlowModel));
    }
}
