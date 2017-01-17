/*
 * Copyright 2012-2017 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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
package eu.mihosoft.vrl.workflow.fx.impl;

import eu.mihosoft.vrl.workflow.fx.OptimizationRule;
import eu.mihosoft.vrl.workflow.fx.scene.layout.OptimizableContentPane;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.scene.transform.Transform;

/**
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class DefaultOptimizationRule implements OptimizationRule {

    private final DoubleProperty minSceneArea = new SimpleDoubleProperty(2000);
    private final DoubleProperty minSceneDimension = new SimpleDoubleProperty(50);

    @Override
    public boolean visible(OptimizableContentPane p, Transform t) {

        Bounds bounds = p.getBoundsInLocal();

        // if bounds are infinite we assume visibility
        if (Double.isInfinite(bounds.getWidth())
            || Double.isInfinite(bounds.getHeight())) {
            return true;
        }

        bounds = p.localToScene(bounds);

        // if bounds are NaN we assume visibility
        if (Double.isNaN(bounds.getWidth())
            || Double.isNaN(bounds.getHeight())) {
            return true;
        }

        boolean visible = getMinSceneArea() <= bounds.getWidth() * bounds.getHeight();

        if (visible) {
            visible = Math.min(bounds.getWidth(), bounds.getHeight()) > getMinSceneDimension();
        }

        p.layout();

        return visible;
    }

    @Override
    public boolean attached(OptimizableContentPane p, Transform t) {
        return visible(p, t);
    }

    public DoubleProperty minSceneAreaProperty() {
        return minSceneArea;
    }

    public void setMinSceneArea(double s) {
        minSceneArea.set(s);
    }

    public double getMinSceneArea() {
        return minSceneArea.get();
    }

    public DoubleProperty minSceneDimensionProperty() {
        return minSceneDimension;
    }

    public void setMinSceneDimension(double s) {
        minSceneDimension.set(s);
    }

    public double getMinSceneDimension() {
        return minSceneDimension.get();
    }
}