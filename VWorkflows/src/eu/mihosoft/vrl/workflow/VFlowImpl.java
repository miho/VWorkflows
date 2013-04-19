/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class VFlowImpl implements VFlow {

    ObjectProperty<VFlowModel> modelProperty = new SimpleObjectProperty<>();
    private ListChangeListener<VNode> nodesListener;
    private ListChangeListener<Connection> connectionsListener;
    private SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> skinFactory;
    private Map<String, VNodeSkin> nodeSkins = new HashMap<>();
    private Map<String, ConnectionSkin> connectionSkins = new HashMap<>();
    private ObservableMap<String, VFlow> subControllers = FXCollections.observableHashMap();
    private ChangeListener<Boolean> visibilityListener;
    private IdGenerator idGenerator;
    private NodeLookup nodeLookup;
    private FlowNodeSkinLookup nodeSkinLookup;

    public VFlowImpl(SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> skinFactory) {
        this.skinFactory = skinFactory;

        init();
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
                        }
                    } else if (change.wasUpdated()) {
                        //update item
                    } else if (change.wasRemoved()) {
                        // removed
                        for (VNode n : change.getRemoved()) {
                            if (nodeSkins.containsKey(n.getId())) {
                                removeNodeSkin(n);
//                                 System.out.println("remove node: " + n.getId());
                            }

                            if (n instanceof FlowModel) {
                                subControllers.remove(n.getId());
                            }


                        }
                    } else if (change.wasAdded()) {
                        // added
                        for (VNode n : change.getAddedSubList()) {
                            if (!nodeSkins.containsKey(n.getId())) {
                                createNodeSkin(n);
//                                 System.out.println("add node: " + n.getId());
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
                        }
                    } else if (change.wasUpdated()) {
                        //update item
                    } else if (change.wasRemoved()) {
                        // removed
                        for (Connection n : change.getRemoved()) {
                            removeConnectionSkin(n);
//                            System.out.println("remove skin: " + n);
                        }
                    } else if (change.wasAdded()) {
                        // added
                        for (Connection c : change.getAddedSubList()) {
                            createConnectionSkin(c, c.getType());
                        }
                    }

                }
            }
        };


        visibilityListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {

                System.out.println("visible: " + t1 + " : " + getModel().isVisible() + " : " + getModel().getId());

                if (t1) {
                    setSkinFactory(skinFactory);
                } else {
                    removeUI();
                }
            }
        };

        modelProperty.addListener(new ChangeListener<FlowModel>() {
            @Override
            public void changed(ObservableValue<? extends FlowModel> ov, FlowModel t, FlowModel t1) {

                removeUI();

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
    private static String connectionId(String id, String s, String r) {
        return "id=" + id + ";[" + s + "]->[" + r + "]";
    }

    // TODO duplicated code
    private static String connectionId(Connection c) {
        return connectionId(c.getId(), c.getSenderId(), c.getReceiverId());
    }

    @Override
    public ConnectionResult tryConnect(VNode s, VNode r, String type) {
        return getModel().tryConnect(s, r, type);
    }

    @Override
    public ConnectionResult connect(VNode s, VNode r, String type) {

        return getModel().connect(s, r, type);
    }

    @Override
    public ObservableList<VNode> getNodes() {
        return getModel().getNodes();
    }

    @Override
    public VNode remove(VNode n) {
        return getModel().remove(n);
    }

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

    private VNodeSkin createNodeSkin(VNode n) {

        if (skinFactory == null) {
            return null;
        }

        if (getModel() != null && !getModel().isVisible()) {
            return null;
        }

        VNodeSkin skin = skinFactory.createSkin(n, this);

        nodeSkins.put(n.getId(), skin);
        skin.add();

        return skin;
    }

    private ConnectionSkin createConnectionSkin(Connection c, String type) {

        if (skinFactory == null) {
            return null;
        }

        if (getModel() != null && !getModel().isVisible()) {
            return null;
        }

        ConnectionSkin skin = skinFactory.createSkin(c, this, type);

        connectionSkins.put(connectionId(c), skin);
        skin.add();

        return skin;
    }

    private void removeNodeSkin(VNode n) {

        if (skinFactory == null) {
            return;
        }

        VNodeSkin skin = nodeSkins.remove(n.getId());

        if (skin != null) {
            skin.remove();
        }
    }

    private void removeConnectionSkin(Connection c) {

        if (skinFactory == null) {
            return;
        }

        ConnectionSkin skin = connectionSkins.remove(connectionId(c));

        if (skin != null) {
            skin.remove();
        }
    }

    private void removeUI() {

        List<VNodeSkin> nodeDelList = new ArrayList<>(nodeSkins.values());

        for (VNodeSkin<VNode> nS : nodeDelList) {
            nS.remove();
        }
        nodeSkins.clear();

        List<ConnectionSkin> connectionDelList =
                new ArrayList<>(connectionSkins.values());

        for (ConnectionSkin<Connection> cS : connectionDelList) {
            cS.remove();
        }

        connectionSkins.clear();
    }

    /**
     * @param nodeSkinFactory the nodeSkinFactory to set
     */
    @Override
    public void setSkinFactory(SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> skinFactory) {

        this.skinFactory = skinFactory;

        if (skinFactory == null) {
            removeUI();

        } else {

            for (VNode n : getNodes()) {
                createNodeSkin(n);
            }

            for (Connections cns : getAllConnections().values()) {
                for (Connection c : cns.getConnections()) {

                    createConnectionSkin(c, c.getType());

                }
            }
        }

        if (getModel() != null && !getModel().isVisible()) {
            return;
        }

        for (VFlow fC : subControllers.values()) {

            SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> childNodeSkinFactory = null;

            if (skinFactory != null) {
                childNodeSkinFactory = skinFactory.createChild(
                        nodeSkins.get(fC.getModel().getId()));
            }

            fC.setSkinFactory(childNodeSkinFactory);
        }
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
    public void setModel(VFlowModel flow) {
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

        VNodeSkin<VNode> skin = nodeSkins.get(flowNode.getId());

        SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> childFactory = null;

        if (skinFactory != null) {
            childFactory = skinFactory.createChild(skin);
        }

        VFlow controller = new VFlowImpl(childFactory);

        controller.setIdGenerator(getIdGenerator());
        controller.setNodeLookup(getNodeLookup());
        controller.setNodeSkinLookup(getNodeSkinLookup());
        controller.setModel(flowNode);


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
    public void setIdGenerator(IdGenerator generator) {
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
    public void setNodeLookup(NodeLookup nodeLookup) {
        this.nodeLookup = nodeLookup;
    }

    @Override
    public VNodeSkin getNodeSkinById(String id) {
        return nodeSkins.get(id);
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

    @Override
    public boolean isInputOfType(String type) {
       return getModel().isInputOfType(type);
    }

    @Override
    public boolean isOutputOfType(String type) {
        return getModel().isOutputOfType(type);
    }

    @Override
    public boolean isInput() {
        return getModel().isInput();
    }

    @Override
    public boolean isOutput() {
        return getModel().isOutput();
    }

    @Override
    public void setInput(boolean state, String type) {
        getModel().setInput(state, type);
    }

    @Override
    public void setOutput(boolean state, String type) {
        getModel().setOutput(state, type);
    }

    @Override
    public ObservableList<String> getInputTypes() {
        return getModel().getInputTypes();
    }

    @Override
    public ObservableList<String> getOutputTypes() {
        return getModel().getOutputTypes();
    }
}
