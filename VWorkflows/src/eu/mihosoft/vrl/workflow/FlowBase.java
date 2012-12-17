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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FlowBase implements Flow {

    private Map<String, Connections> connections = new HashMap<>();
    private ObservableList<FlowNode> observableNodes =
            FXCollections.observableArrayList();
    private Map<String, FlowNode> nodes = new HashMap<>();
    private Class<? extends FlowNode> flowNodeClass = FlowNodeBase.class;
    private FlowNodeSkinFactory nodeSkinFactory;
    private ConnectionSkinFactory connectionSkinFactory;
    private Map<String, FlowNodeSkin> nodeSkins = new HashMap<>();
    private Map<String, ConnectionSkin> connectionSkins = new HashMap<>();

    private static String connectionId(String id, String s, String r) {
        return "id=" + id + ";[" + s + "]->[" + r + "]";
    }

    private static String connectionId(Connection c) {
        return connectionId(c.getId(), c.getSenderId(), c.getReceiverId());
    }

    @Override
    public ConnectionResult tryConnect(FlowNode s, FlowNode r, String type) {
        CompatibilityResult result = r.getValueObject().
                compatible(s.getValueObject(), type);

        return new ConnectionResultImpl(result, null);
    }

    @Override
    public ConnectionResult connect(FlowNode s, FlowNode r, String type) {

        ConnectionResult result = tryConnect(s, r, type);

        if (!result.getStatus().isCompatible()) {
            return result;
        }

//        nodes.put(s.getId(), s);
//        nodes.put(r.getId(), r);

        observableNodes.add(s);
        observableNodes.add(r);

        Connection connection = getConnections(type).add(s.getId(), r.getId());

        if (connection != null) {
            createConnectionSkin(connection, type);
        }

        return new ConnectionResultImpl(result.getStatus(), connection);
    }

    @Override
    public ObservableList<FlowNode> getNodes() {
        return observableNodes;
    }

    @Override
    public FlowNode remove(FlowNode n) {
        FlowNode result = nodes.remove(n.getId());
        observableNodes.remove(n);

        removeNodeSkin(n);

        for (Connections cns : getAllConnections()) {

            Collection<Connection> connectionsToRemove =
                    cns.getAllWith(n.getId());

            for (Connection c : connectionsToRemove) {
                cns.remove(c);
                removeConnectionSkin(c);
            }

        }

        return result;
    }

    public Collection<Connections> getAllConnections() {
        return connections.values();
    }

    @Override
    public Connections getConnections(String type) {
        return connections.get(type);
    }

    @Override
    public FlowNode getSender(Connection c) {
        return nodes.get(c.getSenderId());
    }

    @Override
    public FlowNode getReceiver(Connection c) {
        return nodes.get(c.getReceiverId());
    }

    @Override
    public void setFlowNodeClass(Class<? extends FlowNode> cls) {
        try {
            Constructor constructor = cls.getConstructor(Flow.class);
            throw new IllegalArgumentException("constructor missing: (String, String)");
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(ConnectionsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.flowNodeClass = cls;
    }

    @Override
    public Class<? extends FlowNode> getFlowNodeClass() {
        return flowNodeClass;
    }

    @Override
    public FlowNode newNode(ValueObject obj) {

        FlowNode result = null;

        try {
            Constructor constructor = getFlowNodeClass().getConstructor(Flow.class);
            try {
                result = (FlowNode) constructor.newInstance(this);
                result.setValueObject(obj);

                // search id:
                String id = "0";
                int count = 0;

                while (nodes.containsKey(id)) {
                    count++;
                    id = "" + count;
                }

                result.setId(id);

                nodes.put(id, result);
                observableNodes.add(result);

                createNodeSkin(result);

            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ConnectionsImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(ConnectionsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }
    
    @Override
    public FlowNode newNode() {
        return newNode(new ValueObject() {
            @Override
            public FlowNode getParent() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Object getValue() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setValue(Object o) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public ObjectProperty<Object> valueProperty() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public CompatibilityResult compatible(ValueObject other, String flowType) {
                return new CompatibilityResult() {
                    @Override
                    public boolean isCompatible() {
                        return true;
                    }

                    @Override
                    public String getMessage() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getStatus() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
            }

            @Override
            public VisualizationRequest getVisualizationRequest() {
                return new VisualizationRequest() {
                    @Override
                    public String getStyle() {
                        return "default";
                    }

                    @Override
                    public String getOptions() {
                        return "";
                    }
                };
            }
        });
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
    }

    /**
     * @param connectionSkinFactory the connectionSkinFactory to set
     */
    @Override
    public void setConnectionSkinFactory(ConnectionSkinFactory connectionSkinFactory) {
        this.connectionSkinFactory = connectionSkinFactory;
    }

    @Override
    public void addConnections(Connections connections, String flowType) {
        this.connections.put(flowType, connections);
    }
}
