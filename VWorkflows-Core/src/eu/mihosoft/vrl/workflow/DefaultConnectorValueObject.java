/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.ObjectProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
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