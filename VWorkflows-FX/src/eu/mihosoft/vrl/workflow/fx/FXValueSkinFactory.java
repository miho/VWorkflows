/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionSkin;
import eu.mihosoft.vrl.workflow.Skin;
import eu.mihosoft.vrl.workflow.SkinFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VNodeSkin;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Parent;

/**
 * Value based skin factory.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FXValueSkinFactory extends FXSkinFactory {

    private Map<String, Class<? extends FlowNodeSkinBase>> valueSkins = new HashMap<>();

    public FXValueSkinFactory(Parent parent) {
        super(parent);

        init();
    }

    public FXValueSkinFactory(Parent parent, FXSkinFactory factory) {
        super(parent, factory);

        init();
    }

    private void init() {
        valueSkins.put(Object.class.getName(), FlowNodeSkinBase.class);
    }

    private FXFlowNodeSkin chooseNodeSkin(VNode n, VFlow flow) {
        
        Object value = n.getValueObject().getValue();

        if (value == null) {
            return FlowNodeSkinBase.newInstance(this, n, flow);
        }

        Class<?> cls = value.getClass();
        Class<? extends FlowNodeSkinBase> valueClass = valueSkins.get(cls.getName());

        while (valueClass == null) {
            cls = cls.getSuperclass();
            valueClass = valueSkins.get(cls.getName());
        }

        try {
            return (FXFlowNodeSkin) valueClass.getMethod(
                    "newInstance", VNode.class, VFlow.class).
                    invoke(valueClass, n, flow);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(FXValueSkinFactory.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        
        return FlowNodeSkinBase.newInstance(this, n, flow);
    }

    @Override
    public VNodeSkin createSkin(VNode n, VFlow flow) {
        return chooseNodeSkin(n, flow);
    }

    @Override
    public ConnectionSkin createSkin(Connection c, VFlow flow, String type) {
        return new FXConnectionSkin(this, getFxParent(), c, flow, type/*, clipboard*/);
    }

    @Override
    public SkinFactory<FXConnectionSkin, FXFlowNodeSkin> createChild(Skin parent) {

        FXSkinFactory result = new FXValueSkinFactory(((FXSkin) parent).getContentNode(), this);

        return result;
    }

    @Override
    public FXSkinFactory newInstance(Parent parent, FXSkinFactory parentFactory) {

        FXSkinFactory result = new FXValueSkinFactory(parent, parentFactory);

        return result;
    }
}
