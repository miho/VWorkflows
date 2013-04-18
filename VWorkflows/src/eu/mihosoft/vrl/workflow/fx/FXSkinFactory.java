/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionSkin;
import eu.mihosoft.vrl.workflow.FlowController;
import eu.mihosoft.vrl.workflow.FlowNode;
import eu.mihosoft.vrl.workflow.FlowNodeSkin;
import eu.mihosoft.vrl.workflow.Skin;
import eu.mihosoft.vrl.workflow.SkinFactory;
import javafx.scene.Parent;
import jfxtras.labs.scene.control.window.Window;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FXSkinFactory implements SkinFactory<FXConnectionSkin, FXFlowNodeSkin> {

    private final Parent parent;
    private Window clipboard;

    public FXSkinFactory(Parent parent, Window clipboard) {
        this.parent = parent;
        this.clipboard = clipboard;
    }

    @Override
    public FlowNodeSkin createSkin(FlowNode n, FlowController flow) {
        return new FXFlowNodeSkin(parent, n, flow);
    }

    @Override
    public ConnectionSkin createSkin(Connection c, FlowController flow, String type) {
        return new FXConnectionSkin(parent, c, flow, type, clipboard);
    }

    @Override
    public SkinFactory<FXConnectionSkin, FXFlowNodeSkin> createChild(Skin parent) {

        FXSkinFactory result = new FXSkinFactory(((FXSkin) parent).getContentNode(), clipboard);

        return result;
    }

}
