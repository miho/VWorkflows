/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface FlowFlowNode extends FlowModel, FlowNode {

    public FlowFlowNode newFlowNode(NodeValueObject obj);

    public FlowFlowNode newFlowNode();

    public FlowNode newNode(NodeValueObject obj);

    public FlowNode newNode();
}
