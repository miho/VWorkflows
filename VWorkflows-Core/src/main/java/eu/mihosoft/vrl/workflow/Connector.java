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

import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;

/**
 * This interface describes a connector. A connector is used to link nodes
 * together. It serves as the input or output for the node. Methods allow you to
 * detect whether the connector has been clicked or if it is used in a
 * connection.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface Connector extends Model {

    /**
     * Returns the conneciton type of this connector.
     *
     * @return connection type (e.g. <code>"control"</code> or
     * <code>"data"</code>)
     */
    public String getType();

    /**
     * Determines whether this connector is an input connector.
     *
     * @return <code>true</code> if this connector is an input connector;
     * <code>false</code> otherwise
     */
    public boolean isInput();

    /**
     * Determines whether this connector is an output connector.
     *
     * @return <code>true</code> if this connector is an output connector;
     * <code>false</code> otherwise
     */
    public boolean isOutput();

    /**
     * Returns the global id of this connector.
     *
     * @return global connector id
     */
    public String getId();

    /**
     * Returns the local id of this connector.
     *
     * @return local connector id
     */
    public String getLocalId();

    /**
     * Defines the local id of this connector.
     *
     * @param id the id to set
     */
    public void setLocalId(String id);

    /**
     * Returns the parent node of this connector.
     *
     * @return parent node of this connector
     */
    public VNode getNode();

    /**
     * Defines the value object of this connector.
     *
     * @param obj value object to set
     */
    public void setValueObject(ValueObject obj);

    /**
     * Returns the value object of this connector.
     *
     * @return value object or <code>null</code> if no value object has been
     * defined
     */
    public ValueObject getValueObject();

    /**
     * Returns the value object property (can be used to get notified if the value object changes).
     * @return value object property
     */
    public ObjectProperty<ValueObject> valueObjectProperty();

    /**
     * Adds the specified connection-event listener to this connector.
     * 
     * @param handler the listener to add
     */
    public void addConnectionEventListener(EventHandler<ConnectionEvent> handler);

    /**
     * Removes the specified connection-event listener from this connector.
     * @param handler the listener to remove
     */
    public void removeConnectionEventListener(EventHandler<ConnectionEvent> handler);

    /**
     * Adds the specified click-event listener to this connector.
     * @param handler the listener to add
     */
    public void addClickEventListener(EventHandler<ClickEvent> handler);

    /**
     * Removes the specified click-event listener from this connector.
     * @param handler the listener to remove
     */
    public void removeClickEventListener(EventHandler<ClickEvent> handler);

    /**
     * Perform a mouse click on this connector.
     * @param btn the click button
     * @param event the event (e.g. javafx mouse-event)
     */
    public void click(MouseButton btn, Object event);
    
    /**
     * Defines the maximum allowed number of connections.
     * @param numConnections maximum allowed number of connections ({@code [0,MAX_INT]})
     */
    public void setMaxNumberOfConnections(int numConnections);
    
    /**
     * Returns the maximum allowed number of connections. 
     * @return maximum allowed number of connections ({@code [0,MAX_INT]})
     */
    public int getMaxNumberOfConnections();
    
    /**
     * Returns the property of the maximum allowed number of connections. 
     * @return maximum allowed number of connections ({@code [0,MAX_INT]})
     */
    public ObjectProperty<Integer> maxNumberOfConnectionsProperty();
}
