/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 * A passthru connector. Passthru connectors are used to establish connections
 * between nodes in different hierarchy levels.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface ThruConnector extends Connector{
    
    /**
     * Returns the inner connector node.
     * @return inner connector node
     */
    VNode getInnerNode();
    
    /**
     * Returns the inner connector.
     * @return inner connector
     */
    Connector getInnerConnector();
}
