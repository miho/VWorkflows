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
package eu.mihosoft.vrl.workflow.playground;

import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.DefaultValueObject;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.io.WorkflowIO;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public class Playground {

    public static void main(String args[]) {

        VFlow flow = FlowFactory.newFlow();

        VNode n = flow.newNode();

        Connector c1 = n.addInput("control"); // returns 
        
        Connector c2 = n.addOutput("control"); // returns 0
        DefaultValueObject defaultValueObject = new DefaultValueObject();
        defaultValueObject.setValue(new String("Testing"));

        Connector d1 = n.addInput("data");  // returns 0
        Connector d2 = n.addInput("data");  // returns 1
        Connector d3 = n.addInput("data");  // returns 2

        Connector d4 = n.addOutput("data"); // returns 0

        VNode n2 = flow.newNode();

        Connector c3 = n2.addInput("control"); // returns 
        Connector c4 = n2.addOutput("control"); // returns 

        ConnectionResult result = flow.connect(c2, c3);
        

//        ConnectionResult result2 = flow.tryConnect(c1, c3);

        if (!result.getStatus().isCompatible()) {
            System.out.println("ERROR:" + result.getStatus().getMessage());
        }

        try {
            WorkflowIO.saveToXML(Paths.get("flow01.xml"), flow.getModel());
        } catch (IOException ex) {
            Logger.getLogger(Playground.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            VFlow flowFromFile = WorkflowIO.loadFromXML(Paths.get("flow01.xml"));
            System.out.println(flowFromFile.getConnections("control").toString());
        } catch (IOException ex) {
            Logger.getLogger(Playground.class.getName()).log(Level.SEVERE, null, ex);
        }



        //        flow.connect(n,n2,"event").getStatus().getMessage();


        // the node should look like this:
        //
        //              -----------------------------
        //             |            Node           X |
        //             |-----------------------------|
        //             |                             |
        //            /| o (data)                    |\
        // (control) |O| o (data)           (data) o |O| (control)
        //            \| o (data)                    |/
        //             |                             |
        //              -----------------------------
        //



    }
}
