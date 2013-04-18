/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface NodeSkinFactory<T extends Skin> {

    FlowNodeSkin createSkin(VNode n, VFlow controller);

    
}
