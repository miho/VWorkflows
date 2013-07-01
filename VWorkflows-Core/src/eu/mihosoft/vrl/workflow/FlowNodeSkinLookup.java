/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.List;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface FlowNodeSkinLookup extends Lookup<List<VNodeSkin>> {

    public VNodeSkin getById(SkinFactory skinFactory, String globalId);
    public ConnectionSkin<?> getById(SkinFactory skinFactory, Connection c);
}
