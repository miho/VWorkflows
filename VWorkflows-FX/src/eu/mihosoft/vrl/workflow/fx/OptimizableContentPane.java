/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import java.util.ArrayList;
import java.util.Collection;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Transform;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class OptimizableContentPane extends StackPane {

    private OptimizationRule optimizationRule;
    private Transform transform;
    private boolean optimizing = false;
    private boolean visibility = true;
    private boolean attached = true;
    private Collection<Node> detatched = new ArrayList<>();

    public OptimizableContentPane() {
        this.optimizationRule = new DefaultOptimizationRuleImpl();

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

    public void requestOptimization() {
        updateOptimizationRule();
    }

    private synchronized void updateOptimizationRule() {

        if (!visibility) {
            return;
        }

        // TODO why does synchronized not work here!
        if (optimizing) {
            return;
        }

        optimizing = true;

//        if (transform == null) {
        transform = OptimizableContentPane.this.localToSceneTransformProperty().get();
//        }

        boolean visible = optimizationRule.visible(this, transform);

        if (isVisible() != visible) {
            setVisible(visible);
        }

        boolean attachedReq = optimizationRule.attached(this, transform);

        if (attached != attachedReq) {
            if (attachedReq) {
                getChildren().addAll(detatched);
                detatched.clear();
                
//                for (Node n : getChildren()) {
//                    if (n instanceof Parent) {
//                        Parent p = (Parent) n;
//                        p.layout();
//                    }
//                }


            } else {
                detatched.addAll(getChildren());
                getChildren().removeAll(detatched);
            }
            attached = attachedReq;
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

class DefaultOptimizationRuleImpl implements OptimizationRule {

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