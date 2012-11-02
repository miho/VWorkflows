/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
abstract class ControlFlow<T> implements Flow<T> {
    
    private Connections  connections = VFXConnections.newConnections();

    public ControlFlow() {
    }

    @Override
    public Connection connect(FlowNode<T> s, FlowNode<T> r) {
        connections.
    }
 
}
