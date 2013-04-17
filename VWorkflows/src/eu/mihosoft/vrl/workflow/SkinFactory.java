/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface SkinFactory<T extends Skin, V extends Skin> extends ConnectionSkinFactory<T>, FlowNodeSkinFactory<V> {

    SkinFactory<T, V> createChild(Skin parent);

}
