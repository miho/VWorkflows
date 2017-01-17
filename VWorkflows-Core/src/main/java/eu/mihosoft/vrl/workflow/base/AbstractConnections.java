/*
 * Copyright 2012-2016 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import eu.mihosoft.vrl.workflow.impl.DefaultVisualizationRequest;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static eu.mihosoft.vrl.workflow.util.ConnectionUtils.connectionId;

/**
 * This class provides a default implementation of the {@code Connections}
 * interface.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public abstract class AbstractConnections implements Connections {
    protected final String type;
    protected final Map<String, Connection> connections = new HashMap<>();
    protected final ObservableList<Connection> observableConnections = FXCollections.observableArrayList();

    protected ObjectProperty<VisualizationRequest> visualizationRequest;

    public AbstractConnections(String type) {
        this.type = type;
    }

    @Override
    public void add(Connection c) {
        checkUniqueness(c);

        connections.put(connectionId(c), c);
        observableConnections.add(c);
    }

    protected void checkUniqueness(Connection c) {
        if (connections.containsKey(connectionId(c))) {
            throw new IllegalStateException("Cannot add connection: a connection with equal id already added!");
        }
    }

    @Override
    public Connection add(Connector s, Connector r) {
        // search id:
        String id = "0";
        int count = 0;

        while (connections.containsKey(connectionId(id, s.getId(), r.getId()))) {
            count++;
            id = "" + count;
        }

        Connection c = createConnection(id, s, r);

        add(c);

        return c;
    }

    @Override
    public Connection add(String id, Connector s, Connector r, VisualizationRequest vReq) {
        Connection c = createConnection(id, s, r);
        c.setVisualizationRequest(vReq);
        add(c);
        return c;
    }

    @Override
    public void remove(Connection c) {
        connections.remove(connectionId(c));
        observableConnections.remove(c);
    }

    @Override
    public Connection get(String id, Connector s, Connector r) {
        return connections.get(connectionId(id, s.getId(), r.getId()));
    }

    @Override
    public void remove(String id, Connector s, Connector r) {
        observableConnections.remove(get(id, s, r));
        connections.remove(connectionId(id, s.getId(), r.getId()));
    }

    protected Connection createConnection(String id, Connector s, Connector r) {
        if (s == null) {
            throw new IllegalArgumentException("Sender must not be null.");
        }

        if (r == null) {
            throw new IllegalArgumentException("Receiver must not be null.");
        }

        return instantiateConnection(id, s, r);
    }

    protected abstract Connection instantiateConnection(String id, Connector sender, Connector receiver);

    @Override
    public ObservableList<Connection> getConnections() {
        return observableConnections;
    }

    @Override
    public Collection<Connection> getAllWith(Connector c) {

        Collection<Connection> result = new ArrayList<>();

        for (Connection conn : getConnections()) {
            if (conn.getSender().getId().equals(c.getId())
                || conn.getReceiver().getId().equals(c.getId())) {
                result.add(conn);
            }
        }

        return result;
    }

    @Override
    public Collection<Connection> getAllWithNode(VNode n) {
        Collection<Connection> result = new ArrayList<>();

        for (Connection conn : getConnections()) {
            // TODO: reference check (==) ok? 20.11.2013
            if (conn.getSender().getNode() == n || conn.getReceiver().getNode() == n) {
                result.add(conn);
            }
        }

        return result;
    }

    @Override
    public Collection<Connection> getAll(Connector s, Connector r) {
        Collection<Connection> result = new ArrayList<>();

        for (Connection c : connections.values()) {
            if (c.getSender().getId().equals(s.getId())
                && c.getReceiver().getId().equals(r.getId())) {
                result.add(c);
            }
        }

        return result;
    }

    @Override
    public void removeAll(Connector s, Connector r) {
        Collection<Connection> delList = new ArrayList<>();

        for (Connection c : connections.values()) {
            if (c.getSender().getId().equals(s.getId())
                && c.getReceiver().getId().equals(r.getId())) {
                delList.add(c);
            }
        }

        for (Connection connection : delList) {
            remove(connection);
        }
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        return visualizationRequestProperty().getValue();
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest visualizationRequest) {
        writableVisualizationRequestProperty().set(visualizationRequest);
    }

    protected ObjectProperty<VisualizationRequest> writableVisualizationRequestProperty() {
        if (visualizationRequest == null) {
            visualizationRequest = new SimpleObjectProperty<>(this, "visualizationRequest", new DefaultVisualizationRequest());
        }

        return visualizationRequest;
    }

    @Override
    public ReadOnlyProperty<VisualizationRequest> visualizationRequestProperty() {
        return writableVisualizationRequestProperty();
    }

    @Override
    public boolean isInputConnected(Connector input) {
        Collection<Connection> connectionsWith = getAllWith(input);

        for (Connection connection : connectionsWith) {
            if (connection.getReceiver().getId().equals(input.getId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isOutputConnected(Connector output) {
        Collection<Connection> connectionsWith = getAllWith(output);

        for (Connection connection : connectionsWith) {
            if (connection.getSender().getId().equals(output.getId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean contains(Connector s, Connector r) {
        return getAll(s, r).iterator().hasNext();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("[ type: ").append(getType()).append('\n');

        for (Connection connection : observableConnections) {
            result.append(" --> ").append(connection.toString()).append('\n');
        }

        result.append("]");

        return result.toString();
    }

    @Override
    public boolean isVisualizationRequestInitialized() {
        return visualizationRequest != null;
    }
}
