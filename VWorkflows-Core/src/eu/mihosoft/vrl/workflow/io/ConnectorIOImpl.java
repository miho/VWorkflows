/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.io;

import eu.mihosoft.vrl.workflow.*;
import javafx.beans.property.ObjectProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ConnectorIOImpl implements Connector {

    private VNode node;
    private String type;
    private String localId;
    private VisualizationRequest vRequest;
    private boolean input;
    private boolean output;
    private ValueObject vObj;

    public ConnectorIOImpl(VNode node, String type, String localId, boolean input, boolean output) {
        this.type = type;
        this.localId = localId;
        this.node = node;
        this.input = input;
        this.output = output;
    }
    
    public ConnectorIOImpl(Connector c) {
        this(c.getNode(), c.getType(), c.getLocalId(), c.isInput(), c.isOutput());
        this.vObj = c.getValueObject();
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
    public void setValueObject(ValueObject obj) {
        this.vObj = obj;
    }

    @Override
    public ValueObject getValueObject() {
        return vObj;
    }

    @Override
    public ObjectProperty<ValueObject> valueObjectProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO NB-AUTOGEN
    }
}
