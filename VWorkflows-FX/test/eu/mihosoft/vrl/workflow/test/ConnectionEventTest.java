/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.test;

import eu.mihosoft.vrl.workflow.ConnectionEvent;
import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import javafx.event.EventHandler;
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
public class ConnectionEventTest {

    public ConnectionEventTest() {
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
    public void connectNodes() {
        VFlow flow = FlowFactory.newFlow();

        VNode n1 = flow.newNode();
        n1.setMainOutput(n1.addOutput("control"));
        VNode n2 = flow.newNode();
        Connector receiver2 = n2.addInput("control");
        n2.setMainInput(receiver2);
        Connector sender2 = n2.addOutput("control");
        n2.setMainOutput(sender2);
        VNode n3 = flow.newNode();
        Connector receiver3 = n3.addInput("control");
        n3.setMainInput(receiver3);
        
        class CountingListener implements EventHandler<ConnectionEvent>{
            
            private int counter = 0;

            @Override
            public void handle(ConnectionEvent t) {
                counter++;
            }

            /**
             * @return the counter
             */
            public int getCounter() {
                return counter;
            }
        }
        
        CountingListener countingListener = new CountingListener();
        
        receiver2.addConnectionEventListener(countingListener);
        
//        System.out.println("-> " + n1.getMainOutput("control") + ", " + n2.getMainInput("control"));
        
        ConnectionResult result = flow.connect(n1, n2, "control");
        
//        System.out.println(" --> count" + countingListener.getCounter());
        
        assertTrue("Connection must be valid", result.getStatus().isCompatible());
        // add event has been fired once
        assertTrue("Add-Event must be fired exactly once!", countingListener.getCounter() == 1);
        
        // remove the connection
        result.getConnection().getConnections().remove(result.getConnection());
        // remove event has been fired once
        assertTrue("Remove-Event must be fired exactly once!", countingListener.getCounter() == 2);
    }

}
