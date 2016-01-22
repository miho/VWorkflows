/*
 * Copyright 2012-2016 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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
