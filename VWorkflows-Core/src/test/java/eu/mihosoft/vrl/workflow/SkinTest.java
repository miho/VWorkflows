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
package eu.mihosoft.vrl.workflow;

import eu.mihosoft.vrl.workflow.skin.ConnectionSkin;
import eu.mihosoft.vrl.workflow.skin.Skin;
import eu.mihosoft.vrl.workflow.skin.SkinFactory;
import eu.mihosoft.vrl.workflow.skin.VNodeSkin;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class SkinTest {

    @Test
    public void nodeSkinTest() {
        VFlow flow = FlowFactory.newFlow();
        VNodeSkinFactoryStub skinFactory = new VNodeSkinFactoryStub();
        flow.setSkinFactories(skinFactory);

        flow.setVisible(true);
        VNode n1 = flow.newNode();

        Assert.assertTrue("Skin for n1 must be present.",
                skinFactory.getNodeSkins().get(n1) != null);

        Assert.assertTrue("Skin must be skin of n1.",
                skinFactory.getNodeSkins().get(n1).getModel() == n1);

        VFlow sf1 = flow.newSubFlow();

        Assert.assertTrue("Skin for sf1 must be present.",
                skinFactory.getNodeSkins().get(sf1.getModel()) != null);

        Optional<VNodeSkinFactoryStub> sfSkinFactry
                = skinFactory.getChildFactories().values().stream().findFirst();

        Assert.assertTrue("Child factory must exist.", sfSkinFactry.isPresent());

        VNode sn1 = sf1.newNode();

        sf1.setVisible(true);
        Assert.assertTrue("Skin for sn1 must be present.",
                sfSkinFactry.get().getNodeSkins().get(sn1) != null);

        Assert.assertTrue("Skin must be skin of n1.",
                sfSkinFactry.get().getNodeSkins().get(sn1).getModel() == sn1);

    }

    @Test
    public void nodeSkinLookupTest() {
        VFlow flow = FlowFactory.newFlow();
        VNodeSkinFactoryStub skinFactory = new VNodeSkinFactoryStub();
        flow.setSkinFactories(skinFactory);

        flow.setVisible(true);
        VNode n1 = flow.newNode();

        VNodeSkin lookupN1 = flow.getNodeSkinLookup().
                getById(skinFactory, n1.getId());

        Assert.assertTrue("Skin for n1 must be present.",
                lookupN1 != null);

        Assert.assertTrue("Skin must be skin of n1.",
                lookupN1.getModel() == n1);
    }

    @Test
    public void nodeSkinLookupRemovalTest() {
        VFlow flow = FlowFactory.newFlow();
        VNodeSkinFactoryStub skinFactory = new VNodeSkinFactoryStub();
        flow.setSkinFactories(skinFactory);

        flow.setVisible(true);
        VNode n1 = flow.newNode();
        VNode n2 = flow.newNode();

        VNodeSkin lookupN1 = flow.getNodeSkinLookup().
                getById(skinFactory, n1.getId());

        VNodeSkin lookupN2 = flow.getNodeSkinLookup().
                getById(skinFactory, n2.getId());

        Assert.assertTrue("Skin for n1 must be present.",
                lookupN1 != null);

        Assert.assertTrue("Skin must be skin of n1.",
                lookupN1.getModel() == n1);

        Assert.assertTrue("Skin for n2 must be present.",
                lookupN2 != null);

        Assert.assertTrue("Skin must be skin of n2.",
                lookupN2.getModel() == n2);

        flow.remove(n2);

        VNodeSkin lookupN2AfterRemoval = flow.getNodeSkinLookup().
                getById(skinFactory, n2.getId());

        Assert.assertTrue("Skin for n2 must be removed after node removal.",
                lookupN2AfterRemoval == null);

    }

    @Test
    public void nodeSkinRemoveSubFlowTest() {
        VFlow flow = FlowFactory.newFlow();
        VNodeSkinFactoryStub skinFactory = new VNodeSkinFactoryStub();
        flow.setSkinFactories(skinFactory);

        flow.setVisible(true);
        VFlow sf = flow.newSubFlow();
        sf.setVisible(true);

        VNodeSkin lookupN1 = flow.getNodeSkinLookup().
                getById(skinFactory, sf.getModel().getId());

        Assert.assertTrue("Skin for sf must be present.",
                lookupN1 != null);

        Assert.assertTrue("Skin must be skin of n1.",
                lookupN1.getModel() == sf.getModel());

        VNode n1 = sf.newNode();
        VFlow sf1 = sf.newSubFlow();

        boolean n1IsChildNode = sf.getNodes().stream().filter(n -> n == n1).
                findFirst().isPresent();

        boolean sf1IsChildNode = sf.getNodes().stream().filter(n -> sf1.getModel() == n).
                findFirst().isPresent();

        Assert.assertTrue("n1 must be child of sf",
                n1IsChildNode);

        Assert.assertTrue("sf1 must be child of sf",
                sf1IsChildNode);
        
//        flow.remove(sf.getModel());
//
//        VNodeSkinFactoryStub skinFactoryOfSF = (VNodeSkinFactoryStub)
//                sf.getSkinFactories().iterator().next();
//        
//        skinFactoryOfSF.getNodeSkins().values().forEach(ns -> {
//            System.out.println("ns: " + ns.getModel().getId());
//        });
    }

    @Test
    public void connectionSkinLookupTest() {
        VFlow flow = FlowFactory.newFlow();
        VNodeSkinFactoryStub skinFactory = new VNodeSkinFactoryStub();
        flow.setSkinFactories(skinFactory);

        flow.setVisible(true);
        VNode n1 = flow.newNode();
        Connector s = n1.addOutput("mytype");
        VNode n2 = flow.newNode();
        Connector r = n2.addInput("mytype");

        Connection connection = flow.connect(s, r).getConnection();

        ConnectionSkin lookupC1 = flow.getNodeSkinLookup().
                getById(skinFactory, connection);

        Assert.assertTrue("Skin for connection must be present.",
                lookupC1 != null);

        Assert.assertTrue("Skin must be skin of connection.",
                lookupC1.getModel() == connection);
    }

    @Test
    public void connectionSkinRemovalLookupTest() {
        VFlow flow = FlowFactory.newFlow();
        VNodeSkinFactoryStub skinFactory = new VNodeSkinFactoryStub();
        flow.setSkinFactories(skinFactory);

        flow.setVisible(true);
        VNode n1 = flow.newNode();
        Connector s1 = n1.addOutput("mytype");
        VNode n2 = flow.newNode();
        Connector r1 = n2.addInput("mytype");
        Connector r2 = n2.addInput("mytype");

        Connection connection1 = flow.connect(s1, r1).getConnection();
        Connection connection2 = flow.connect(s1, r2).getConnection();

        ConnectionSkin lookupC1 = flow.getNodeSkinLookup().
                getById(skinFactory, connection1);

        Assert.assertTrue("Skin for connection must be present.",
                lookupC1 != null);

        Assert.assertTrue("Skin must be skin of connection.",
                lookupC1.getModel() == connection1);

        ConnectionSkinStub lookupC2 = (ConnectionSkinStub) flow.getNodeSkinLookup().
                getById(skinFactory, connection2);

        Assert.assertTrue("Skin for connection must be present.",
                lookupC2 != null);

        Assert.assertTrue("Skin must be skin of connection.",
                lookupC2.getModel() == connection2);

        flow.getConnections("mytype").remove(connection2);

        Assert.assertTrue("Skin has to be removed after connection removal.",
                lookupC2.isRemoved());

        ConnectionSkinStub lookupC2AfterRemoval = (ConnectionSkinStub) flow.getNodeSkinLookup().
                getById(skinFactory, connection2);

        Assert.assertTrue("Skin has to be removed after connection removal.",
                lookupC2AfterRemoval == null);

    }
}

