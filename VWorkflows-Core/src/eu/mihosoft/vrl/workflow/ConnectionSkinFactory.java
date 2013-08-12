/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public interface ConnectionSkinFactory<T extends Skin> {

    ConnectionSkin createSkin(Connection c, VFlow flow, String type);
   
}
