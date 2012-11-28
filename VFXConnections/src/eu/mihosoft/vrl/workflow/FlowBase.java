/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FlowBase implements Flow {

    private Connections connections = VConnections.newConnections();
    private ObservableList<FlowNode> observableNodes =
            FXCollections.observableArrayList();
    private Map<String, FlowNode> nodes = new HashMap<>();
    private Class<? extends FlowNode> flowNodeClass = FlowNodeBase.class;
    private ConnectionSkinFactory connectionSkinFactory;
    private Map<String, ConnectionSkin> connectionSkins = new HashMap<>();

    private static String connectionId(String id, String s, String r) {
        return "id=" + id + ";[" + s + "]->[" + r + "]";
    }

    private static String connectionId(Connection c) {
        return connectionId(c.getId(), c.getSenderId(), c.getReceiverId());
    }

    @Override
    public ConnectionResult tryConnect(FlowNode s, FlowNode r) {
        CompatibilityResult result = r.getValueObject().
                compatible(s.getValueObject(), this);

        return new ConnectionResultImpl(result, null);
    }

    @Override
    public ConnectionResult connect(FlowNode s, FlowNode r) {

        ConnectionResult result = tryConnect(s, r);

        if (!result.getStatus().isCompatible()) {
            return result;
        }

//        nodes.put(s.getId(), s);
//        nodes.put(r.getId(), r);

        observableNodes.add(s);
        observableNodes.add(r);

        Connection connection = getConnections().add(s.getId(), r.getId());

        if (connection != null) {
            createConnectionSkin(connection);
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

        Collection<Connection> connectionsToRemove =
                getConnections().getAllWith(n.getId());

        for (Connection c : connectionsToRemove) {
            getConnections().remove(c);
            removeConnectionSkin(c);
        }

        return result;
    }

    @Override
    public Connections getConnections() {
        return connections;
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
    public FlowNode newNode(FlowNode parent, ValueObject obj) {

        FlowNode result = null;

        try {
            Constructor constructor = getFlowNodeClass().getConstructor(Flow.class);
            try {
                result = (FlowNode) constructor.newInstance(this);
                result.setValueObject(obj);

//                // search id:
//                String id = "0";
//                int count = 0;
//
//                while (nodes.containsKey(id)) {
//                    count++;
//                    id = "" + count;
//                }
//
//                result.setId(id);
//
//                nodes.put(id, result);
//                observableNodes.add(result);

                parent.getChildren().add(result);

                nodes.put(result.getId(), result);
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

    private FlowNodeSkin createNodeSkin(FlowNode n) {
        FlowNodeSkin skin = nodeSkinFactory.createSkin(n);

        nodeSkins.put(n.getId(), skin);
        skin.add();

        return skin;
    }

    private ConnectionSkin createConnectionSkin(Connection c) {
        ConnectionSkin skin = connectionSkinFactory.createSkin(c, this);

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
     * @param connectionSkinFactory the connectionSkinFactory to set
     */
    @Override
    public void setConnectionSkinFactory(ConnectionSkinFactory connectionSkinFactory) {
        this.connectionSkinFactory = connectionSkinFactory;
    }
}
