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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import eu.mihosoft.vrl.workflow.io.WorkflowIO;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
