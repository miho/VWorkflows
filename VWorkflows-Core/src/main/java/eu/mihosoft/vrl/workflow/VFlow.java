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
    public void setModel(VFlowModel flow);

    /**
     * Defines the node lookup that shall be used by this flow controller.
     *
     * @param nodeLookup
     */
    public void setNodeLookup(NodeLookup nodeLookup);

    /**
     * Returns the node lookup that is used by this flow controller.
     *
     * @return
     */
    public NodeLookup getNodeLookup();

    /**
     * Returns the model that is used by this flow controller.
     *
     * @return the model that is used by this flow controller
     */
    public VFlowModel getModel();

    /**
     * Returns the property of the model that is used by this flow controller.
     *
     * @return the property of the model that is used by this flow controller
     */
    public ObjectProperty modelProperty();

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

    public ConnectionResult tryConnect(VFlow s, VNode r, String flowType);

    public ConnectionResult tryConnect(VNode s, VFlow r, String flowType);

    public ConnectionResult tryConnect(VFlow s, VFlow r, String flowType);

    public ConnectionResult connect(VFlow s, VNode r, String flowType);

    public ConnectionResult connect(VNode s, VFlow r, String flowType);

    public ConnectionResult connect(VFlow s, VFlow r, String flowType);

    /**
     * Removes the specified node from this flow.
     *
     * @param n the node to remove
     * @return the removed node or <code>null</code> if no node has been removed
     */
    public VNode remove(VNode n);

    /**
     * Returns the nodes of this flow.
     *
     * @return nodes of this flow
     */
    public ObservableList<VNode> getNodes();

    /**
     * Clears this flow, i.e., removes all nodes and connections.
     */
    public void clear();

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
     * Adds the specified connections to this flow controller.
     *
     * @param connections connections to add
     * @param flowType connection type
     */
    public void addConnections(Connections connections, String flowType);

    /**
     * Returns the all connections of the specified flow/connection type
     *
     * @param flowType connection type
     * @return all connections of the specified flow/connection type
     */
    public Connections getConnections(String flowType);

    /**
     * Returns all connections of this flow controller.
     *
     * @return all connections of this flow controller
     */
    public ObservableMap<String, Connections> getAllConnections();

    /**
     * Defines the flow node implementation class used by this flow controller.
     *
     * @param cls flow node implementation class
     */
    public void setFlowNodeClass(Class<? extends VNode> cls);

    /**
     * Returns the flow node implementation class used by this flow controller.
     *
     * @return the flow node implementation class used by this flow controller
     */
    public Class<? extends VNode> getFlowNodeClass();

    /**
     * Adds a new node to this flow.
     *
     * @param obj value object that shall be used for the requested node
     * @return new node
     */
    public VNode newNode(ValueObject obj);

    /**
     * Adds a new node to this flow.
     *
     * @return new node
     */
    public VNode newNode();

    /**
     * Adds a new subflow to this flow.
     *
     * @param obj value object that shall be used for the requested subflow
     * @return new subflow
     */
    public VFlow newSubFlow(ValueObject obj);

    /**
     * Adds a new subflow to this flow.
     *
     * @return new subflow
     */
    public VFlow newSubFlow();

    /**
     * Returns all direct subcontrollers of this flow controller. Subcontrollers
     * of subcontrollers won't be returned. Use the {@link #getFlowById(java.lang.String)
     * } method to accomplish this.
     *
     * @return all direct subcontrollers of this flow controller
     */
    public Collection<VFlow> getSubControllers();

    /**
     * Defines the skin factories for this flow controller.
     *
     * @param skinFactories skin factories that shall be used by this flow
     * controller
     *
     * Doesn't use Generics because generic arrays are not supported. GENERICS
     * ARE CRAPPY!
     * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6227971">JDK-6227971</a>
     *
     */
    public void setSkinFactories(SkinFactory... skinFactories);

    /**
     * Defines the skin factories for this flow controller.
     *
     * @param skinFactories skin factories that shall be used by this flow
     * controller
     */
    public void setSkinFactories(Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> skinFactories);

    /**
     * Returns the skin factories that are used by this flow controller.
     *
     * @return the skin factories that are used by this flow controller
     */
    public Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> getSkinFactories();

    /**
     * Adds the specified skin factories to this flow controller.
     *
     * @param skinFactories skin factories that shall be added to this flow
     * controller
     *
     * Doesn't use Generics because generic arrays are not supported. GENERICS
     * ARE CRAPPY!
     * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6227971">JDK-6227971</a>
     *
     */
    public void addSkinFactories(SkinFactory... skinFactories);

    /**
     * Adds the specified skin factories to this flow controller.
     *
     * @param skinFactories skin factories that shall be added to this flow
     * controller
     */
    public void addSkinFactories(Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> skinFactories);

    /**
     * Removes the specified skin factories from this flow controller.
     *
     * @param skinFactories skin factories to be removed
     */
    public void removeSkinFactories(SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>... skinFactories);

    /**
     * Removes the specified skin factories from this flow controller.
     *
     * @param skinFactories skin factories to be removed
     */
    public void removeSkinFactories(Collection<SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin>> skinFactories);

    /**
     * Defines the id generator that shall be used by this flow controller.
     *
     * @param generator id generator
     */
    public void setIdGenerator(IdGenerator generator);

    /**
     * Returns the id generator used by this flow controller.
     *
     * @return id generator
     */
    public IdGenerator getIdGenerator();


    public List<VNodeSkin> getNodeSkinsById(String id);

    public FlowNodeSkinLookup getNodeSkinLookup();

    public void setNodeSkinLookup(FlowNodeSkinLookup skinLookup);

    public void setVisible(boolean state);

    public boolean isVisible();

    public BooleanProperty visibleState();

    Connector addInput(String type);

    Connector addOutput(String type);

    /**
     * Returns child flow by id.
     *
     * @param id the id that specifies the requested flow
     * @return the requested child flow or <code>null</code> if no such flow
     * exists
     */
    public VFlow getFlowById(String id);

    VFlow getParent();

    ReadOnlyObjectProperty<VFlow> parentProperty();

    VFlow getRootFlow();

    public ThruConnector addThruInput(String type);

    public ThruConnector addThruOutput(String type);

    public ObservableList<ThruConnector> getThruInputs();

    public ObservableList<ThruConnector> getThruOutputs();
}
