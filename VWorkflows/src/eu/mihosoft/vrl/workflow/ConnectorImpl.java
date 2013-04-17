/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ConnectorImpl implements Connector {
    
    private ObjectProperty<FlowNode> parentProperty = new SimpleObjectProperty<>();
    private ObjectProperty<VisualizationRequest> vReq = new SimpleObjectProperty<>();
    private ObjectProperty<ConnectorValueObject> valObj = new SimpleObjectProperty<>();
    private StringProperty idProperty = new SimpleStringProperty();

    @Override
    public void setValueObject(ConnectorValueObject obj) {
        this.valObj.set(obj);
    }

    @Override
    public ConnectorValueObject getValueObject() {
        return this.valObj.get();
    }

    @Override
    public ObjectProperty<ConnectorValueObject> valueObjectProperty() {
        return this.valObj;
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        return this.vReq.get();
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        this.vReq.set(vReq);
    }

    @Override
    public ObjectProperty<VisualizationRequest> visualizationRequestProperty() {
        return this.vReq;
    }

    /**
     * @return the idProperty
     */
    @Override
    public StringProperty idProperty() {
        return idProperty;
    }

    @Override
    public void setId(String id) {
        this.idProperty.set(id);
    }

    @Override
    public String getId() {
        return this.idProperty.get();
    }

    @Override
    public void setParent(FlowNode parent) {
        this.parentProperty.set(parent);
    }

    @Override
    public FlowNode getParent() {
        return this.parentProperty.get();
    }

    @Override
    public ObjectProperty<FlowNode> parentProperty() {
        return this.parentProperty;
    }

    @Override
    public String getGlobalId() {
        return ConnectorUtil.globalId(this);
    }
    
    
    
}
