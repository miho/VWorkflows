/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
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
        Connector sender1 = n1.setMainOutput(n1.addOutput("control"));
        VNode n2 = flow.newNode();
        Connector receiver2 = n2.addInput("control");
        n2.setMainInput(receiver2);
        Connector sender2 = n2.addOutput("control");
        n2.setMainOutput(sender2);
        VNode n3 = flow.newNode();
        Connector receiver3 = n3.addInput("control");
        n3.setMainInput(receiver3);

        class CountingListener implements EventHandler<ConnectionEvent> {

            private int counter = 0;
            
            private EventType<? extends Event> lastEventType;

            @Override
            public void handle(ConnectionEvent t) {
                counter++;
                lastEventType = t.getEventType();
            }

            /**
             * @return the counter
             */
            public int getCounter() {
                return counter;
            }

            private void reset() {
                counter = 0;
            }
        }

        // create test for input
        CountingListener countingListenerInput = new CountingListener();
        receiver2.addConnectionEventListener(countingListenerInput);
        ConnectionResult resultInput = flow.connect(n1, n2, "control");
        assertTrue("Connection must be valid", resultInput.getStatus().isCompatible());
        // add event has been fired once
        assertTrue("Add-Event must be fired exactly once!", 
                countingListenerInput.getCounter() == 1 && countingListenerInput.lastEventType == ConnectionEvent.ADD);
        // remove the connection
        resultInput.getConnection().getConnections().remove(resultInput.getConnection());
        // remove event has been fired once
        assertTrue("Remove-Event must be fired exactly once!",
                countingListenerInput.getCounter() == 2 && countingListenerInput.lastEventType == ConnectionEvent.REMOVE);
        
        // create test for output
        CountingListener countingListenerOutput = new CountingListener();
        sender1.addConnectionEventListener(countingListenerOutput);
        ConnectionResult resultOutput = flow.connect(n1, n2, "control");
        assertTrue("Connection must be valid", resultOutput.getStatus().isCompatible());
        // add event has been fired once
        assertTrue("Add-Event must be fired exactly once!", 
                countingListenerOutput.getCounter() == 1 && countingListenerOutput.lastEventType == ConnectionEvent.ADD);
        // remove the connection
        resultOutput.getConnection().getConnections().remove(resultOutput.getConnection());
        // remove event has been fired once
        assertTrue("Remove-Event must be fired exactly once!",
                countingListenerOutput.getCounter() == 2 && countingListenerOutput.lastEventType == ConnectionEvent.REMOVE);
    }

}
