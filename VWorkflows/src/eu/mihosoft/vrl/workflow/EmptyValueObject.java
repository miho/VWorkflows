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
public class EmptyValueObject implements ValueObject {

    private FlowNode parent;

    public EmptyValueObject() {
    }

    public EmptyValueObject(FlowNode parent) {
        this.parent = parent;
    }

    @Override
    public FlowNode getParent() {
        return parent;
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
                boolean differentObjects = sender != EmptyValueObject.this;
                boolean compatibleType = getParent().isInputOfType(flowType)
                        && sender.getParent().isOutputOfType(flowType);

                return differentObjects && compatibleType;
            }

            @Override
            public String getMessage() {
                throw new UnsupportedOperationException("Not supported yet.");
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
    public void setParent(FlowNode parent) {
        this.parent = parent;
    }
}