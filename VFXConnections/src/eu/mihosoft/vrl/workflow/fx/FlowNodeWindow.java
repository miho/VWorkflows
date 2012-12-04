/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.fxwindows.Window;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FlowNodeWindow extends Window{
    private ObjectProperty<FXFlowNodeSkin> nodeSkinProperty = new SimpleObjectProperty<>();

    public FlowNodeWindow(FXFlowNodeSkin skin) {
        
        nodeSkinProperty().set(skin);
        
        
    }
 
    /**
     * @return the nodeSkinProperty
     */
    public final ObjectProperty<FXFlowNodeSkin> nodeSkinProperty() {
        return nodeSkinProperty;
    }
    
    
}
