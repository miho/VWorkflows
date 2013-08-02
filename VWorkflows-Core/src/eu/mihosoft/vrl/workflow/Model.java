/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public interface Model {
    
    public VisualizationRequest getVisualizationRequest();

    public void setVisualizationRequest(VisualizationRequest vReq);
    


//    public void setSkin(Skin<?> skin);
//
//    public Skin<?> getSkin();
//
//    public ObjectProperty<?> skinProperty();
}
