/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface ConnectionSkinFactory<T extends Skin> {

    ConnectionSkin createSkin(Connection c, FlowController flow, String type);
    
    ConnectionSkinFactory createChild(T parent);
}
