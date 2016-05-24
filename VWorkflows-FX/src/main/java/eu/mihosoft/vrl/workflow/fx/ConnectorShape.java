package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connector;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;

/**
 * Represents a connector "shape" without specifying a particular path.
 *
 * @author Andres Almiray
 */
public interface ConnectorShape {
    String CONNECTOR_SHAPE_CLASS = ConnectorShape.class.getName();

    /**
     * @return the connector
     */
    Connector getConnector();

    /**
     * @param connector the connector to set
     */
    void setConnector(Connector connector);

    /**
     * Moves the shape to the front of the visual area.
     */
    void toFront();

    /**
     * @return the radiusProperty
     */
    DoubleProperty radiusProperty();

    /**
     * Set the radius of the connector shape.
     *
     * @param radius the radius to use. Should be > 0.
     */
    void setRadius(double radius);

    double getRadius();

    /**
     * Returns the real JavaFX Node that represents the visual state of this connector shape.
     *
     * @return the backing node for this connector shape
     */
    Node getNode();
}
