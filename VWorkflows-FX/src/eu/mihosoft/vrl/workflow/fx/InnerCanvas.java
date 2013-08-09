/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.VNode;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import jfxtras.labs.scene.control.window.Window;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class InnerCanvas extends Pane {

    public InnerCanvas() {
        
    }

    @Override
    protected void layoutChildren() {
        
        List<VNode> nodeList = new ArrayList<>();
        
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        // search minX and minY of window nodes
        for (Node n : getChildrenUnmodifiable()) {
            if (n instanceof FlowNodeWindow) {
                FlowNodeWindow w = (FlowNodeWindow) n;
                nodeList.add(w.nodeSkinProperty().get().getModel());
                
                minX = Math.min(n.getLayoutX(), minX);
                minY = Math.min(n.getLayoutY(), minY);
            }
        }
        
        // move windows
        for(VNode n : nodeList) {
            n.setX(n.getX()-minX);
            n.setY(n.getY()-minY);
        }

        super.layoutChildren();
    }
}
