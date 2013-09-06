/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VCanvas extends ScalableContentPane {

    private BooleanProperty translateToMinNodePosProperty = new SimpleBooleanProperty(true);
    private InnerCanvas innerCanvas = new InnerCanvas();

    public VCanvas() {
        setContentPane(innerCanvas);
        getStyleClass().add("vflow-background");

    }

    public BooleanProperty translateToMinNodePosProperty() {

        if (!(getContentPane() instanceof InnerCanvas)) {
            throw new UnsupportedOperationException("Only supported for content panes of type InnerCanvas");
        }

        return ((InnerCanvas) getContentPane()).translateToMinNodePosProperty();
    }

    public void setTranslateToMinNodePos(boolean value) {
        if (!(getContentPane() instanceof InnerCanvas)) {
            throw new UnsupportedOperationException("Only supported for content panes of type InnerCanvas");
        }

        ((InnerCanvas) getContentPane()).translateToMinNodePosProperty().set(value);
    }

    public boolean getTranslateToMinNodePos() {
        if (!(getContentPane() instanceof InnerCanvas)) {
            throw new UnsupportedOperationException("Only supported for content panes of type InnerCanvas");
        }

        return ((InnerCanvas) getContentPane()).translateToMinNodePosProperty().get();
    }
}
