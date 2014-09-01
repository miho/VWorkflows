/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.Optional;
import javafx.collections.MapChangeListener;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class VisualizationRequestImpl extends PropertyStorageImpl implements VisualizationRequest {

    @Override
    public void setStyle(String style) {

        if (style == null) {
            style = "";
        }

        set(VisualizationRequest.KEY_STYLE, style);
    }

    @Override
    public String getStyle() {
        return get(VisualizationRequest.KEY_STYLE).get().toString();
    }

}
