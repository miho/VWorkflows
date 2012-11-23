/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface WorkFlow {

    public DataFlow getDataFlow();

    public ControlFlow getControlFlow();
    
    public void setNodeSkinFactory(FlowNodeSkinFactory factory);
    public void setConnectionSkinFactory(ConnectionSkinFactory factory);
}
