/*
 * Copyright 2012-2021 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * Please cite the following publication(s):
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.skin.ConnectionSkin;
import eu.mihosoft.vrl.workflow.skin.Skin;
import eu.mihosoft.vrl.workflow.skin.SkinFactory;
import eu.mihosoft.vrl.workflow.skin.VNodeSkin;
import javafx.scene.Parent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Value based skin factory.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class FXValueSkinFactory extends FXSkinFactory {

    private Map<String, Class<? extends FXFlowNodeSkinBase>> valueSkins = new HashMap<>();
    private final Map<String, Class<? extends FXConnectionSkin>> connectionSkins = new HashMap<>();

    private Class<? extends FXFlowNodeSkinBase> defaultNodeSkinClass = FXFlowNodeSkinBase.class;
    private Class<? extends FXConnectionSkin> defaultConnectionSkinClass = DefaultFXConnectionSkin.class;

    public FXValueSkinFactory(Parent parent) {
        super(parent);
    }

    public FXValueSkinFactory(Parent parent, FXSkinFactory factory) {
        super(parent, factory);
    }

    /**
     * Chooses the best matching node skin depending on the value object.
     *
     * @param n node
     * @param flow parent flow
     * @return best matching node skin depending on the value object or the
     * default node skin if no matching node skin exists
     */
    private FXFlowNodeSkin chooseNodeSkin(VNode n, VFlow flow) {

        Object value = n.getValueObject().getValue();

        if (value == null) {
            return createSkinInstanceOfClass(getDefaultNodeSkin(), n, flow);
        }

        Class<?> valueClass = value.getClass();
        Class<? extends FXFlowNodeSkinBase> skinClass = null;

        while (skinClass == null && valueClass != null) {
            skinClass = valueSkins.get(valueClass.getName());
            valueClass = valueClass.getSuperclass();
        }

        if (skinClass == null) {

            skinClass = getDefaultNodeSkin();
        }

        return createSkinInstanceOfClass(skinClass, n, flow);
    }

    /**
     * Creates an instance of the specified skin class.
     *
     * @param skinClass skin class
     * @param n flow node that shall be visualized by the skin
     * @param flow parent flow
     * @return an instance of the specified skin class that represents node
     * {@code n} or {@code null} if the specified class cannot be instantiated
     */
    private FXFlowNodeSkin createSkinInstanceOfClass(
            Class<? extends FXFlowNodeSkinBase> skinClass, VNode n, VFlow flow) {
        try {

            Constructor<?> constructor
                    = skinClass.getConstructor(
                            FXSkinFactory.class, VNode.class, VFlow.class);

            FXFlowNodeSkinBase skin
                    = (FXFlowNodeSkinBase) constructor.newInstance(this, n, flow);

            return skin;

        } catch (NoSuchMethodException 
                | SecurityException 
                | IllegalAccessException
                | IllegalArgumentException 
                | InvocationTargetException
                | InstantiationException ex) {
            Logger.getLogger(FXValueSkinFactory.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        return null;
    }

    private ConnectionSkin chooseConnectionSkin(Connection c, VFlow flow, String type) {

        String connectionType = c.getType();

        Class<? extends FXConnectionSkin> skinClass = connectionSkins.get(connectionType);

        if (skinClass == null) {
            skinClass = DefaultFXConnectionSkin.class;
        }

        return createConnectionSkinOfClass(skinClass, c, flow, type);
    }

    private ConnectionSkin createConnectionSkinOfClass(
            Class<? extends FXConnectionSkin> skinClass,
            Connection c, VFlow flow, String type) {
        try {

            Constructor<?> constructor
                    = skinClass.getConstructor(
                            FXSkinFactory.class, Parent.class,
                            Connection.class, VFlow.class, String.class);

            FXConnectionSkin skin
                    = (FXConnectionSkin) constructor.newInstance(
                            this, getFxParent(), c, flow, type);
            skin.init();

            return skin;

        } catch (NoSuchMethodException 
                | SecurityException 
                | IllegalAccessException 
                | IllegalArgumentException
                | InvocationTargetException 
                | InstantiationException ex) {
            Logger.getLogger(FXValueSkinFactory.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Adds a skin class for the specified value type.
     * @param valueType value type
     * @param skinClass skin class
     */
    public void addSkinClassForValueType(Class<?> valueType,
            Class<? extends FXFlowNodeSkinBase> skinClass) {

        boolean notAvailable = true;

        // check whether correct constructor is available
        try {
            Constructor<?> constructor
                    = FXFlowNodeSkinBase.class.getConstructor(
                            FXSkinFactory.class, VNode.class, VFlow.class);
            notAvailable = false;
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(FXValueSkinFactory.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(FXValueSkinFactory.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        // we cannot accept the specified skin class as it does not provide
        // the required constructor
        if (notAvailable) {
            throw new IllegalArgumentException(
                    "Required constructor missing: ("
                    + FXSkinFactory.class.getSimpleName()
                    + ", " + VNode.class.getSimpleName() + ", "
                    + VNode.class.getSimpleName() + ")");
        }

        valueSkins.put(valueType.getName(), skinClass);
    }

    /**
     * Adds a skin class for the specified connection class.
     * @param connectionType connection type
     * @param skinClass skin class
     */
    public void addSkinClassForConnectionType(String connectionType,
            Class<? extends FXConnectionSkin> skinClass) {

        boolean notAvailable = true;

        // check whether correct constructor is available
        try {

            Constructor<?> constructor
                    = DefaultFXConnectionSkin.class.getConstructor(
                            FXSkinFactory.class, Parent.class,
                            Connection.class, VFlow.class, String.class);
            notAvailable = false;
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(FXValueSkinFactory.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        // we cannot accept the specified skin class as it does not provide
        // the required constructor
        if (notAvailable) {
            throw new IllegalArgumentException(
                    "Required constructor missing: ("
                    + FXSkinFactory.class.getSimpleName()
                    + ", " + Parent.class.getSimpleName() + ", "
                    + VFlow.class.getSimpleName() + ", "
                    + String.class + ")");
        }

        connectionSkins.put(connectionType, skinClass);
    }

    @Override
    public VNodeSkin createSkin(VNode n, VFlow flow) {
        return chooseNodeSkin(n, flow);
    }

    @Override
    public ConnectionSkin createSkin(Connection c, VFlow flow, String type) {
        return chooseConnectionSkin(c, flow, type);
    }

    @Override
    public SkinFactory<FXConnectionSkin, FXFlowNodeSkin> createChild(Skin parent) {

        FXValueSkinFactory result = new FXValueSkinFactory(((FXSkin) parent).
                getContentNode(), this);

        result.valueSkins = valueSkins;
        result.defaultNodeSkinClass = defaultNodeSkinClass;
        result.defaultConnectionSkinClass = defaultConnectionSkinClass;

        return result;
    }

    @Override
    public FXSkinFactory newInstance(Parent parent, FXSkinFactory parentFactory) {

        FXValueSkinFactory result = new FXValueSkinFactory(parent, parentFactory);

        result.valueSkins = valueSkins;
        result.defaultNodeSkinClass = defaultNodeSkinClass;
        result.defaultConnectionSkinClass = defaultConnectionSkinClass;

        return result;
    }

    /**
     * @return the defaultNodeSkin
     */
    public Class<? extends FXFlowNodeSkinBase> getDefaultNodeSkin() {
        return defaultNodeSkinClass;
    }

    /**
     * @param defaultNodeSkin the defaultNodeSkin to set
     */
    public void setDefaultNodeSkin(Class<? extends FXFlowNodeSkinBase> defaultNodeSkin) {

        Objects.requireNonNull(defaultNodeSkin);

        this.defaultNodeSkinClass = defaultNodeSkin;
    }

    /**
     * @return the defaultConnectionSkin
     */
    public Class<? extends FXConnectionSkin> getDefaultConnectionSkin() {
        return defaultConnectionSkinClass;
    }

    /**
     * @param defaultConnectionSkin the defaultConnectionSkin to set
     */
    public void setDefaultConnectionSkin(Class<? extends FXConnectionSkin> defaultConnectionSkin) {

        Objects.requireNonNull(defaultConnectionSkin);

        this.defaultConnectionSkinClass = defaultConnectionSkin;
    }

}
