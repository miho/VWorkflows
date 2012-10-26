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
class ControlFlow implements Flow {

    public ControlFlow() {
    }

    @Override
    public FlowNode newNode(Node w1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Connection connect(FlowNode s, FlowNode r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
