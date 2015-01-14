/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class IOConnector extends ConnectorImpl{

    public IOConnector(VNode node, String type, String localId, boolean input) {
        super(node, type, localId, input);
    }
    
}
