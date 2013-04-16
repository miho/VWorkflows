/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface FlowNodeSkinFactory<T extends Skin> {

    FlowNodeSkin createSkin(FlowNode n);

//    FlowNodeSkinFactory createChild(T parent);

    public void setNodeSkinLookup(FlowNodeSkinLookup skinLookup);
    
    public FlowNodeSkinLookup getNodeSkinLookup();
    
}
