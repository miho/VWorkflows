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
