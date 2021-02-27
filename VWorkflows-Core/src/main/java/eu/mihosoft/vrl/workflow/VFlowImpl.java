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
import eu.mihosoft.vrl.workflow.skin.FlowNodeSkinLookup;
import eu.mihosoft.vrl.workflow.skin.SkinFactory;
import eu.mihosoft.vrl.workflow.skin.VNodeSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of the {@code VFlow} interface. This class manages the
 * workflow
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class VFlowImpl implements VFlow {

    final ObjectProperty<VFlowModel> modelProperty = new SimpleObjectProperty<>();
    private ListChangeListener<VNode> nodesListener;
    private ListChangeListener<Connection> connectionsListener;
    private final ObservableList<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> skinFactories = FXCollections.observableArrayList();
    private final Map<SkinFactory, Map<String, VNodeSkin>> nodeSkins = new WeakHashMap<>();
    private final Map<SkinFactory, Map<String, ConnectionSkin>> connectionSkins = new WeakHashMap<>();
    private final ObservableMap<String, VFlow> subControllers = FXCollections.observableHashMap();
    private ChangeListener<Boolean> visibilityListener;
    private IdGenerator idGenerator;
    private NodeLookup nodeLookup;
    private FlowNodeSkinLookup nodeSkinLookup;
    private final ObjectProperty<VFlow> parentProperty = new SimpleObjectProperty<>();

    public VFlowImpl(VFlow parent, VFlowModel model, SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>... skinFactories) {

        init();

        if (model.getIdGenerator() != null) {
            setIdGenerator(model.getIdGenerator());
        }

        if (model.getNodeLookup() != null) {
            setNodeLookup(model.getNodeLookup());
        }

        setModel(model);
        setSkinFactories(skinFactories);

    }

    @Override
    public ReadOnlyObjectProperty<VFlow> parentProperty() {
        return parentProperty;
    }

    @Override
    public VFlow getParent() {
        return parentProperty().get();
    }

    @Override
    public VFlow getRootFlow() {
        VFlow root = this;
        VFlow parent = this.getParent();

        while (parent != null) {
            root = parent;
            parent = parent.getParent();
        }

        return root;
    }

    public VFlowImpl(VFlow parent, VFlowModel model, Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> skinFactories) {
        init();

        if (model.getIdGenerator() != null) {
            setIdGenerator(model.getIdGenerator());
        }

        if (model.getNodeLookup() != null) {
            setNodeLookup(model.getNodeLookup());
        }

        setModel(model);
        setSkinFactories(skinFactories);

    }

    private void init() {

        setIdGenerator(new IdGeneratorImpl());
        setNodeSkinLookup(new FlowNodeSkinLookupImpl(this));

        nodesListener = new ListChangeListener<VNode>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends VNode> change) {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        for (int i = change.getFrom(); i < change.getTo(); ++i) {
                            //permutate
                            String action = "permutate"; // TODO: implement
                        }
                    } else if (change.wasUpdated()) {
                        //update item
                        String action = "update"; // TODO: implement
                    } else if (change.wasRemoved()) {
                        // removed
                        for (VNode n : change.getRemoved()) {
//                            if (nodeSkins.containsKey(n.getId())) {
                            if (!getNodeSkinsById(n.getId()).isEmpty()) {

                                removeNodeSkinFromAllSkinFactories(n);

                                for (Connector connector : n.getConnectors()) {

//                                    System.out.println("conn: " + connector);
                                    Collection<Connection> connections
                                            = getConnections(connector.getType()).
                                            getAllWith(connector);

                                    for (Connection connection : connections) {
                                        VFlowImpl.this.getConnections(connector.getType()).
                                                remove(connection);
                                    }
                                }

//                                System.out.println("remove node: " + n.getId());
                            }

                            if (n instanceof FlowModel) {
                                subControllers.remove(n.getId());
                            }

                        }
                    } else if (change.wasAdded()) {
                        // added
                        for (VNode n : change.getAddedSubList()) {
//                            if (!nodeSkins.containsKey(n.getId())) {
                            if (getNodeSkinsById(n.getId()).isEmpty()) {
                                createNodeSkins(n, skinFactories);
//                                System.out.println("add node: " + n.getId() + ", title: " + n.getTitle());
                            } else {
                                String action = "handle error"; // TODO: implement
//                                System.out.println("can't add node: " + n.getId() + ", title: " + n.getTitle());
                            }
                        }
                    }

                } // end while change.next()
            }
        };

        connectionsListener = new ListChangeListener<Connection>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Connection> change) {

                while (change.next()) {
                    if (change.wasPermutated()) {
                        for (int i = change.getFrom(); i < change.getTo(); ++i) {
                            //permutate
                            String action = "permutate"; // TODO: implement
                        }
                    } else if (change.wasUpdated()) {
                        //update item
                        String action = "update"; // TODO: implement
                    } else if (change.wasRemoved()) {
                        // removed
                        for (Connection c : change.getRemoved()) {

                            // fire events <begin>
                            // 07.09.2013
                            // TODO add connector references to connections
                            //      reason: lookup is too expensive
//                            Connector s = getNodeLookup().getConnectorById(c.getSenderId());
//                            Connector r = getNodeLookup().getConnectorById(c.getReceiverId());
                            Connector s = c.getSender();
                            Connector r = c.getReceiver();

                            ConnectionEvent evt = new ConnectionEvent(ConnectionEvent.REMOVE, s, r, c);

                            if (s != null) {

                                Collection<EventHandler<ConnectionEvent>> eventHandlersS
                                        = ((ConnectorImpl) s).getConnectionEventHandlers();

                                if (eventHandlersS != null) {
                                    for (EventHandler<ConnectionEvent> evtHandler : eventHandlersS) {
                                        try {
                                            evtHandler.handle(evt);
                                        } catch (Throwable tr) {
                                            Logger.getLogger(VFlowImpl.class.getName()).log(Level.SEVERE, null, tr);
                                        }
                                    }
                                }
                            }

                            if (r != null) {

                                Collection<EventHandler<ConnectionEvent>> eventHandlersR
                                        = ((ConnectorImpl) r).getConnectionEventHandlers();

                                if (eventHandlersR != null) {
                                    for (EventHandler<ConnectionEvent> evtHandler : eventHandlersR) {
                                        try {
                                            evtHandler.handle(evt);
                                        } catch (Throwable tr) {
                                            Logger.getLogger(VFlowImpl.class.getName()).log(Level.SEVERE, null, tr);
                                        }
                                    }
                                }
                            }

                            // fire events <end>
                            // remove skins for each connection 
                            // that have been removed
                            removeConnectionSkinFromAllSkinFactories(c);
//                            System.out.println("remove skin: " + c);
                        }
                    } else if (change.wasAdded()) {
                        // added
                        for (Connection c : change.getAddedSubList()) {

                            // fire events <begin>
                            // 07.09.2013
                            // TODO add connector references to connections
                            //      reason: lookup is too expensive
//                            Connector s = getNodeLookup().getConnectorById(c.getSenderId());
//                            Connector r = getNodeLookup().getConnectorById(c.getReceiverId());
                            Connector s = c.getSender();
                            Connector r = c.getReceiver();

                            ConnectionEvent evt = new ConnectionEvent(ConnectionEvent.ADD, s, r, c);

                            Collection<EventHandler<ConnectionEvent>> eventHandlersS
                                    = ((ConnectorImpl) s).getConnectionEventHandlers();

                            if (eventHandlersS != null) {
                                for (EventHandler<ConnectionEvent> evtHandler : eventHandlersS) {
                                    try {
                                        evtHandler.handle(evt);
                                    } catch (Throwable tr) {
                                        Logger.getLogger(VFlowImpl.class.getName()).log(Level.SEVERE, null, tr);
                                    }
                                }
                            }

                            Collection<EventHandler<ConnectionEvent>> eventHandlersR
                                    = ((ConnectorImpl) r).getConnectionEventHandlers();

                            if (eventHandlersR != null) {
                                for (EventHandler<ConnectionEvent> evtHandler : eventHandlersR) {
                                    try {
                                        evtHandler.handle(evt);
                                    } catch (Throwable tr) {
                                        Logger.getLogger(VFlowImpl.class.getName()).log(Level.SEVERE, null, tr);
                                    }
                                }
                            }

                            // fire events <end>
                            // create skins for added connections
                            createConnectionSkins(c, c.getType(), getSkinFactories());
//                            System.out.println("add skin: " + c);
                        }
                    }
                }

            }
        };

        visibilityListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {

//                System.out.println("visible: " + t1 + " : " + getModel().isVisible() + " : " + getModel().getId());
                if (t1) {
                    setSkinFactories(getSkinFactories());
                } else {
                    removeUIFromAllSkinFactories();
                }
            }
        };

        modelProperty.addListener(new ChangeListener<FlowModel>() {
            @Override
            public void changed(ObservableValue<? extends FlowModel> ov, FlowModel t, FlowModel t1) {

//                removeUIFromAllSkinFactories();
                Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> tmpFactories = new ArrayList<>();
                tmpFactories.addAll(getSkinFactories());
                removeSkinFactories(getSkinFactories());
                setSkinFactories(tmpFactories);

                if (t != null) {

                    if (nodesListener != null) {
                        t.getNodes().removeListener(nodesListener);
                    }

                    if (connectionsListener != null) {
                        for (Connections conn : t.getAllConnections().values()) {
                            System.out.println("listener for conn-type removed: " + conn.getType());
                            conn.getConnections().removeListener(connectionsListener);
                        }
                    }

                    if (visibilityListener != null) {
                        t.visibleProperty().removeListener(visibilityListener);
                    }
                }

                if (t1 != null) {

                    _updateIdGenerator();
                    _updateNodeLookup();

                    if (nodesListener != null) {
                        t1.getNodes().addListener(nodesListener);
                    }

                    for (Connections conn : t1.getAllConnections().values()) {
                        conn.getConnections().addListener(connectionsListener);
                    }

                    t1.getAllConnections().addListener(new MapChangeListener<String, Connections>() {
                        @Override
                        public void onChanged(MapChangeListener.Change<? extends String, ? extends Connections> change) {
                            if (change.wasAdded()) {
                                change.getValueAdded().getConnections().addListener(connectionsListener);
                            }

                            if (change.wasRemoved()) {
                                change.getValueRemoved().getConnections().removeListener(connectionsListener);
                            }
                        }
                    });

                    getModel().visibleProperty().addListener(visibilityListener);
                }
            }
        });
    }

    private void _updateIdGenerator() {
        getModel().setIdGenerator(idGenerator);
    }

    private void _updateNodeLookup() {
        if (nodeLookup == null) {
            setNodeLookup(new NodeLookupImpl(getModel()));
        }

        getModel().setNodeLookup(getNodeLookup());
    }

    // TODO duplicated code
    public static String connectionId(String id, String s, String r) {
        return "id=" + id + ";[" + s + "]->[" + r + "]";
    }

    // TODO duplicated code
    public static String connectionId(Connection c) {
        return connectionId(c.getId(), c.getSender().getId(), c.getReceiver().getId());
    }

    @Override
    public ConnectionResult tryConnect(Connector s, Connector r) {
        return getModel().tryConnect(s, r);
    }

    @Override
    public ConnectionResult connect(Connector s, Connector r) {
        return getModel().connect(s, r);
    }

    @Override
    public ConnectionResult tryConnect(VNode s, VNode r, String type) {
        return getModel().tryConnect(s, r, type);
    }

    @Override
    public ConnectionResult tryConnect(VFlow s, VNode r, String type) {
        return getModel().tryConnect(s.getModel(), r, type);
    }

    @Override
    public ConnectionResult tryConnect(VNode s, VFlow r, String type) {
        return getModel().tryConnect(s, r.getModel(), type);
    }

    @Override
    public ConnectionResult tryConnect(VFlow s, VFlow r, String type) {
        return getModel().tryConnect(s.getModel(), r.getModel(), type);
    }

    @Override
    public ConnectionResult connect(VNode s, VNode r, String type) {
        return getModel().connect(s, r, type);
    }

    @Override
    public ConnectionResult connect(VFlow s, VNode r, String type) {
        return getModel().connect(s.getModel(), r, type);
    }

    @Override
    public ConnectionResult connect(VNode s, VFlow r, String type) {
        return getModel().connect(s, r.getModel(), type);
    }

    @Override
    public ConnectionResult connect(VFlow s, VFlow r, String type) {
        return getModel().connect(s.getModel(), r.getModel(), type);
    }

    @Override
    public ObservableList<VNode> getNodes() {
        return getModel().getNodes();
    }

    @Override
    public VNode remove(VNode n) {
        getNodeLookup().invalidateCache();
        return getModel().remove(n);
    }

    @Override
    public ObservableMap<String, Connections> getAllConnections() {
        return getModel().getAllConnections();
    }

    @Override
    public Connections getConnections(String type) {
        return getModel().getConnections(type);
    }

    @Override
    public VNode getSender(Connection c) {
        return getModel().getSender(c);
    }

    @Override
    public VNode getReceiver(Connection c) {
        return getModel().getReceiver(c);
    }

    @Override
    public void setFlowNodeClass(Class<? extends VNode> cls) {
        getModel().setFlowNodeClass(cls);
    }

    @Override
    public Class<? extends VNode> getFlowNodeClass() {
        return getModel().getFlowNodeClass();
    }

    @Override
    public VNode newNode(ValueObject obj) {

        return getModel().newNode(obj);
    }

    @Override
    public VNode newNode() {
        return getModel().newNode();
    }

    private List<VNodeSkin<VNode>> createNodeSkins(VNode n, List<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> skinFactories) {
        return createNodeSkins(n, skinFactories.toArray(new SkinFactory[skinFactories.size()]));
    }

    private List<VNodeSkin<VNode>> createNodeSkins(VNode n, SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>... skinFactories) {

        if (getModel() != null && !getModel().isVisible()) {
            return null;
        }

//        System.out.println(">> creating skins for node: " + n.getId());
        List<VNodeSkin<VNode>> skins = new ArrayList<>();

//        System.out.println(" --> #skinFactories: " + skinFactories.length);
        for (SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> skinFactory : skinFactories) {

            if (skinFactory == null) {
                System.err.println("ERROR: skinFactory must not be null!");
                continue;
            }

//            System.out.println(" --> adding to skinfsctory: " + skinFactory);
            VNodeSkin skin = skinFactory.createSkin(n, this);

//        nodeSkins.put(n.getId(), skin);
            putNodeSkin(skinFactory, skin);

            skin.add();

            skins.add(skin);

        }

        return skins;
    }

    private void putNodeSkin(SkinFactory skinFactory, VNodeSkin<VNode> skin) {
        Map<String, VNodeSkin> nodeSkinMap = getNodeSkinMap(skinFactory);

//        System.out.println("put skin " + skin + "for: " + skin.getModel().getId() + ", factory: " + skinFactory);
        nodeSkinMap.put(skin.getModel().getId(), skin);
    }

    private VNodeSkin<VNode> getNodeSkin(SkinFactory skinFactory, String id) {
        Map<String, VNodeSkin> nodeSkinMap = getNodeSkinMap(skinFactory);

        VNodeSkin<VNode> nodeSkin = nodeSkinMap.get(id);

//        System.out.println("skin for " + id + " = " + nodeSkin + ", factory: " + skinFactory + ", controller: " + getModel().getId());
        return nodeSkin;
    }

    public List<VNodeSkin> getAllNodeSkins() {
        List<VNodeSkin> result = new ArrayList<>();

        for (SkinFactory<?, ?> skinFactory : getSkinFactories()) {
            result.addAll(getNodeSkinMap(skinFactory).values());
        }

        return result;
    }

    private VNodeSkin<VNode> removeNodeSkinFromFactory(SkinFactory skinFactory, String id) {
        Map<String, VNodeSkin> nodeSkinMap = getNodeSkinMap(skinFactory);

        VNodeSkin skin = nodeSkinMap.remove(id);

        if (skin != null) {
            skin.remove();
        }

        // if id references a subflow, remove all node skins from the subflow that
        VFlow flow = null;

        for (VFlow subFlow : getSubControllers()) {
            if (subFlow.getModel().getId().equals(id)) {
                flow = subFlow;
                break;
            }
        }

        if (flow != null) {
            // remove child skinfactories
            Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> delList = new HashSet<>();
            for (SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> sF : flow.getSkinFactories()) {
                if (getSkinFactories().contains(sF.getParent())) {
                    delList.add(sF);
                }
            }
            flow.removeSkinFactories(delList);
        }

        return skin;
    }

    private void putConnectionSkin(SkinFactory skinFactory, ConnectionSkin<Connection> skin) {
        Map<String, ConnectionSkin> connectionSkinMap = getConnectionSkinMap(skinFactory);

        connectionSkinMap.put(connectionId(skin.getModel()), skin);
    }

    public List<ConnectionSkin> getAllConnectionSkins() {
        List<ConnectionSkin> result = new ArrayList<>();

        for (SkinFactory<?, ?> skinFactory : getSkinFactories()) {
            result.addAll(getConnectionSkinMap(skinFactory).values());
        }

        return result;
    }

    private ConnectionSkin<Connection> removeConnectionSkinFromFactory(SkinFactory skinFactory, Connection c) {
        Map<String, ConnectionSkin> connectionSkinsMap = getConnectionSkinMap(skinFactory);

        ConnectionSkin skin = connectionSkinsMap.remove(connectionId(c));

        if (skin != null) {
            skin.remove();
        }

        return skin;
    }

    private List<ConnectionSkin> createConnectionSkins(Connection c, String type, List<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> skinFactories) {

        SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>[] skinFactoryArray = new SkinFactory[skinFactories.size()];

        for (int i = 0; i < skinFactories.size(); i++) {
            skinFactoryArray[i] = skinFactories.get(i);
        }

        return createConnectionSkins(c, type, skinFactoryArray);
    }

    private List<ConnectionSkin> createConnectionSkins(Connection c, String type, SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>... skinFactories) {

        List<ConnectionSkin> skins = new ArrayList<>();

        if (getModel() != null && !getModel().isVisible()) {
            return null;
        }

        for (SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> skinFactory : skinFactories) {

            if (skinFactory == null) {
                return null;
            }

            ConnectionSkin skin = skinFactory.createSkin(c, this, type);

//            connectionSkins.put(connectionId(c), skin);
            putConnectionSkin(skinFactory, skin);

            skin.add();
            skins.add(skin);
        }

        return skins;
    }

    private void removeNodeSkinFromAllSkinFactories(VNode n) {

        for (SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> skinFactory : getSkinFactories()) {

            if (skinFactory == null) {
                return;
            }

            removeNodeSkinFromFactory(skinFactory, n.getId());
        }

    }

    private void removeConnectionSkinFromAllSkinFactories(Connection c) {

        for (SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> skinFactory : this.getSkinFactories()) {
            removeConnectionSkinFromFactory(skinFactory, c);
        }
    }

    private void removeUIFromAllSkinFactories() {

        for (SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> skinFactory : getSkinFactories()) {
            removeUI(skinFactory);
        }

    }

    private void removeUI(SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> skinFactory) {

        Collection<VNodeSkin> nodeDelList = getNodeSkinMap(skinFactory).values();
        Collection<VNodeSkin> nodeDelListCopy = new ArrayList<>();
        nodeDelListCopy.addAll(nodeDelList);

        for (VNodeSkin<VNode> nS : nodeDelListCopy) {
            nS.remove();
            removeNodeSkinFromFactory(skinFactory, nS.getModel().getId());
        }

        Collection<ConnectionSkin> connectionDelList = getConnectionSkinMap(skinFactory).values();
        Collection<ConnectionSkin> connectionDelListCopy = new ArrayList<>();
        connectionDelListCopy.addAll(connectionDelList);

        for (ConnectionSkin<Connection> cS : connectionDelListCopy) {
            cS.remove();
            removeConnectionSkinFromFactory(skinFactory, cS.getModel());
        }
    }

    @Override
    public final void setSkinFactories(Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> skinFactories) {
        setSkinFactories(skinFactories.toArray(new SkinFactory[skinFactories.size()]));
    }

    @Override
    public final void setSkinFactories(SkinFactory... skinFactories) {

        removeSkinFactories(getSkinFactories());

        this.getSkinFactories().clear();

        addSkinFactories(skinFactories);
    }

    @Override
    public final void addSkinFactories(Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> skinFactories) {
        addSkinFactories(skinFactories.toArray(new SkinFactory[skinFactories.size()]));
    }

    /**
     * @param skinFactories the nodeskin factories to set
     */
    @Override
    public final void addSkinFactories(SkinFactory... skinFactories) {

        Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> tmpList = new ArrayList<>();

        for (SkinFactory sF : skinFactories) {
            tmpList.add(sF);
        }

        this.getSkinFactories().addAll(tmpList);

        if (skinFactories.length > 0) {

            for (VNode n : getNodes()) {

                createNodeSkins(n, skinFactories);
            }

            for (Connections cns : getAllConnections().values()) {
                for (Connection c : cns.getConnections()) {
                    createConnectionSkins(c, c.getType(), skinFactories);
                }
            }
        }

        if (getModel() != null && !getModel().isVisible()) {
            return;
        }

        for (VFlow fC : subControllers.values()) {

            Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> childSkinFactories = new ArrayList<>();

            for (SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> sF : skinFactories) {

                SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> childNodeSkinFactory = null;

                if (sF != null) {
                    childNodeSkinFactory = sF.createChild(
                            getNodeSkin(sF, fC.getModel().getId()));
                }

                childSkinFactories.add(childNodeSkinFactory);
            }

            fC.addSkinFactories(childSkinFactories);
        }
    }

    @Override
    public void removeSkinFactories(SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>... skinFactories) {
//        if (getModel() != null) {
//            System.out.println(">> remove skinfactories from " + getModel().getId() + ", #sf: " + getSkinFactories().size());
//        }

        for (SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> skinFactory : skinFactories) {

            removeUI(skinFactory);
            this.skinFactories.remove(skinFactory);

            // remove child skinfactories
            for (VFlow sFlow : getSubControllers()) {
                Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> delList = new HashSet<>();
                for (SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> sF : sFlow.getSkinFactories()) {
                    if (getSkinFactories().contains(sF.getParent())) {
                        delList.add(sF);
                    }
                }
                sFlow.removeSkinFactories(delList);
            }

        }

//        System.out.println(" --> #skinFactories: " + getSkinFactories().size());
    }

    @Override
    public void removeSkinFactories(Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> skinFactories) {
        removeSkinFactories(skinFactories.toArray(new SkinFactory[skinFactories.size()]));
    }

    @Override
    public void setNodeSkinLookup(FlowNodeSkinLookup skinLookup) {
        this.nodeSkinLookup = skinLookup;
    }

    @Override
    public FlowNodeSkinLookup getNodeSkinLookup() {
        return this.nodeSkinLookup;
    }

    @Override
    public void addConnections(Connections connections, String flowType) {
        getModel().addConnections(connections, flowType);
    }

    @Override
    public final void setModel(VFlowModel flow) {
        modelProperty.set(flow);

        for (VNode n : flow.getNodes()) {
            if (n instanceof VFlowModel) {
                newSubFlow((VFlowModel) n);
            }
        }

    }

    @Override
    public VFlowModel getModel() {
        return modelProperty.get();
    }

    @Override
    public ObjectProperty modelProperty() {
        return modelProperty;
    }

    private VFlow newSubFlow(VFlowModel flowNode) {

        Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> childFactories = new ArrayList<>();

        if (isVisible()) {

            for (SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> skinFactory : getSkinFactories()) {

                VNodeSkin<VNode> skin = getNodeSkin(skinFactory, flowNode.getId());//nodeSkins.get(flowNode.getId());

//                System.out.println("skin: " + skin + ", node: " + flowNode.getId());
                SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> childFactory = null;

                if (skinFactory != null) {
                    childFactory = skinFactory.createChild(skin);
                    childFactories.add(childFactory);
                }
            }
        }

        VFlow controller = new VFlowImpl(this, flowNode, childFactories);

        controller.setIdGenerator(getIdGenerator());
        controller.setNodeLookup(getNodeLookup());
        controller.setNodeSkinLookup(getNodeSkinLookup());

        for (String connectionType : getAllConnections().keySet()) {
            if (flowNode.getConnections(connectionType) == null) {
                controller.addConnections(
                        VConnections.newConnections(connectionType),
                        connectionType);
            }
        }

        subControllers.put(flowNode.getId(), controller);

        return controller;
    }

    @Override
    public VFlow newSubFlow(ValueObject obj) {
        return newSubFlow(getModel().newFlowNode(obj));
    }

    @Override
    public VFlow newSubFlow() {
        VFlowModel flowNode = getModel().newFlowNode();

        return newSubFlow(flowNode);
    }

    @Override
    public Collection<VFlow> getSubControllers() {
        return Collections.unmodifiableCollection(subControllers.values());
    }

    @Override
    public final void setIdGenerator(IdGenerator generator) {
        this.idGenerator = generator;
    }

    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    /**
     * @return the nodeLookup
     */
    @Override
    public NodeLookup getNodeLookup() {
        return nodeLookup;
    }

    /**
     * @param nodeLookup the nodeLookup to set
     */
    @Override
    public final void setNodeLookup(NodeLookup nodeLookup) {
        this.nodeLookup = nodeLookup;
    }

    @Override
    public List<VNodeSkin> getNodeSkinsById(String id) {

        List<VNodeSkin> result = new ArrayList<>();

        for (SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> sF : this.getSkinFactories()) {
            VNodeSkin nodeSkin = getNodeSkin(sF, id);

            if (nodeSkin != null) {
                result.add(nodeSkin);
            }
        }

        return result;
    }

    @Override
    public void setVisible(boolean state) {
        getModel().setVisible(state);
    }

    @Override
    public boolean isVisible() {
        return getModel().isVisible();
    }

    @Override
    public BooleanProperty visibleState() {
        return getModel().visibleProperty();
    }

