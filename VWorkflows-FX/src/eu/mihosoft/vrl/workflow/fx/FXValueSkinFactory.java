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
import java.lang.reflect.Constructor;
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

    private Map<String, Class<? extends FXFlowNodeSkinBase>> valueSkins = new HashMap<>();

    public FXValueSkinFactory(Parent parent) {
        super(parent);

        init();
    }

    public FXValueSkinFactory(Parent parent, FXSkinFactory factory) {
        super(parent, factory);

        init();
    }

    private void init() {
        valueSkins.put(Object.class.getName(), FXFlowNodeSkinBase.class);
    }

    private FXFlowNodeSkin chooseNodeSkin(VNode n, VFlow flow) {

        Object value = n.getValueObject().getValue();

        if (value == null) {
            return new FXFlowNodeSkinBase(this, n, flow);
        }

        Class<?> valueClass = value.getClass();
        Class<? extends FXFlowNodeSkinBase> skinClass = valueSkins.get(valueClass.getName());

        while (skinClass == null) {
            valueClass = valueClass.getSuperclass();
            skinClass = valueSkins.get(valueClass.getName());
        }

        try {

            Constructor<?> constructor =
                    skinClass.getConstructor(
                    FXSkinFactory.class, VNode.class, VFlow.class);

            FXFlowNodeSkinBase skin = 
                    (FXFlowNodeSkinBase) constructor.newInstance(this, n, flow);
            
            return skin;

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
            Logger.getLogger(FXValueSkinFactory.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        return new FXFlowNodeSkinBase(this, n, flow);
    }

    public void addSkinClassForValueType(Class<?> valueType,
            Class<? extends FXFlowNodeSkinBase> skinClass) {

        boolean notAvailable = true;

        // check whether correct constructor is available
        try {
            Constructor<?> constructor =
                    FXFlowNodeSkinBase.class.getConstructor(
                    FXSkinFactory.class, VNode.class, VFlow.class);
            notAvailable = false;
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(FXValueSkinFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(FXValueSkinFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        // we cannot accept the specified skin class as it does not provide
        // the required constructor
        if (notAvailable) {
            throw new IllegalArgumentException(
                    "Required constructor missing: ("
                    + FXSkinFactory.class.getSimpleName()
                    + ", " + VNode.class.getSimpleName() + ", " 
                    + FXSkinFactory.class.getSimpleName() + ")");
        }
        
        valueSkins.put(valueType.getName(), skinClass);
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

        FXValueSkinFactory result = new FXValueSkinFactory(((FXSkin) parent).getContentNode(), this);
        
        result.valueSkins = valueSkins;

        return result;
    }

    @Override
    public FXSkinFactory newInstance(Parent parent, FXSkinFactory parentFactory) {

        FXValueSkinFactory result = new FXValueSkinFactory(parent, parentFactory);
        
        result.valueSkins = valueSkins;

        return result;
    }
}
