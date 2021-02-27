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

import javafx.collections.ObservableList;

import java.util.Collection;

/**
 * This interface defines a collection of {@code Connection}
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface Connections extends Model {

    /**
     * Adds a connection to this collection.
     *
     * @param c the connection to add
     */
    public void add(Connection c);

    /**
     * Adds the connection defined by the specified connectors.
     *
     * @param s sender connector
     * @param r receiver connector
     * @return connection that has been added
     */
    public Connection add(Connector s, Connector r);

    /**
     * Adds the connection defined by the specified connectors.
     *
     * @param id the id of the connection that shall be added
     * @param s the sender connector
     * @param r the receiver connector
     * @param vReq the visualization request of the connection
     * @return connection that has been added
     */
    public Connection add(String id, Connector s, Connector r, VisualizationRequest vReq);

    /**
     * Removes the specified connection from this collection
     *
     * @param c connection to remove
     */
    public void remove(Connection c);

    /**
     * Returns the specified connection.
     *
     * @param id id of the connection to be returned
     * @param s sender connector
     * @param r receiver connector
     * @return the requested connection or <code>null</code> if no such
     * connection exists
     */
    public Connection get(String id, Connector s, Connector r);

    /**
     * Returns all connections betwenn the specified connectors.
     *
     * @param s sender connector
     * @param r receiver connector
     * @return all connections betwenn the specified connectors
     */
    public Collection<Connection> getAll(Connector s, Connector r);

    /**
     * Removes the specified connection from this collection.
     *
     * @param id connection id
     * @param s sender connector
     * @param r receiver connector
     */
    public void remove(String id, Connector s, Connector r);

    /**
     * Removes all connections between the specified connectors from this
     * collection.
     *
     * @param s sender connector
     * @param r receiver connector
     */
    public void removeAll(Connector s, Connector r);

    /**
     * Defines the connection implementation class that shall be used.
     *
     * @param cls connection implementation class
     */
    public void setConnectionClass(Class<? extends Connection> cls);

    /**
     * Returns the connection implementation class.
     *
     * @return the connection implementation class
     */
    public Class<? extends Connection> getConnectionClass();

    /**
     * Returns the connections defined by this collection.
     *
     * @return the connections defined by this collection
     */
    public ObservableList<Connection> getConnections();

    /**
     * Returns all connections that are connected to the specified connector.
     *
     * @param c connector
     * @return all connections that are connected to the specified connector
     */
    public Collection<Connection> getAllWith(Connector c);

    /**
     * Returns all connections that are connected to the specified node.
     *
     * @param n node
     * @return all connections that are connected to the specified node
     */
    public Collection<Connection> getAllWithNode(VNode n);

    /**
     * Determines whether the specified input connector is connected.
     *
     * @param id connector id
     * @return <code>true</code> if the specified input connector is connected;
     * <code>false</code> otherwise
     */
    public boolean isInputConnected(Connector id);

    /**
     * Determines whether the specified output connector is connected.
     *
     * @param id connector id
     * @return <code>true</code> if the specified output connector is connected;
     * <code>false</code> otherwise
     */
    public boolean isOutputConnected(Connector id);

    /**
     * Determines if a connection exists between the specified connectors.
     *
     * @param s sender connector
     * @param r receiver connector
     * @return <code>true</code> if a connection between the specified
     * connectors exists; <code>false</code> otherwise
     */
    public boolean contains(Connector s, Connector r);

    /**
     * Returns the connection type of this collection.
     *
     * @return connection type (e.g. <code>"control"</code> or
     * <code>"data"</code>)
     */
    public String getType();
}
