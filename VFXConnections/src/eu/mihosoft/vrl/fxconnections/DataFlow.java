/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import eu.mihosoft.vrl.fxwindows.Window;
import javafx.scene.Node;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class DataFlow<T> implements Flow<T> {

    public DataFlow() {
    }

    @Override
    public FlowNode<T> newNode(T n) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Connection connect(FlowNode<T> s, FlowNode<T> r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    
}
