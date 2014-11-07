/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.test;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.io.WorkflowIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class VFlowIOTest {

    public VFlowIOTest() {
    }

    @BeforeClass
    public static void setUpClass() {
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
    public void saveAndLoadFlowFile() {

        VFlow flow1 = FlowFactory.newFlow();

        FlowUtil.createFlow(flow1, 5, 5);

        boolean couldSave = true;
        try {
            WorkflowIO.saveToXML(Paths.get("test-flow-01.xml"), flow1.getModel());
        } catch (IOException ex) {
            Logger.getLogger(VFlowIOTest.class.getName()).log(Level.SEVERE, null, ex);
            couldSave = false;
        }

        assertTrue("saveToXML() must not throw an exception", couldSave);

        boolean couldLoad = true;
        VFlow flow2 = null;
        try {
            flow2 = WorkflowIO.loadFromXML(Paths.get("test-flow-01.xml"));
        } catch (IOException ex) {
            Logger.getLogger(VFlowIOTest.class.getName()).log(Level.SEVERE, null, ex);
            couldLoad = false;
        }

        assertTrue("loadFromXML() must not throw an exception", couldLoad);
        assertNotNull("loadFromXML() must not return null", flow2);

        boolean couldSave2 = true;
        try {
            WorkflowIO.saveToXML(Paths.get("test-flow-02.xml"), flow2.getModel());
        } catch (IOException ex) {
            Logger.getLogger(VFlowIOTest.class.getName()).log(Level.SEVERE, null, ex);
            couldSave2 = false;
        }

        assertTrue("saveToXML() must not throw an exception", couldSave2);

        // compare both flows (samples)
        compare(flow1, flow2);
    }

    @Test
    public void saveAndLoadFlowString() {

        VFlow flow1 = FlowFactory.newFlow();

        FlowUtil.createFlow(flow1, 1, 5);

        String xml = "";
        boolean couldSave = true;
        try {
            xml = WorkflowIO.saveToXML(flow1.getModel());
        } catch (Exception ex) {
            Logger.getLogger(VFlowIOTest.class.getName()).log(Level.SEVERE, null, ex);
            couldSave = false;
        }

        assertTrue("saveToXML() must not throw an exception", couldSave);

        boolean couldLoad = true;
        VFlow flow2 = null;
        try {
            flow2 = WorkflowIO.loadFromXML(xml);
        } catch (Exception ex) {
            Logger.getLogger(VFlowIOTest.class.getName()).log(Level.SEVERE, null, ex);
            couldLoad = false;
        }

        assertTrue("loadFromXML() must not throw an exception", couldLoad);
        assertNotNull("loadFromXML() must not return null", flow2);

        boolean couldSave2 = true;
        try {
            xml = WorkflowIO.saveToXML(flow2.getModel());
        } catch (Exception ex) {
            Logger.getLogger(VFlowIOTest.class.getName()).log(Level.SEVERE, null, ex);
            couldSave2 = false;
        }

        assertTrue("saveToXML() must not throw an exception", couldSave2);

        // compare both flows (samples)
        compare(flow1, flow2);
    }
    
    @Test
    public void saveAndLoadFlowStream() {

        VFlow flow1 = FlowFactory.newFlow();

        FlowUtil.createFlow(flow1, 1, 5);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        boolean couldSave = true;
        try {
            WorkflowIO.saveToXML(flow1.getModel(), os);
        } catch (Exception ex) {
            Logger.getLogger(VFlowIOTest.class.getName()).log(Level.SEVERE, null, ex);
            couldSave = false;
        }

        assertTrue("saveToXML() must not throw an exception", couldSave);

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        
        boolean couldLoad = true;
        VFlow flow2 = null;
        try {
            flow2 = WorkflowIO.loadFromXML(is); 
        } catch (Exception ex) {
            Logger.getLogger(VFlowIOTest.class.getName()).log(Level.SEVERE, null, ex);
            couldLoad = false;
        }

        assertTrue("loadFromXML() must not throw an exception", couldLoad);
        assertNotNull("loadFromXML() must not return null", flow2);

        ByteArrayOutputStream os2 = new ByteArrayOutputStream();
        boolean couldSave2 = true;
        try {
            WorkflowIO.saveToXML(flow2.getModel(), os2);
        } catch (Exception ex) {
            Logger.getLogger(VFlowIOTest.class.getName()).log(Level.SEVERE, null, ex);
            couldSave2 = false;
        }

        assertTrue("saveToXML() must not throw an exception", couldSave2);

        // compare both flows (samples)
        compare(flow1, flow2);
    }

    private void compare(VFlow flow1, VFlow flow2) {

        assertEquals("Both flows must have equal id",
                flow1.getModel().getId(), flow2.getModel().getId());

        assertEquals("Both flows must have an equal number of subflows",
                flow1.getSubControllers().size(), flow2.getSubControllers().size());

        assertEquals("Both flows must have an equal number of nodes",
                flow1.getNodes().size(), flow2.getNodes().size());

        // Flows may have empty connection objects
        // (however, number of connections must be the same, see below)
//        assertEquals("Both flows must have an equal number of connection types,",
//                flow1.getAllConnections().values().size(), flow2.getAllConnections().values().size());
        for (Connections connections1 : flow1.getAllConnections().values()) {

            Connections connections2 = flow2.getConnections(connections1.getType());

            assertNotNull("Second flows must have a connections object of type "
                    + connections1.getType(),
                    connections2);

            assertEquals("Both flows must have an equal number of connections,",
                    connections1.getConnections().size(),
                    connections2.getConnections().size());

            for (int i = 0; i < connections1.getConnections().size(); i++) {
                Connection c1 = connections1.getConnections().get(i);
                Connection c2 = connections2.getConnections().get(i);

                assertEquals("Connection of flow2 must be equal to connection with same id in flow 2", c1, c2);
            }

        }

        for (VNode n1 : flow1.getNodes()) {

            VNode n2 = flow2.getNodeLookup().getById(n1.getId());

            assertNotNull("Second flow must contain a node with same id as in flow1: "
                    + n1.getId(), n2);

            assertEquals("Second node must contain the same number of connectors as node 1",
                    n1.getConnectors().size(), n2.getConnectors().size());

            for (int i = 0; i < n1.getConnectors().size(); i++) {

                assertEquals("Connectors must have equal id: ",
                        n1.getConnectors().get(i).getId(),
                        n2.getConnectors().get(i).getId());
            }

            // TODO deep compare for valueobjects etc.
        }

        for (VFlow subFlow1 : flow1.getSubControllers()) {

            VFlow subFlow2 = null;

            for (VFlow sf : flow2.getSubControllers()) {
                if (sf.getModel().getId().equals(subFlow1.getModel().getId())) {
                    subFlow2 = sf;
                    break;
                }
            }

            assertNotNull("Second flow must contain a subflow with same id as in flow1: "
                    + subFlow1.getModel().getId(), subFlow2);

            compare(subFlow1, subFlow2);
        }
    }
}
