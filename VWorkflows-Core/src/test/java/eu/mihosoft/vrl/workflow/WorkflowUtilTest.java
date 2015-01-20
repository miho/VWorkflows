/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.io.WorkflowIO;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class WorkflowUtilTest {

    @Test
    public void getAncestorsTest() {
        createAncestorsTestCase(1, 1, 1, 0);
        createAncestorsTestCase(1, 3, 1, 2);
        createAncestorsTestCase(10, 10, 8, 3);
        createAncestorsTestCase(10, 10, 7, 4);
        createAncestorsTestCase(10, 10, 10, 9);
        createAncestorsTestCase(100, 50, 100, 27);
    }

    public void createAncestorsTestCase(int depth, int width, int destDepth, int destWidth) {

        if (destDepth < 0 || destDepth > depth) {
            throw new IllegalArgumentException("Illegal destDepth specified: "
                    + destDepth + ". Must be between 1 and " + depth + ".");
        }

        if (destWidth < 0 || destWidth >= width) {
            throw new IllegalArgumentException("Illegal destDepth specified: "
                    + destDepth + ". Must be between 0 and " + (width - 1) + ".");
        }

        VFlow flow = FlowFactory.newFlow();
        VNode n = null;
        for (int d = 0; d < depth; d++) {
            for (int w = 0; w < width; w++) {
                if (d == destDepth - 1 && w == destWidth) {
                    n = flow.newNode();
                } else {
                    flow.newNode();
                }
            }

            flow = flow.newSubFlow();
        }

        int numAncestors = WorkflowUtil.getAncestors(n).size();

        Assert.assertTrue("Number of Ancestors must be equal to dest depth. "
                + "Expected " + destDepth + ", got " + numAncestors,
                numAncestors == destDepth);

    }

    @Test
    public void getCommonAncestorsTest() {
        createCommonAncestorTestCase(3, 3, "ROOT:0", "ROOT:1", "ROOT");
        createCommonAncestorTestCase(3, 3, "ROOT:2", "ROOT:1", "ROOT");
        createCommonAncestorTestCase(3, 3, "ROOT:2:0", "ROOT:2:1", "ROOT:2");
        createCommonAncestorTestCase(3, 3, "ROOT:1:0", "ROOT:1:2", "ROOT:1");

        createCommonAncestorTestCase(5, 3, "ROOT:0:0:0", "ROOT:0:2", "ROOT:0");
        createCommonAncestorTestCase(5, 3, "ROOT:0:1", "ROOT:1:2:1", "ROOT");

        createCommonAncestorTestCase(5, 3, "ROOT", "ROOT:1", null);
        createCommonAncestorTestCase(5, 3, "ROOT:2:1", "ROOT", null);
    }

    public void createCommonAncestorTestCase(int depth, int width, String n1Id, String n2Id, String commonAncestorId) {

        VFlow flow = FlowFactory.newFlow();

        List<VFlow> flows = new ArrayList<>();
        flows.add(flow);

        for (int d = 0; d < depth; d++) {
            List<VFlow> newFlows = new ArrayList<>();
            for (int w = 0; w < width; w++) {

                for (VFlow f : flows) {
                    VFlow nF = f.newSubFlow();
                    newFlows.add(nF);
                }
            }

            flows = newFlows;
        }

        try {
            WorkflowIO.saveToXML(Paths.get("testflow.xml"), flow.getModel());
        } catch (IOException ex) {
            Logger.getLogger(WorkflowUtilTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        VNode n1 = flow.getNodeLookup().getById(n1Id);
        VNode n2 = flow.getNodeLookup().getById(n2Id);

        Assert.assertNotNull(n1);
        Assert.assertNotNull(n2);

        Optional<VFlowModel> ancestorResult = WorkflowUtil.getCommonAncestor(n1, n2);

        if (commonAncestorId == null) {
            Assert.assertTrue("Ancestor must not exist.", !ancestorResult.isPresent());
        } else {
            Assert.assertTrue("Ancestor must exist.", ancestorResult.isPresent());

            VFlowModel ancestor = WorkflowUtil.getCommonAncestor(n1, n2).get();

            Assert.assertTrue("Wrong common ancestor. "
                    + "Expected " + commonAncestorId + ", got " + ancestor.getId(),
                    commonAncestorId.equals(ancestor.getId())
            );

        }
    }
    
    @Test
    public void pathInLayerTest() {
        createPathInLayerTest(3, 6);
        createPathInLayerTest(1, 1);
        createPathInLayerTest(32, 32);
        createPathInLayerTest(2, 100);
        

    }
    
    private void createPathInLayerTest(int pathLength, int numNodes) {
        
        if (numNodes == 0 || pathLength == 0) {
            return;
        }
        
        if (numNodes < pathLength) {
            throw new IllegalArgumentException(
                    "path length must be less than number of nodes!");
        }
        
        VFlow flow = FlowFactory.newFlow();
        
        for(int i = 0; i < numNodes;i++) {
            VNode n = flow.newNode();
            n.setMainOutput(n.addOutput("mytype"));
            n.setMainInput(n.addInput("mytype"));
        }
        
        for(int i = 0; i < pathLength-1;i++) {
            VNode sender = flow.getNodes().get(i);
            VNode receiver = flow.getNodes().get(i+1);
            flow.connect(sender, receiver, "mytype");
        }
        
        List<VNode> path= WorkflowUtil.getPathInLayerFromRoot(
                flow.getNodes().get(0), "mytype");
        
        Assert.assertTrue("Expected number of nodes in path = "+pathLength
                +", got " + path.size(), path.size()==pathLength);
    }
}
