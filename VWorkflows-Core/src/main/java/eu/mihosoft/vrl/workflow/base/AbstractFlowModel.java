/*
 * Copyright 2012-2017 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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
package eu.mihosoft.vrl.workflow.base;

import eu.mihosoft.vrl.workflow.CompatibilityResult;
import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowModel;
import eu.mihosoft.vrl.workflow.IdGenerator;
import eu.mihosoft.vrl.workflow.NodeLookup;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.ValueObject;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import eu.mihosoft.vrl.workflow.impl.NoDefaultConnectorValueObject;
import eu.mihosoft.vrl.workflow.util.VConnections;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the flow model. The flow model manages the connections
 * between nodes.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public abstract class AbstractFlowModel implements FlowModel {
    protected final ObservableMap<String, Connections> connections = FXCollections.observableHashMap();
    protected final ObservableMap<String, Connections> readOnlyObservableConnections = FXCollections.unmodifiableObservableMap(connections);
    protected final ObservableList<VNode> observableNodes = FXCollections.observableArrayList();
    protected final ObservableList<VNode> readOnlyObservableNodes = FXCollections.unmodifiableObservableList(observableNodes);
    protected final Map<String, VNode> nodes = new HashMap<>();
    protected final BooleanProperty visibleProperty = new SimpleBooleanProperty(this, "visible");

    protected IdGenerator idGenerator;
    protected NodeLookup nodeLookup;

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

        return instantiateConnectionResult(receiverValObj.compatible(senderValObj, type), null);
    }

    @Override
    public ConnectionResult connect(VNode s, VNode r, String type) {
        ConnectionResult result = tryConnect(s, r, type);

        if (!result.getStatus().isCompatible()) {
            return result;
        }

        Connector sender = null;
        Connector receiver = null;

        if (s.getMainOutput(type) != null) {
            sender = s.getMainOutput(type);
        }

        if (r.getMainInput(type) != null) {
            receiver = r.getMainInput(type);
        }

        return instantiateConnectionResult(result.getStatus(), getConnections(type).add(sender, receiver));
    }

    @Override
    public ConnectionResult tryConnect(Connector s, Connector r) {
        CompatibilityResult result = r.getValueObject().compatible(s.getValueObject(), s.getType());
        return instantiateConnectionResult(result, null);
    }

    @Override
    public ConnectionResult connect(Connector s, Connector r) {
        ConnectionResult result = tryConnect(s, r);

        if (!result.getStatus().isCompatible()) {
            return result;
        }

        return instantiateConnectionResult(result.getStatus(), getConnections(s.getType()).add(s, r));
    }

    protected abstract ConnectionResult instantiateConnectionResult(CompatibilityResult compatibilityResult, Connection connection);

    @Override
    public ObservableList<VNode> getNodes() {
        return readOnlyObservableNodes;
    }

    @Override
    public void clear() {
        for (VNode n : new ArrayList<>(observableNodes)) {
            remove(n);
        }
    }

    @Override
    public VNode remove(VNode n) {
        VNode result = nodes.remove(n.getId());
        observableNodes.remove(n);

        for (Connections cns : getAllConnections().values()) {
            for (Connection c : cns.getAllWithNode(n)) {
                cns.remove(c);
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
        return getNodeLookup().getById(c.getSender().getId());
    }

    @Override
    public VNode getReceiver(Connection c) {
        return getNodeLookup().getById(c.getReceiver().getId());
    }

    @Override
    public VNode registerNode(VNode node, ValueObject obj, String parentId) {
        node.setValueObject(obj);

        if (getIdGenerator() == null) {
            throw new IllegalStateException("Please define an idgenerator before creating nodes!");
        }

        String id = getIdGenerator().newId(parentId + ":");

        node.setId(id);
        nodes.put(id, node);
        // should check if node is not inside observableNodes?
        observableNodes.add(node);

        return node;
    }

    @Override
    public void addConnections(Connections connections, String flowType) {
        this.connections.put(flowType, connections);
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    @Override
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public NodeLookup getNodeLookup() {
        return nodeLookup;
    }

    @Override
    public void setNodeLookup(NodeLookup nodeLookup) {
        this.nodeLookup = nodeLookup;
    }

    @Override
    public ReadOnlyProperty<VisualizationRequest> visualizationRequestProperty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isVisualizationRequestInitialized() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
