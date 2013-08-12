/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public class DefaultValueObject implements ValueObject {

    private transient VNode parent;
    private ObjectProperty valueProperty = new SimpleObjectProperty();

    public DefaultValueObject() {
    }

    public DefaultValueObject(VNode parent) {
        this.parent = parent;
    }

    @Override
    public VNode getParent() {
        return parent;
    }

    @Override
    public Object getValue() {
        return valueProperty().get();
    }

    @Override
    public void setValue(Object o) {
        this.valueProperty().set(o);
    }

    @Override
    public ObjectProperty<Object> valueProperty() {
        return valueProperty;
    }

    @Override
    public CompatibilityResult compatible(final ValueObject sender, final String flowType) {
        return new CompatibilityResult() {
            @Override
            public boolean isCompatible() {
                boolean differentObjects = sender != DefaultValueObject.this;
//                boolean compatibleType = getParent().isInputOfType(flowType)
//                        && sender.getParent().isOutputOfType(flowType);

                return differentObjects /*&& compatibleType*/;
            }

            @Override
            public String getMessage() {
                return "incompatible: " + sender.getParent().getId()  + " -> " +  getParent().getId();
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