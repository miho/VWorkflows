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
public class ConnectionImplClassTest {

    @Test
    public void testImplClassWithConformingConstructor() {
        VFlow flow = FlowFactory.newFlow();
        
        flow.getConnections("mytype").setConnectionClass(ConnectionBase.class);
    }

    public void testImplClassWithNonConformingConstructor() {
        VFlow flow = FlowFactory.newFlow();
        
        boolean exceptionThrown = false;
        
        try{
            flow.getConnections("mytype").setConnectionClass(
                ConnectionBaseWithoutConformingConstructor.class);
        } catch(IllegalArgumentException ex) {
            exceptionThrown = true;
        }
        
        Assert.assertTrue("IllegalArgumentException must be thrown!", exceptionThrown);
    }
}

class ConnectionBaseWithoutConformingConstructor extends ConnectionBase {
    //
    private ConnectionBaseWithoutConformingConstructor() {};
}
