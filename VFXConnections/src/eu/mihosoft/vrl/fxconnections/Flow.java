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
public interface Flow {

    public FlowNode newNode(Node w1);
    
    public Connection connect(FlowNode s, FlowNode r);
    
}