//    @Override
//    public boolean isInputOfType(String type) {
//        return getModel().isInputOfType(type);
//    }
//
//    @Override
//    public boolean isOutputOfType(String type) {
//        return getModel().isOutputOfType(type);
//    }
//
//    @Override
//    public boolean isInput() {
//        return getModel().isInput();
//    }
//
//    @Override
//    public boolean isOutput() {
//        return getModel().isOutput();
//    }
//
//    
//
//    @Override
//    public ObservableList<String> getInputTypes() {
//        return getModel().getInputTypes();
//    }
//
//    @Override
//    public ObservableList<String> getOutputTypes() {
//        return getModel().getOutputTypes();
//    }
    public synchronized Map<String, VNodeSkin> getNodeSkinMap(SkinFactory skinFactory) {
        Map<String, VNodeSkin> nodeSkinMap = nodeSkins.get(skinFactory);
        if (nodeSkinMap == null) {
            nodeSkinMap = new HashMap<>();
            nodeSkins.put(skinFactory, nodeSkinMap);
        }
        return nodeSkinMap;
    }

    public synchronized Map<String, ConnectionSkin> getConnectionSkinMap(SkinFactory skinFactory) {
        Map<String, ConnectionSkin> connectionSkinMap = connectionSkins.get(skinFactory);
        if (connectionSkinMap == null) {
            connectionSkinMap = new HashMap<>();
            connectionSkins.put(skinFactory, connectionSkinMap);
        }
        return connectionSkinMap;
    }

    /**
     * @return the skinFactories
     */
    @Override
    public ObservableList<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> getSkinFactories() {
        return skinFactories;
    }

    @Override
    public void clear() {
        getModel().clear();
    }

    @Override
    public Connector addInput(String type) {
        return getModel().addInput(type);
    }

    @Override
    public Connector addOutput(String type) {
        return getModel().addOutput(type);
    }

    @Override
    public VFlow getFlowById(String id) {
        return getFlowById(this, id);
    }

    private VFlow getFlowById(VFlow parent, String id) {

        if (getModel().getId().equals(id)) {
            return this;
        }

        for (VFlow sf : parent.getSubControllers()) {
            if (sf.getModel().getId().equals(id)) {
                return sf;
            } else {
                VFlow tmp = getFlowById(sf, id);

                if (tmp != null) {
                    return tmp;
                }
            }
        }

        return null;
    }

    @Override
    public ThruConnector addThruInput(String type) {
        return getModel().addThruInput(type);
    }

    @Override
    public ThruConnector addThruOutput(String type) {
        return getModel().addThruOutput(type);
    }

    @Override
    public ObservableList<ThruConnector> getThruInputs() {
        return getModel().getThruInputs();
    }

    @Override
    public ObservableList<ThruConnector> getThruOutputs() {
        return getModel().getThruOutputs();
    }
}
