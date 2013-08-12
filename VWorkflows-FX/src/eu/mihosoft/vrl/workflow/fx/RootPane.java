/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public class RootPane extends Pane {

    public RootPane() {
        setPrefWidth(USE_COMPUTED_SIZE);
        setPrefHeight(USE_COMPUTED_SIZE);
        
        setMinWidth(USE_COMPUTED_SIZE);
        setMinHeight(USE_COMPUTED_SIZE);
    }

    @Override
    protected void layoutChildren() {

//        System.out.println("root: layout " + System.currentTimeMillis());

        getParent().requestLayout();

        super.layoutChildren();
        for (Node n : getManagedChildren()) {
            if (n instanceof Region) {
                Region p = (Region) n;

                double width = Math.max(p.getMinWidth(), p.getPrefWidth());
                double height = Math.max(p.getMinHeight(), p.getPrefHeight());

                n.resize(width, height);

                double nX = Math.min(0, n.getLayoutX());
                double nY = Math.min(0, n.getLayoutY());

                n.relocate(nX, nY);
            }
        }
    }

    @Override
    protected double computeMinWidth(double h) {
//        double w = getInsets().getLeft() + getInsets().getRight();

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;

        for (Node n : getManagedChildren()) {
            minX = Math.min(minX, n.getBoundsInParent().getMinX());
            maxX = Math.max(maxX, n.getBoundsInParent().getMaxX());
        }
        
        return maxX;
    }

    @Override
    protected double computeMinHeight(double w) {
//        double h = getInsets().getLeft() + getInsets().getRight();

        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Node n : getManagedChildren()) {
            minY = Math.min(minY, n.getBoundsInParent().getMinY());
            maxY = Math.max(maxY, n.getBoundsInParent().getMaxY());
        }

        return maxY;
    }

    @Override
    protected double computePrefWidth(double h) {
        return computeMinWidth(h);
    }

    @Override
    protected double computePrefHeight(double w) {
        return computeMinHeight(w);
    }
}