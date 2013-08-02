/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public interface SkinFactory<T extends Skin, V extends Skin> extends ConnectionSkinFactory<T>, VNodeSkinFactory<V> {

    SkinFactory<T, V> createChild(Skin parent);
    
    public SkinFactory<T, V> getParent();

}
