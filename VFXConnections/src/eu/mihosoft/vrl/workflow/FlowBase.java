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

    @Override
    public ConnectionResult tryConnect(FlowNode s, FlowNode r) {
        CompatibilityResult result = r.getValueObject().
                compatible(s.getValueObject());

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

        Collection<Connection> connectionsToRemove =
                getConnections().getAllWith(n.getId());

        for (Connection c : connectionsToRemove) {
            getConnections().remove(c);
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
            Constructor constructor = cls.getConstructor();
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
            Constructor constructor = getFlowNodeClass().getConstructor();
            try {
                result = (FlowNode) constructor.newInstance();
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

            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ConnectionsImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(ConnectionsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }
}