final class VNodeSkinStub implements VNodeSkin<VNode> {

    private final ObjectProperty<VNode> modelProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<VFlow> flowProperty = new SimpleObjectProperty<>();
    private final SkinFactory skinFactory;

    private boolean removed;

    public VNodeSkinStub(SkinFactory skinFactory, VNode n, VFlow flow) {
        this.skinFactory = skinFactory;
        setController(flow);
        setModel(n);
    }

    @Override
    public void add() {
        //
    }

    @Override
    public void remove() {
        removed = true;
    }

    @Override
    public void setModel(VNode model) {
        modelProperty().set(model);
    }

    @Override
    public VNode getModel() {
        return modelProperty().get();
    }

    @Override
    public ObjectProperty<VNode> modelProperty() {
        return modelProperty;
    }

    @Override
    public VFlow getController() {
        return flowProperty.get();
    }

    @Override
    public void setController(VFlow flow) {
        flowProperty.set(flow);
    }

    @Override
    public SkinFactory getSkinFactory() {
        return skinFactory;
    }

    /**
     * @return the removed
     */
    public boolean isRemoved() {
        return removed;
    }
}

final class ConnectionSkinStub implements ConnectionSkin<Connection> {

    private final ObjectProperty<Connector> senderProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Connector> receiverProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Connection> modelProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<VFlow> flowProperty = new SimpleObjectProperty<>();
    private final SkinFactory skinFactory;

