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
package eu.mihosoft.vrl.workflow.impl;

import eu.mihosoft.vrl.workflow.CompatibilityResult;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.ConnectorValueObject;
import eu.mihosoft.vrl.workflow.ValueObject;
import eu.mihosoft.vrl.workflow.base.AbstractConnectorValueObject;

/**
 * This class defines a default connector value object. ValueObjects are used to
 * store data in connectors. It is a placeholder for a value object as it's
 * get/setValue methods are not implemented.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class DefaultConnectorValueObject extends AbstractConnectorValueObject {
    public DefaultConnectorValueObject(Connector connector) {
        super(connector);
    }

    @Override
    public CompatibilityResult compatible(final ValueObject sender, final String flowType) {
        boolean differentObjects = sender != this;

        boolean compatibleType = false;

        int numConnectionsOfReceiver = getParent().getFlow().
            getConnections(flowType).getAllWith(connector).size();

        boolean lessThanMaxNumberOfConnections = true;

        int maxNumConnections = connector.getMaxNumberOfConnections();

        if (sender instanceof ConnectorValueObject) {
            ConnectorValueObject senderConnectorVObj =
                (ConnectorValueObject) sender;
            compatibleType = getConnector().getType().
                equals(senderConnectorVObj.getConnector().getType())
                && getConnector().isInput() && senderConnectorVObj.
                getConnector().isOutput();

            int numConnectionsOfSender = senderConnectorVObj.getParent().
                getFlow().getConnections(flowType).
                getAllWith(senderConnectorVObj.getConnector()).size();

            maxNumConnections = Math.min(connector.getMaxNumberOfConnections(),
                senderConnectorVObj.getConnector().getMaxNumberOfConnections());

            lessThanMaxNumberOfConnections
                = numConnectionsOfReceiver < maxNumConnections
                && numConnectionsOfSender < maxNumConnections;
        }

        String message = "";
        if (!differentObjects) {
            message = "Connections can only established between different nodes."
                + " Sender node cannot be equal to receiver node.";
        } else if (!compatibleType) {
            message = "Connections can only established between"
                + " connectors of the same connection/flow type.";
        } else if (!lessThanMaxNumberOfConnections) {
            message = "Trying to create more than " + maxNumConnections
                + " number of connections is not allowed.";
        }

        final boolean compatible = differentObjects && compatibleType && lessThanMaxNumberOfConnections;
        final String errorMessage = message;
        String id = sender.getParent() + ":undefined";
        if (sender instanceof ConnectorValueObject) {
            id = ((ConnectorValueObject) sender).getConnector().getId();
        }
        final String senderId = id;

        return new CompatibilityResult() {
            @Override
            public boolean isCompatible() {
                return compatible;
            }

            @Override
            public String getMessage() {
                return "incompatible: " + senderId + " -> " + getConnector().getId() + ", reason: " + errorMessage;
            }

            @Override
            public String getStatus() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
}
