/*
 * Copyright 2012-2021 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * Please cite the following publication(s):
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
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