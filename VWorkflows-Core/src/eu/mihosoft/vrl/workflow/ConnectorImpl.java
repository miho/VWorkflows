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
class ConnectorImpl implements Connector {

    private VNode node;
    private String type;
    private String localId;
    private VisualizationRequest vRequest;
    private boolean input;
    private boolean output;
    private ObjectProperty<ValueObject> valueObjectProperty = new SimpleObjectProperty<>();

    public ConnectorImpl(VNode node, String type, String localId, boolean input) {
        this.type = type;
        this.localId = localId;
        this.node = node;
        this.input = input;
        this.output = !input;
        setValueObject(new DefaultConnectorValueObject(this));
    }
    
    public ConnectorImpl(VNode node, Connector c) {
        this(node, c.getType(), c.getLocalId(), c.isInput());
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getId() {
        return this.node.getId() + ":" + this.localId;
    }

    @Override
    public String getLocalId() {
        return this.localId;
    }

    @Override
    public void setLocalId(String id) {
        this.localId = id;
    }

    @Override
    public VNode getNode() {
        return this.node;
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        return this.vRequest;
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        this.vRequest = vReq;
    }

    /**
     * @return the input
     */
    @Override
    public boolean isInput() {
        return input;
    }

    /**
     * @return the output
     */
    @Override
    public boolean isOutput() {
        return output;
    }

    @Override
    public void setValueObject(ValueObject vObj) {
        valueObjectProperty().set(vObj);
    }

    @Override
    public ValueObject getValueObject() {
        return valueObjectProperty().get();
    }

    @Override
    public ObjectProperty<ValueObject> valueObjectProperty() {
        return this.valueObjectProperty;
    }
}
