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

    private final Parent parent;
    private final FXSkinFactory parentFactory;
//    private Window clipboard;
    
    private ObservableMap<String,Color> connectionFillColors = FXCollections.observableHashMap();
    private ObservableMap<String,Color> connectionStrokeColors = FXCollections.observableHashMap();

    public FXSkinFactory(Parent parent) {
        this.parent = parent;
//        this.clipboard = clipboard;
        this.parentFactory = null;
    }

    private FXSkinFactory(Parent parent, FXSkinFactory parentFactory) {
        this.parent = parent;
//        this.clipboard = clipboard;
        this.parentFactory = parentFactory;
    }

    @Override
    public VNodeSkin createSkin(VNode n, VFlow flow) {
        return new FXFlowNodeSkin(this, parent, n, flow);
    }

    @Override
    public ConnectionSkin createSkin(Connection c, VFlow flow, String type) {
        return new FXConnectionSkin(this, parent, c, flow, type/*, clipboard*/);
    }

    @Override
    public SkinFactory<FXConnectionSkin, FXFlowNodeSkin> createChild(Skin parent) {

        FXSkinFactory result = new FXSkinFactory(((FXSkin) parent).getContentNode(), this);

        return result;
    }

    @Override
    public SkinFactory<FXConnectionSkin, FXFlowNodeSkin> getParent() {
        return this.parentFactory;
    }
    
    public void setConnectionFillColor(String type, Color color) {
        connectionFillColors.put(type, color);
    }
    
    public Color getConnectionFillColor(String type) {
        return connectionFillColors.get(type);
    }
    
    ObservableMap<String, Color> connectionFillColorTypes() {
        return connectionFillColors;
    }
    
    public Collection<String> getConnectionFillColorTypes() {
        return connectionFillColors.keySet();
    }
    
    public void setConnectionStrokeColor(String type, Color color) {
        connectionStrokeColors.put(type, color);
    }
    
    public Color getConnectionStrokeColor(String type) {
        return connectionStrokeColors.get(type);
    }
    
    ObservableMap<String, Color> connectionStrokeColorTypes() {
        return connectionStrokeColors;
    }
    
    public Collection<String> getConnectionStrokeColorTypes() {
        return connectionStrokeColors.keySet();
    }
}
