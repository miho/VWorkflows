/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface DataObject {
    public FlowNode getParent();
    public Object getValue();
    public void setValue();
    public CompatibilityResult compatible(DataObject other);
}
