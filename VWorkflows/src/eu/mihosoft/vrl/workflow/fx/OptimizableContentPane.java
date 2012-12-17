/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Transform;


/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class OptimizableContentPane extends StackPane {

    private OptimizationRule optimizationRule;
    private Transform transform;
    private boolean optimizing = false;
    private boolean visibility = true;

    public OptimizableContentPane() {
        this.optimizationRule = new OptimizationRuleImpl();

        localToSceneTransformProperty().addListener(new ChangeListener<Transform>() {
            @Override
            public void changed(ObservableValue<? extends Transform> ov, Transform oldVal, Transform newVal) {
                transform = newVal;
                updateOptimizationRule();
            }
        });

        boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds t, Bounds t1) {
                updateOptimizationRule();
            }
        });

        visibleProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                if (!optimizing) {
                    visibility = newValue;
                }
            }
        });
    }

    private synchronized void updateOptimizationRule() {

        if (!visibility) {
            return;
        }

        optimizing = true;

        if (transform == null) {
            transform = OptimizableContentPane.this.localToSceneTransformProperty().get();
        }

        boolean visible = optimizationRule.visible(this, transform);

        if (isVisible() != visible) {
            setVisible(visible);
        }

        optimizing = false;
    }

    /**
     * @return the optimizationRule
     */
    public OptimizationRule getOptimizationRule() {
        return optimizationRule;
    }

    /**
     * @param optimizationRule the optimizationRule to set
     */
    public void setOptimizationRule(OptimizationRule optimizationRule) {
        this.optimizationRule = optimizationRule;
    }
}
class OptimizationRuleImpl implements OptimizationRule {

    private DoubleProperty minSceneArea = new SimpleDoubleProperty(200);

    @Override
    public boolean visible(OptimizableContentPane p, Transform t) {

        Bounds bounds = p.getBoundsInLocal();
        bounds = p.localToScene(bounds);

        return getMinSceneArea() <= bounds.getWidth() * bounds.getHeight();
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
}