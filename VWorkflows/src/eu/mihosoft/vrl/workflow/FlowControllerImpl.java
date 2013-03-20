/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private FlowNodeSkinFactory nodeSkinFactory;
    private ConnectionSkinFactory connectionSkinFactory;
    private Map<String, FlowNodeSkin> nodeSkins = new HashMap<>();
    private Map<String, ConnectionSkin> connectionSkins = new HashMap<>();

    public FlowControllerImpl(FlowNodeSkinFactory nodeSkinFactory, ConnectionSkinFactory connectionSkinFactory) {
        this.nodeSkinFactory = nodeSkinFactory;
        this.connectionSkinFactory = connectionSkinFactory;

        init();
    }

    private void init() {

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
                    } else {
                        if (change.wasRemoved()) {
                            // removed
                            for (FlowNode n : change.getRemoved()) {
                                removeNodeSkin(n);
                            }
                        } else if (change.wasAdded()) {
                            // added
                            for (FlowNode n : change.getAddedSubList()) {
                                createNodeSkin(n);
                            }
                        }
                    }
                }
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
                    } else {
                        if (change.wasRemoved()) {
                            // removed
                            for (Connection n : change.getRemoved()) {
                                removeConnectionSkin(n);
                            }
                        } else if (change.wasAdded()) {
                            // added
                            for (Connection n : change.getAddedSubList()) {
                                // TODO only control type possible, shall connection know its type?
                                createConnectionSkin(n, "control");
                            }
                        }
                    }
                }
            }
        };

        modelProperty.addListener(new ChangeListener<FlowModel>() {
            @Override
            public void changed(ObservableValue<? extends FlowModel> ov, FlowModel t, FlowModel t1) {
                if (t != null) {

                    if (nodesListener != null) {
                        t.getNodes().removeListener(nodesListener);
                    }

                    if (connectionsListener != null) {
                        for (Connections conn : t.getAllConnections().values()) {
                            conn.getConnections().removeListener(connectionsListener);
                        }
                    }
                }

                if (t1 != null) {

                    if (nodesListener != null) {
                        t1.getNodes().addListener(nodesListener);
                    }

                    if (connectionsListener != null) {
                        for (Connections conn : t1.getAllConnections().values()) {
                            conn.getConnections().addListener(connectionsListener);
                        }
                    }


                    t1.getAllConnections().addListener(new MapChangeListener<String, Connections>() {
                        @Override
                        public void onChanged(MapChangeListener.Change<? extends String, ? extends Connections> change) {
                            if (change.wasAdded()) {
                                change.getValueAdded().getConnections().addListener(connectionsListener);
                            }

                            if (change.wasRemoved()) {
                                change.getValueAdded().getConnections().removeListener(connectionsListener);
                            }
                        }
                    });
                }
            }
        });

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
    public ConnectionResult tryConnect(FlowNode s, FlowNode r, String type) {
        return getModel().tryConnect(s, r, type);
    }

    @Override
    public ConnectionResult connect(FlowNode s, FlowNode r, String type) {

        return getModel().connect(s, r, type);
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
    public FlowNode newNode(ValueObject obj) {


        return getModel().newNode(obj);
    }

    @Override
    public FlowNode newNode() {
        return getModel().newNode();
    }

    private FlowNodeSkin createNodeSkin(FlowNode n) {
        FlowNodeSkin skin = nodeSkinFactory.createSkin(n);

        nodeSkins.put(n.getId(), skin);
        skin.add();

        return skin;
    }

    private ConnectionSkin createConnectionSkin(Connection c, String type) {
        ConnectionSkin skin = connectionSkinFactory.createSkin(c, this, type);

        connectionSkins.put(connectionId(c), skin);
        skin.add();

        return skin;
    }

    private void removeNodeSkin(FlowNode n) {
        FlowNodeSkin skin = nodeSkins.remove(n.getId());

        if (skin != null) {
            skin.remove();
        }
    }

    private void removeConnectionSkin(Connection c) {
        ConnectionSkin skin = connectionSkins.remove(connectionId(c));

        if (skin != null) {
            skin.remove();
        }
    }

    /**
     * @param nodeSkinFactory the nodeSkinFactory to set
     */
    @Override
    public void setNodeSkinFactory(FlowNodeSkinFactory nodeSkinFactory) {
        this.nodeSkinFactory = nodeSkinFactory;
        
        // TODO build ui
    }

    /**
     * @param connectionSkinFactory the connectionSkinFactory to set
     */
    @Override
    public void setConnectionSkinFactory(ConnectionSkinFactory connectionSkinFactory) {
        this.connectionSkinFactory = connectionSkinFactory;
        
        // TODO build ui
    }

    @Override
    public void addConnections(Connections connections, String flowType) {
        getModel().addConnections(connections, flowType);
    }

    @Override
    public void setModel(FlowFlowNode flow) {
        modelProperty.set(flow);
    }

    @Override
    public FlowFlowNode getModel() {
        return modelProperty.get();
    }

    @Override
    public ObjectProperty modelProperty() {
        return modelProperty;
    }

    @Override
    public FlowController newSubFlow(ValueObject obj) {
        FlowFlowNode flowNode = getModel().newFlowNode(obj);

        FlowNodeSkin<FlowNode> skin = nodeSkins.get(flowNode.getId());

        FlowController controller = new FlowControllerImpl(
                nodeSkinFactory.createChild(skin),
                connectionSkinFactory.createChild(skin));

        controller.setModel(flowNode);

        for (String connectionType : getAllConnections().keySet()) {
            controller.addConnections(VConnections.newConnections(), connectionType);
        }

        return controller;
    }

    @Override
    public FlowController newSubFlow() {
        FlowFlowNode flowNode = getModel().newFlowNode();

        FlowNodeSkin<FlowNode> skin = nodeSkins.get(flowNode.getId());

        FlowController controller = new FlowControllerImpl(
                nodeSkinFactory.createChild(skin),
                connectionSkinFactory.createChild(skin));

        controller.setModel(flowNode);

        for (String connectionType : getAllConnections().keySet()) {
            controller.addConnections(VConnections.newConnections(), connectionType);
        }

        return controller;
    }
}
