/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionSkin;
import eu.mihosoft.vrl.workflow.ConnectionSkinFactory;
import eu.mihosoft.vrl.workflow.FlowController;
import eu.mihosoft.vrl.workflow.Skin;
import javafx.scene.Parent;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FXConnectionSkinFactory implements ConnectionSkinFactory<FXSkin>{
    private Parent parent;

    public FXConnectionSkinFactory(Parent parent) {
        this.parent = parent;
    }
    
    @Override
    public ConnectionSkin createSkin(Connection c, FlowController flow, String type) {
        return new FXConnectionSkin(parent, c, flow, type);
    }

    @Override
    public ConnectionSkinFactory createChild(FXSkin parent) {
 
        return new FXConnectionSkinFactory(parent.getContentNode());
    }
}
