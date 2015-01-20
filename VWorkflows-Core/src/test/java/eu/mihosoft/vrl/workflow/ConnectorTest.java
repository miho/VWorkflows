/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ConnectorTest {

    @Test
    public void maxNumberOfConnectionsTest() {
        VFlow flow = FlowFactory.newFlow();
        
        VNode sender = flow.newNode();
        VNode receiver = flow.newNode();
        
        Connector senderOut = sender.addOutput("mytype");
        Connector receiverIn = receiver.addInput("mytype");
        
        int maxConn = 1;
        
        senderOut.setMaxNumberOfConnections(maxConn);
        receiverIn.setMaxNumberOfConnections(maxConn);
        
        int senderMax = senderOut.getMaxNumberOfConnections();
        
        Assert.assertTrue("Sender: expected max number of connections = "
                + maxConn + ", got " + senderMax, maxConn == senderMax);
        
        int receiverMax = receiverIn.getMaxNumberOfConnections();
        
        Assert.assertTrue("Receiver: expected max number of connections = "
                + maxConn + ", got " + receiverMax, maxConn == receiverMax);
        
        ConnectionResult result = flow.connect(senderOut, receiverIn);
        
        Assert.assertTrue("Connection must be established. Connection-Msg: "
                + result.getStatus().getMessage(),
                result.getStatus().isCompatible());
        
        ConnectionResult result2 = flow.connect(senderOut, receiverIn);
        
        Assert.assertTrue("Connection must not be established as the max "
                + "number of connections is already reached.",
                !result2.getStatus().isCompatible());
        
        
    }
}
