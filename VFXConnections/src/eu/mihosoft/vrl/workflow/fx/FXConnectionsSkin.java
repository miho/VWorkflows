/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.fxwindows.VFXNodeUtils;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.ConnectionsSkin;
import javafx.scene.Node;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FXConnectionsSkin implements ConnectionsSkin, FXSkin<Connections, Node> {
    
    private Connections connections;

    public FXConnectionsSkin(Connections connections) {
        this.connections = connections;
    }
    
    

    @Override
    public Node getNode() {
        return null;
    }
    
    @Override
    public void remove() {
//        VFXNodeUtils.removeFromParent(node);
    }
    
}
