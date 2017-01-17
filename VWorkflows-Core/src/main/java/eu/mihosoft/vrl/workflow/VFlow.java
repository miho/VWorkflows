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
package eu.mihosoft.vrl.workflow;

import eu.mihosoft.vrl.workflow.skin.ConnectionSkin;
import eu.mihosoft.vrl.workflow.skin.FlowNodeSkinLookup;
import eu.mihosoft.vrl.workflow.skin.SkinFactory;
import eu.mihosoft.vrl.workflow.skin.VNodeSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The {@code VFlow} interface describes a workflow controller. A workflow is a
 * network of {@code VNode}s. {@code VNode}s are connected using
 * {@code Connector}s.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface VFlow {

    /**
     * Defines the model that shall be used by this flow controller.
     *
     * @param flow flow model
     */
    void setModel(VFlowModel flow);

    /**
     * Defines the node lookup that shall be used by this flow controller.
     *
     * @param nodeLookup
     */
    void setNodeLookup(NodeLookup nodeLookup);

    /**
     * Returns the node lookup that is used by this flow controller.
     *
     * @return
     */
    NodeLookup getNodeLookup();

    /**
     * Returns the model that is used by this flow controller.
     *
     * @return the model that is used by this flow controller
     */
    VFlowModel getModel();

    /**
     * Returns the property of the model that is used by this flow controller.
     *
     * @return the property of the model that is used by this flow controller
     */
    ObjectProperty modelProperty();

    /**
     * Attempts to create the specified connection. This method never creates an
     * actual connection. It only checks whether the requested connection is
     * valid.
     *
     * @param s sender connector
     * @param r receiver connector
     *
     * @return connection result
     */
    ConnectionResult tryConnect(Connector s, Connector r);

    /**
     * Requests the specified connection. If the specified connection can be
     * established it will be created.
     *
     * @param s sender node
     * @param r receiver node
     *
     * @return connection result
     */
    ConnectionResult connect(Connector s, Connector r);

    /**
     * Attempts to create the specified connection. This method never creates an
     * actual connection. It only checks whether the requested connection is
     * valid.
     *
     * @param s        sender node (uses main output connector of this node if
     *                 specified)
     * @param r        receiver node (uses main input connector of this node if
     *                 specified)
     * @param flowType connection type
     *
     * @return connection result
     */
    ConnectionResult tryConnect(VNode s, VNode r, String flowType);

    /**
     * Requests the specified connection. If the specified connection can be
     * established it will be created.
     *
     * @param s        sender node
     * @param r        receiver node
     * @param flowType connection type
     *
     * @return connection result
     */
    ConnectionResult connect(VNode s, VNode r, String flowType);

    ConnectionResult tryConnect(VFlow s, VNode r, String flowType);

    ConnectionResult tryConnect(VNode s, VFlow r, String flowType);

    ConnectionResult tryConnect(VFlow s, VFlow r, String flowType);

    ConnectionResult connect(VFlow s, VNode r, String flowType);

    ConnectionResult connect(VNode s, VFlow r, String flowType);

    ConnectionResult connect(VFlow s, VFlow r, String flowType);

    /**
     * Removes the specified node from this flow.
     *
     * @param n the node to remove
     *
     * @return the removed node or <code>null</code> if no node has been removed
     */
    VNode remove(VNode n);

    /**
     * Returns the nodes of this flow.
     *
     * @return nodes of this flow
     */
    ObservableList<VNode> getNodes();

    /**
     * Clears this flow, i.e., removes all nodes and connections.
     */
    void clear();

    /**
     * Returns the sender of the specified connection.
     *
     * @param c connection
     *
     * @return the sender of the specified connection or <code>null</code> if
     * the node does not exist
     */
    VNode getSender(Connection c);

    /**
     * Returns the receiver of the specified connection.
     *
     * @param c connection
     *
     * @return the receiver of the specified connection or <code>null</code> if
     * the node does not exist
     */
    VNode getReceiver(Connection c);

    /**
     * Adds the specified connections to this flow controller.
     *
     * @param connections connections to add
     * @param flowType    connection type
     */
    void addConnections(Connections connections, String flowType);

    /**
     * Returns the all connections of the specified flow/connection type
     *
     * @param flowType connection type
     *
     * @return all connections of the specified flow/connection type
     */
    Connections getConnections(String flowType);

    /**
     * Returns all connections of this flow controller.
     *
     * @return all connections of this flow controller
     */
    ObservableMap<String, Connections> getAllConnections();

    /**
     * Adds a new node to this flow.
     *
     * @param obj value object that shall be used for the requested node
     *
     * @return new node
     */
    VNode newNode(ValueObject obj);

    /**
     * Adds a new node to this flow.
     *
     * @return new node
     */
    VNode newNode();

    /**
     * Adds a new subflow to this flow.
     *
     * @param obj value object that shall be used for the requested subflow
     *
     * @return new subflow
     */
    VFlow newSubFlow(ValueObject obj);

    /**
     * Adds a new subflow to this flow.
     *
     * @return new subflow
     */
    VFlow newSubFlow();

    /**
     * Returns all direct subcontrollers of this flow controller. Subcontrollers
     * of subcontrollers won't be returned. Use the {@link #getFlowById(java.lang.String)
     * } method to accomplish this.
     *
     * @return all direct subcontrollers of this flow controller
     */
    Collection<VFlow> getSubControllers();

    /**
     * Defines the skin factories for this flow controller.
     *
     * @param skinFactories skin factories that shall be used by this flow
     *                      controller
     *                      <p>
     *                      Doesn't use Generics because generic arrays are not supported. GENERICS
     *                      ARE CRAPPY!
     *                      {@link http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6227971}
     */
    void setSkinFactories(SkinFactory... skinFactories);

    /**
     * Defines the skin factories for this flow controller.
     *
     * @param skinFactories skin factories that shall be used by this flow
     *                      controller
     */
    void setSkinFactories(Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> skinFactories);

    /**
     * Returns the skin factories that are used by this flow controller.
     *
     * @return the skin factories that are used by this flow controller
     */
    Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> getSkinFactories();

    /**
     * Adds the specified skin factories to this flow controller.
     *
     * @param skinFactories skin factories that shall be added to this flow
     *                      controller
     *                      <p>
     *                      Doesn't use Generics because generic arrays are not supported. GENERICS
     *                      ARE CRAPPY!
     *                      {@link http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6227971}
     */
    void addSkinFactories(SkinFactory... skinFactories);

    /**
     * Adds the specified skin factories to this flow controller.
     *
     * @param skinFactories skin factories that shall be added to this flow
     *                      controller
     */
    void addSkinFactories(Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> skinFactories);

    /**
     * Removes the specified skin factories from this flow controller.
     *
     * @param skinFactories skin factories to be removed
     */
    void removeSkinFactories(SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>... skinFactories);

    /**
     * Removes the specified skin factories from this flow controller.
     *
     * @param skinFactories skin factories to be removed
     */
    void removeSkinFactories(Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> skinFactories);

    /**
     * Defines the id generator that shall be used by this flow controller.
     *
     * @param generator id generator
     */
    void setIdGenerator(IdGenerator generator);

    /**
     * Returns the id generator used by this flow controller.
     *
     * @return id generator
     */
    IdGenerator getIdGenerator();


    List<VNodeSkin> getNodeSkinsById(String id);

    FlowNodeSkinLookup getNodeSkinLookup();

    void setNodeSkinLookup(FlowNodeSkinLookup skinLookup);

    void setVisible(boolean state);

    boolean isVisible();

    BooleanProperty visibleState();

    Connector addInput(String type);

    Connector addOutput(String type);

    /**
     * Returns child flow by id.
     *
     * @param id the id that specifies the requested flow
     *
     * @return the requested child flow or <code>null</code> if no such flow
     * exists
     */
    VFlow getFlowById(String id);

    VFlow getParent();

    ReadOnlyObjectProperty<VFlow> parentProperty();

    VFlow getRootFlow();

    ThruConnector addThruInput(String type);

    ThruConnector addThruOutput(String type);

    ObservableList<ThruConnector> getThruInputs();

    ObservableList<ThruConnector> getThruOutputs();

    Map<String, ConnectionSkin> getConnectionSkinMap(SkinFactory skinFactory);
}
