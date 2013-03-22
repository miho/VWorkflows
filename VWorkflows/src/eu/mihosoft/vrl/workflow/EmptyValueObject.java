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

    @Override
    public FlowNode getParent() {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public CompatibilityResult compatible(ValueObject other, String flowType) {
        return new CompatibilityResult() {
            @Override
            public boolean isCompatible() {
                return true;
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
}