/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.FlowNode;
import eu.mihosoft.vrl.workflow.FlowNodeSkin;
import eu.mihosoft.vrl.workflow.FlowNodeSkinFactory;
import javafx.scene.Parent;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FXFlowNodeSkinFactory implements FlowNodeSkinFactory{
    
    private Parent parent;

    public FXFlowNodeSkinFactory(Parent parent) {
        this.parent = parent;
    }
    
    @Override
    public FlowNodeSkin createSkin(FlowNode n) {
        return  new FXFlowNodeSkin(parent, n);
    }
}
