/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import eu.mihosoft.vrl.fxwindows.Window;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class JFXFlowNode extends FlowNodeBase<Window> {
    
    Window node;

    public JFXFlowNode(Window node) {
        this.node = node;
    }

    @Override
    public StringProperty titleProperty() {
        return titleProperty();
    }

    @Override
    public void setTitle(String title) {
        getNode().setTitle(title);
    }

    @Override
    public String getTitle() {
        return getNode().getTitle();
    }

    @Override
    public DoubleProperty xProperty() {
        return getNode().layoutXProperty();
    }

    @Override
    public DoubleProperty yProperty() {
        return getNode().layoutYProperty();
    }

    @Override
    public void setX(double x) {
        getNode().setLayoutX(x);
    }

    @Override
    public void setY(double y) {
        getNode().setLayoutY(y);
    }

    @Override
    public double getX() {
        return getNode().layoutXProperty().get();
    }

    @Override
    public double getY() {
        return getNode().layoutYProperty().get();
    }

    @Override
    public ObservableList<FlowNode<Window>> getChildren() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Window getNode() {
        return node;
    }

    @Override
    public StringProperty idProperty() {
        return getNode().idProperty();
    }

    @Override
    public void setId(String id) {
        getNode().setId(id);
    }

    @Override
    public String getId() {
        return getNode().getId();
    }
}
