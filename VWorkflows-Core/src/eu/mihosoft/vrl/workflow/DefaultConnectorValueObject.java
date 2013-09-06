/*
 * DefaultConnectorValueObject.java
 * 
 * Copyright 2012-2013 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public class DefaultConnectorValueObject implements ValueObject {

    private transient VNode parent;
    private transient Connector c;

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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setValue(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ObjectProperty<Object> valueProperty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CompatibilityResult compatible(final ValueObject sender, final String flowType) {
        return new CompatibilityResult() {
            @Override
            public boolean isCompatible() {
//                System.out.println(" -> isCompatible: ");
                boolean differentObjects = sender != DefaultConnectorValueObject.this;

                boolean compatibleType = false;

                if (sender instanceof DefaultConnectorValueObject) {
                    DefaultConnectorValueObject senderConnectorVObj = (DefaultConnectorValueObject) sender;
                    compatibleType = getConnector().getType().equals(senderConnectorVObj.getConnector().getType())
                            && getConnector().isInput() && senderConnectorVObj.getConnector().isOutput();
                }

//                System.out.println("differentObj: " + differentObjects + ", compatibleTypes " + compatibleType);
                
                return differentObjects && compatibleType;
            }

            @Override
            public String getMessage() {
                
                String senderId = sender.getParent() + ":undefined";
                
                if (sender instanceof DefaultConnectorValueObject) {
                    senderId = ((DefaultConnectorValueObject)sender).getConnector().getId();
                }
                
                return "incompatible: " + senderId + " -> " + getConnector().getId();
            }

            @Override
            public String getStatus() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        return new VisualizationRequest() {
            @Override
            public String getStyle() {
                return "default";
            }

            @Override
            public String getOptions() {
                return "";
            }
        };
    }

    /**
     * @param parent the parent to set
     */
    @Override
    public void setParent(VNode parent) {
        this.parent = parent;
    }
}