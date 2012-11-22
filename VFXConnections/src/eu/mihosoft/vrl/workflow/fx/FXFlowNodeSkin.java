/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.fxwindows.VFXNodeUtils;
import eu.mihosoft.vrl.fxwindows.Window;
import eu.mihosoft.vrl.workflow.FlowNode;
import eu.mihosoft.vrl.workflow.FlowNodeSkin;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FXFlowNodeSkin
implements FXSkin<FlowNode, Window>, FlowNodeSkin<FlowNode> {
    
    private FlowNode model;
//    private ObjectProperty<V> flowProperty = new SimpleObjectProperty<>();
    private Window node;
    
    public FXFlowNodeSkin(FlowNode model) {
        this.model = model;
//        setFlow(flow);
        init();
    }
    
    private void init() {
        Window w = new Window();
        
        w.titleProperty().bind(model.titleProperty());
        
        w.layoutXProperty().bind(model.xProperty());
        w.layoutYProperty().bind(model.yProperty());
        
        w.layoutXProperty().bind(model.widthProperty());
        w.layoutYProperty().bind(model.heightProperty());
    }

    @Override
    public Window getNode() {
        return node;
    }

//    @Override
//    public final void setFlow(V flow) {
//        flowProperty.set(flow);
//    }
//
//    @Override
//    public final V getFlow() {
//        return flowProperty.get();
//    }
//    
//    @Override
//    public ObjectProperty<V> flowProperty() {
//        return flowProperty;
//    }

    @Override
    public void remove() {
        VFXNodeUtils.removeFromParent(node);
    }
}
