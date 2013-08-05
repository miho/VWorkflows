/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

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

        for (Node n : getChildrenUnmodifiable()) {
            if (n instanceof FlowNodeWindow) {
                FlowNodeWindow w = (FlowNodeWindow) n;
                w.nodeSkinProperty().get().getModel().setX(Math.max(0, n.getLayoutX()));
                w.nodeSkinProperty().get().getModel().setY(Math.max(0, n.getLayoutY()));
            }
        }

        super.layoutChildren();
    }
}
