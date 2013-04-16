/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ConnectorImpl implements Connector {
    
    private ObjectProperty<VisualizationRequest> vReq = new SimpleObjectProperty<>();
    private ObjectProperty<ConnectorValueObject> valObj = new SimpleObjectProperty<>();

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
    
}
