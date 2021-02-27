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
import javafx.beans.property.SimpleObjectProperty;

/**
 * This class defines a default connector value object. ValueObjects are used to
 * store data in connectors. It is a placeholder for a value object as it's
 * get/setValue methods are not implemented.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class DefaultConnectorValueObject implements ValueObject {

    private transient VNode parent;
    private transient Connector c;
    private VisualizationRequest vReq;
    private final ObjectProperty<Object> valueProperty = new SimpleObjectProperty<>();

    private String errorMessage;

    public DefaultConnectorValueObject() {
    }

    public DefaultConnectorValueObject(Connector c) {
        this.c = c;
        this.parent = c.getNode();
    }

    @Override
    public VNode getParent() {
        return parent;
    }

    public Connector getConnector() {
        return c;
    }

    @Override
    public Object getValue() {
        return valueProperty().get();
    }

    @Override
    public void setValue(Object o) {
        valueProperty().set(o);
    }

    @Override
    public ObjectProperty<Object> valueProperty() {
        return this.valueProperty;
    }

    @Override
    public CompatibilityResult compatible(final ValueObject sender, final String flowType) {

        return new CompatibilityResult() {
            
            private boolean compatible;

            {
                compatible = computeCompatibility();
            }
            
            private boolean computeCompatibility() {
                //                System.out.println(" -> isCompatible: ");
                boolean differentObjects = sender != DefaultConnectorValueObject.this;

                boolean compatibleType = false;

                int numConnectionsOfReceiver = getParent().getFlow().
                        getConnections(flowType).getAllWith(c).size();

                boolean lessThanMaxNumberOfConnections = true;

                int maxNumConnections = c.getMaxNumberOfConnections();

                if (sender instanceof DefaultConnectorValueObject) {

                    DefaultConnectorValueObject senderConnectorVObj = 
                            (DefaultConnectorValueObject) sender;
                    compatibleType = getConnector().getType().
                            equals(senderConnectorVObj.getConnector().getType())
                            && getConnector().isInput() && senderConnectorVObj.
                                    getConnector().isOutput();

                    int numConnectionsOfSender = senderConnectorVObj.parent.
                            getFlow().getConnections(flowType).
                            getAllWith(senderConnectorVObj.c).size();

                    maxNumConnections = Math.min(c.getMaxNumberOfConnections(),
                            senderConnectorVObj.c.getMaxNumberOfConnections());

                    lessThanMaxNumberOfConnections
                            = numConnectionsOfReceiver < maxNumConnections
                            && numConnectionsOfSender < maxNumConnections;

                }

                if (!differentObjects) {
                    errorMessage = "Connections can only established between different nodes."
                            + " Sender node cannot be equal to receiver node.";
                } else if (!compatibleType) {
                    errorMessage = "Connections can only established between"
                            + " connectors of the same connection/flow type.";
                } else if (!lessThanMaxNumberOfConnections) {
                    errorMessage = "Trying to creating more than " + maxNumConnections
                            + " number of connections is not allowed.";
                }

                return differentObjects && compatibleType && lessThanMaxNumberOfConnections;
            }

            @Override
            public boolean isCompatible() {
                return compatible;
            }

            @Override
            public String getMessage() {

                String senderId = sender.getParent() + ":undefined";

                if (sender instanceof DefaultConnectorValueObject) {
                    senderId = ((DefaultConnectorValueObject) sender).getConnector().getId();
                }

                return "incompatible: " + senderId + " -> " + getConnector().getId() + ", reason: " + errorMessage;
            }

            @Override
            public String getStatus() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }; // end CompatibilityResult
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        return vReq;
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        this.vReq = vReq;
    }

    /**
     * @param parent the parent to set
     */
    @Override
    public void setParent(VNode parent) {
        this.parent = parent;
    }
}
