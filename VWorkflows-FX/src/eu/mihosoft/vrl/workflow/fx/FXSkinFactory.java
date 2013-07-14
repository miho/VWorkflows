/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionSkin;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VNodeSkin;
import eu.mihosoft.vrl.workflow.Skin;
import eu.mihosoft.vrl.workflow.SkinFactory;
import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Parent;
import javafx.scene.paint.Color;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FXSkinFactory implements SkinFactory<FXConnectionSkin, FXFlowNodeSkin> {

    private final Parent fxParent;
    private final FXSkinFactory parentFactory;
//    private Window clipboard;
    

    public FXSkinFactory(Parent parent) {
        this.fxParent = parent;
//        this.clipboard = clipboard;
        this.parentFactory = null;
    }

    protected FXSkinFactory(Parent parent, FXSkinFactory parentFactory) {
        this.fxParent = parent;
//        this.clipboard = clipboard;
        this.parentFactory = parentFactory;
    }

    @Override
    public VNodeSkin createSkin(VNode n, VFlow flow) {
        return new FXFlowNodeSkin(this, getFxParent(), n, flow);
    }

    @Override
    public ConnectionSkin createSkin(Connection c, VFlow flow, String type) {
        return new FXConnectionSkin(this, getFxParent(), c, flow, type/*, clipboard*/);
    }

    @Override
    public SkinFactory<FXConnectionSkin, FXFlowNodeSkin> createChild(Skin parent) {

        FXSkinFactory result = new FXSkinFactory(((FXSkin) parent).getContentNode(), this);

        return result;
    }
    
    public FXSkinFactory newInstance(Parent parent, FXSkinFactory parentFactory) {

        FXSkinFactory result = new FXSkinFactory(((FXSkin) parent).getContentNode(), parentFactory);

        return result;
    }

    @Override
    public SkinFactory<FXConnectionSkin, FXFlowNodeSkin> getParent() {
        return this.parentFactory;
    }

    /**
     * @return the fxParent
     */
    public Parent getFxParent() {
        return fxParent;
    }
    
}
