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
class FlowControllerImpl implements FlowController {

    ObjectProperty<FlowFlowNode> modelProperty = new SimpleObjectProperty<>();
    private ListChangeListener<FlowNode> nodesListener;
    private ListChangeListener<Connection> connectionsListener;
    private SkinFactory<? extends ConnectionSkin,? extends FlowNodeSkin> skinFactory;
    private Map<String, FlowNodeSkin> nodeSkins = new HashMap<>();
    private Map<String, ConnectionSkin> connectionSkins = new HashMap<>();
    private ObservableMap<String, FlowController> subControllers = FXCollections.observableHashMap();
    private ChangeListener<Boolean> visibilityListener;
    private IdGenerator idGenerator;
    private NodeLookup nodeLookup;
    private FlowNodeSkinLookup nodeSkinLookup;

    public FlowControllerImpl(SkinFactory<? extends ConnectionSkin,? extends FlowNodeSkin> skinFactory) {
        this.skinFactory = skinFactory;

        init();
    }

    private void init() {
        
                
        setIdGenerator(new IdGeneratorImpl());
        setNodeSkinLookup(new FlowNodeSkinLookupImpl(this));

        nodesListener = new ListChangeListener<FlowNode>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends FlowNode> change) {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        for (int i = change.getFrom(); i < change.getTo(); ++i) {
                            //permutate
                        }
                    } else if (change.wasUpdated()) {
                        //update item
                    } else if (change.wasRemoved()) {
                        // removed
                        for (FlowNode n : change.getRemoved()) {
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
                        for (FlowNode n : change.getAddedSubList()) {
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
                        for (Connection n : change.getAddedSubList()) {
                            // TODO only control type possible, shall connection know its type?
//                            if (!connectionSkins.containsKey(n.getId())) {
                            createConnectionSkin(n, "control");
//                                 System.out.println("add skin: " + n);
//                            }
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
    public ConnectionResult tryConnect(Connector s, Connector r) {
        return getModel().tryConnect(s, r);
    }

    @Override
    public ConnectionResult connect(Connector s, Connector r) {

        return getModel().connect(s, r);
    }

    @Override
    public ObservableList<FlowNode> getNodes() {
        return getModel().getNodes();
    }

    @Override
    public FlowNode remove(FlowNode n) {
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
    public FlowNode getSender(Connection c) {
        return getModel().getSender(c);
    }

    @Override
    public FlowNode getReceiver(Connection c) {
        return getModel().getReceiver(c);
    }

    @Override
    public void setFlowNodeClass(Class<? extends FlowNode> cls) {
        getModel().setFlowNodeClass(cls);
    }

    @Override
    public Class<? extends FlowNode> getFlowNodeClass() {
        return getModel().getFlowNodeClass();
    }

    @Override
    public FlowNode newNode(NodeValueObject obj) {


        return getModel().newNode(obj);
    }

    @Override
    public FlowNode newNode() {
        return getModel().newNode();
    }

    private FlowNodeSkin createNodeSkin(FlowNode n) {

        if (skinFactory == null) {
            return null;
        }

        if (getModel() != null && !getModel().isVisible()) {
            return null;
        }

        FlowNodeSkin skin = skinFactory.createSkin(n, this);

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

    private void removeNodeSkin(FlowNode n) {

        if (skinFactory == null) {
            return;
        }

        FlowNodeSkin skin = nodeSkins.remove(n.getId());

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

        List<FlowNodeSkin> nodeDelList = new ArrayList<>(nodeSkins.values());

        for (FlowNodeSkin<FlowNode> nS : nodeDelList) {
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
    public void setSkinFactory(SkinFactory<? extends ConnectionSkin, ? extends FlowNodeSkin> skinFactory) {

        this.skinFactory = skinFactory;
        
        if (skinFactory == null) {
            removeUI();

        } else {

            for (FlowNode n : getNodes()) {
                createNodeSkin(n);
            }
            
            for (Connections cns : getAllConnections().values()) {
                for (Connection c : cns.getConnections()) {

                    // TODO allow other connection types
                    createConnectionSkin(c, "control");

//                    System.out.println(" --> skin for " + c);
                }
            }
        }

        if (getModel() != null && !getModel().isVisible()) {
            return;
        }

        for (FlowController fC : subControllers.values()) {

            SkinFactory<? extends ConnectionSkin, ? extends FlowNodeSkin> childNodeSkinFactory = null;

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
    public void setModel(FlowFlowNode flow) {
        modelProperty.set(flow);

        for (FlowNode n : flow.getNodes()) {
            if (n instanceof FlowFlowNode) {
                newSubFlow((FlowFlowNode) n);
            }
        }

    }

    @Override
    public FlowFlowNode getModel() {
        return modelProperty.get();
    }

    @Override
    public ObjectProperty modelProperty() {
        return modelProperty;
    }

    private FlowController newSubFlow(FlowFlowNode flowNode) {

        FlowNodeSkin<FlowNode> skin = nodeSkins.get(flowNode.getId());

        SkinFactory<? extends ConnectionSkin, ? extends FlowNodeSkin> childFactory = null;

        if (skinFactory != null) {
            childFactory = skinFactory.createChild(skin);
        }

        FlowController controller = new FlowControllerImpl(childFactory);

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
    public FlowController newSubFlow(NodeValueObject obj) {
        return newSubFlow(getModel().newFlowNode(obj));
    }

    @Override
    public FlowController newSubFlow() {
        FlowFlowNode flowNode = getModel().newFlowNode();

        return newSubFlow(flowNode);
    }

    @Override
    public Collection<FlowController> getSubControllers() {
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
    public FlowNodeSkin getNodeSkinById(String id) {
        return nodeSkins.get(id);
    }


}
