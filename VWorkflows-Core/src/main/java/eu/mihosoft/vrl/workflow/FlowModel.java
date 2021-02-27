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
import javafx.collections.ObservableMap;

/**
 * Defines an interface for
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
interface FlowModel extends Model, VisibleState {

    /**
     * Attempts to create the specified connection. This method never creates an
     * actual connection. It only checks whether the requested connection is
     * valid.
     *
     * @param s sender node (uses main output connector of this node if
     * specified)
     * @param r receiver node (uses main input connector of this node if
     * specified)
     * @param flowType connection type
     * @return connection result
     */
    public ConnectionResult tryConnect(VNode s, VNode r, String flowType);

    /**
     * Requests the specified connection. If the specified connection can be
     * established it will be created.
     *
     * @param s sender node
     * @param r receiver node
     * @param flowType connection type
     * @return connection result
     */
    public ConnectionResult connect(VNode s, VNode r, String flowType);

    /**
     * Attempts to create the specified connection. This method never creates an
     * actual connection. It only checks whether the requested connection is
     * valid.
     *
     * @param s sender connector
     * @param r receiver connector
     * @return connection result
     */
    public ConnectionResult tryConnect(Connector s, Connector r);

    /**
     * Requests the specified connection. If the specified connection can be
     * established it will be created.
     *
     * @param s sender node
     * @param r receiver node
     * @return connection result
     */
    public ConnectionResult connect(Connector s, Connector r);

    /**
     * Removes the specified node from this flow.
     *
     * @param n the node to remove
     * @return the removed node or <code>null</code> if no node has been removed
     */
    public VNode remove(VNode n);

    /**
     * Clears this flow, i.e., removes all nodes and connections.
     */
    public void clear();

    /**
     * Returns the nodes of this flow.
     *
     * @return nodes of this flow
     */
    public ObservableList<VNode> getNodes();

    /**
     * Returns the sender of the specified connection.
     *
     * @param c connection
     * @return the sender of the specified connection or <code>null</code> if
     * the node does not exist
     */
    public VNode getSender(Connection c);

    /**
     * Returns the receiver of the specified connection.
     *
     * @param c connection
     * @return the receiver of the specified connection or <code>null</code> if
     * the node does not exist
     */
    public VNode getReceiver(Connection c);

    /**
     * Adds the specified connections to this flow.
     * @param connections connections to add
     * @param flowType connection type
     */
    public void addConnections(Connections connections, String flowType);

    /**
     * Returns the all connections of the specified flow/connection type
     * @param flowType connection type
     * @return all connections of the specified flow/connection type
     */
    public Connections getConnections(String flowType);

    /**
     * Returns all connections of this flow.
     * @return all connections of this flow
     */
    public ObservableMap<String, Connections> getAllConnections();

    /**
     * Defines the flow node implementation class used by this flow model.
     * @param cls flow node implementation class
     */
    public void setFlowNodeClass(Class<? extends VNode> cls);

    /**
     * Returns the flow node implementation class used by this flow.
     * @return the flow node implementation class used by this flow
     */
    public Class<? extends VNode> getFlowNodeClass();

    /**
     * Defines the id generator that shall be used by this flow.
     * @param generator id generator
     */
    public void setIdGenerator(IdGenerator generator);

    /**
     * Returns the id generator used by this flow.
     * @return id generator
     */
    public IdGenerator getIdGenerator();

    /**
     * Defines the node lookup that shall be used by this flow.
     * @param nodeLookup node lookup
     */
    public void setNodeLookup(NodeLookup nodeLookup);

    /**
     * Returns the node lookup that is used by this flow.
     * @return node lookup
     */
    public NodeLookup getNodeLookup();

}
