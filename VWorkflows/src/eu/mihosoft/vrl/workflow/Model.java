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
public interface Model {
    
    public VisualizationRequest getVisualizationRequest();

    public void setVisualizationRequest(VisualizationRequest vReq);
    
    public ObjectProperty<VisualizationRequest> visualizationRequestProperty();

}