    private boolean removed;

    public ConnectionSkinStub(SkinFactory skinFactory, Connection c, VFlow flow) {
        this.skinFactory = skinFactory;
        setModel(c);
        setController(flow);
    }

    @Override
    public Connector getSender() {
        return senderProperty().get();
    }

    @Override
    public void setSender(Connector c) {
        senderProperty().set(c);
    }

    @Override
    public ObjectProperty<Connector> senderProperty() {
        return senderProperty;
    }

    @Override
    public Connector getReceiver() {
        return receiverProperty().get();
    }

    @Override
    public void setReceiver(Connector c) {
        receiverProperty().set(c);
    }

    @Override
    public ObjectProperty<Connector> receiverProperty() {
        return receiverProperty;
    }

    @Override
    public void receiverToFront() {
        //
    }

    @Override
    public void add() {
        //
    }

    @Override
    public void remove() {
        removed = true;
    }

    @Override
    public void setModel(Connection model) {
        modelProperty().set(model);
    }

    @Override
    public Connection getModel() {
        return modelProperty().get();
    }

    @Override
    public ObjectProperty<Connection> modelProperty() {
        return modelProperty;
    }

    @Override
    public VFlow getController() {
        return flowProperty.get();
    }

    @Override
    public void setController(VFlow flow) {
        flowProperty.set(flow);
    }

    @Override
    public SkinFactory getSkinFactory() {
        return skinFactory;
    }

    /**
     * @return the removed
     */
    public boolean isRemoved() {
        return removed;
    }

}

class VNodeSkinFactoryStub implements SkinFactory<ConnectionSkinStub, VNodeSkinStub> {

    private final Map<VNode, VNodeSkinStub> nodeSkins = new HashMap<>();
    private final Map<Connection, ConnectionSkinStub> connectionSkins = new HashMap<>();

    private final Map<VFlow, VNodeSkinFactoryStub> childFactories = new HashMap<>();

    private SkinFactory<ConnectionSkinStub, VNodeSkinStub> parentFactory;

    VNodeSkinFactoryStub() {
        //
    }

    @Override
    public VNodeSkin createSkin(VNode n, VFlow controller) {
        VNodeSkinStub skin = new VNodeSkinStub(this, n, controller);

        System.out.println("N: " + n.getId());

        getNodeSkins().put(n, skin);

        return skin;
    }

    @Override
    public SkinFactory<ConnectionSkinStub, VNodeSkinStub> createChild(Skin parent) {
        VNodeSkinFactoryStub sf = new VNodeSkinFactoryStub();

        getChildFactories().put(parent.getController(), sf);

        return sf;
    }

    @Override
    public SkinFactory<ConnectionSkinStub, VNodeSkinStub> getParent() {
        return parentFactory;
    }

    @Override
    public ConnectionSkin createSkin(Connection c, VFlow flow, String type) {
        ConnectionSkinStub skin = new ConnectionSkinStub(this, c, flow);

        getConnectionSkins().put(c, skin);

        return skin;
    }

    /**
     * @return the nodeSkins
     */
    public Map<VNode, VNodeSkinStub> getNodeSkins() {
        return nodeSkins;
    }

    /**
     * @return the connectionSkins
     */
    public Map<Connection, ConnectionSkinStub> getConnectionSkins() {
        return connectionSkins;
    }

    /**
     * @return the childFactories
     */
    public Map<VFlow, VNodeSkinFactoryStub> getChildFactories() {
        return childFactories;
    }

}
