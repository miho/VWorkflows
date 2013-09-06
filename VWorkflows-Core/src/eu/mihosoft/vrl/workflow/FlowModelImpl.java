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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public class FlowModelImpl implements FlowModel {

    private final ObservableMap<String, Connections> connections =
            FXCollections.observableHashMap();
    private final ObservableMap<String, Connections> readOnlyObservableConnections =
            FXCollections.unmodifiableObservableMap(connections);
    private final ObservableList<VNode> observableNodes =
            FXCollections.observableArrayList();
    private final ObservableList<VNode> readOnlyObservableNodes =
            FXCollections.unmodifiableObservableList(observableNodes);
    private final Map<String, VNode> nodes = new HashMap<>();
    private Class<? extends VNode> flowNodeClass = VNodeImpl.class;
    private final BooleanProperty visibleProperty = new SimpleBooleanProperty();
    private IdGenerator idGenerator;
    private NodeLookup nodeLookup;

    @Override
    public BooleanProperty visibleProperty() {
        return visibleProperty;
    }

    @Override
    public void setVisible(boolean b) {
        visibleProperty.set(b);
    }

    @Override
    public boolean isVisible() {
        return visibleProperty.get();
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

        ValueObject senderValObj = new NoDefaultConnectorValueObject(s);
        ValueObject receiverValObj = new NoDefaultConnectorValueObject(r);

        if (s.getMainOutput(type) != null) {
            senderValObj = s.getMainOutput(type).getValueObject();
        }

        if (r.getMainInput(type) != null) {
            receiverValObj = r.getMainInput(type).getValueObject();
        }

        CompatibilityResult result = receiverValObj.
                compatible(senderValObj, type);

        return new ConnectionResultImpl(result, null);
    }

    @Override
    public ConnectionResult connect(VNode s, VNode r, String type) {

        ConnectionResult result = tryConnect(s, r, type);

        if (!result.getStatus().isCompatible()) {
            return result;
        }

        String senderId = null;
        String receiverId = null;

        if (s.getMainOutput(type) != null) {
            senderId = s.getMainOutput(type).getId();
        }

        if (s.getMainInput(type) != null) {
            receiverId = r.getMainInput(type).getId();
        }

        Connection connection = getConnections(type).add(senderId, receiverId);

        return new ConnectionResultImpl(result.getStatus(), connection);
    }

    @Override
    public ConnectionResult tryConnect(Connector s, Connector r) {
        CompatibilityResult result = r.getValueObject().
                compatible(s.getValueObject(), s.getType());

        return new ConnectionResultImpl(result, null);
    }

    @Override
    public ConnectionResult connect(Connector s, Connector r) {

        ConnectionResult result = tryConnect(s, r);

        if (!result.getStatus().isCompatible()) {
            return result;
        }

        Connection connection = getConnections(s.getType()).add(s.getId(), r.getId());

        return new ConnectionResultImpl(result.getStatus(), connection);
    }

    @Override
    public ObservableList<VNode> getNodes() {
        return readOnlyObservableNodes;
    }

    @Override
    public void clear() {
        List<VNode> delList = new ArrayList<>(observableNodes);

        for (VNode n : delList) {
            remove(n);
        }
    }

    @Override
    public VNode remove(VNode n) {

//        if (n instanceof FlowModel) {
//            ((FlowModel)n).clear();
//        }

        VNode result = nodes.remove(n.getId());
        observableNodes.remove(n);

//        removeNodeSkin(n);

        for (Connections cns : getAllConnections().values()) {

            Collection<Connection> connectionsToRemove =
                    cns.getAllWith(n.getId());

            for (Connection c : connectionsToRemove) {
                cns.remove(c);
//                removeConnectionSkin(c);
            }
        }

        return result;
    }

    @Override
    public ObservableMap<String, Connections> getAllConnections() {
        return readOnlyObservableConnections;
    }

    //TODO unmodifiable connection object?
    @Override
    public Connections getConnections(String type) {
        Connections result = connections.get(type);

        if (result == null) {
            addConnections(VConnections.newConnections(type), type);
            result = connections.get(type);
        }

        return result;
    }

    @Override
    public VNode getSender(Connection c) {
        return getNodeLookup().getById(c.getSenderId());
    }

    @Override
    public VNode getReceiver(Connection c) {
        return getNodeLookup().getById(c.getReceiverId());
    }

    @Override
    public void setFlowNodeClass(Class<? extends VNode> cls) {
        try {
            Constructor constructor = cls.getConstructor(FlowModel.class);
            throw new IllegalArgumentException("constructor missing: (String, String)");
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(ConnectionsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.flowNodeClass = cls;
    }

    @Override
    public Class<? extends VNode> getFlowNodeClass() {
        return flowNodeClass;
    }

    VNode newNode(VNode result, ValueObject obj) {

        result.setValueObject(obj);

        if (getIdGenerator() == null) {
            throw new IllegalStateException("Please define an idgenerator before creating nodes!");
        }

        String id = getIdGenerator().newId();

        result.setId(id);

        nodes.put(id, result);
        observableNodes.add(result);

        return result;
    }

    @Override
    public void addConnections(Connections connections, String flowType) {
        this.connections.put(flowType, connections);
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the idGenerator
     */
    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    /**
     * @param idGenerator the idGenerator to set
     */
    @Override
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
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
}
