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
package eu.mihosoft.vrl.workflow.fx.scene.layout;

import eu.mihosoft.vrl.workflow.fx.OptimizationRule;
import eu.mihosoft.vrl.workflow.fx.impl.DefaultOptimizationRule;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class OptimizableContentPane extends StackPane {

    private OptimizationRule optimizationRule;
    private Transform transform;
    private boolean optimizing = false;
    private boolean visibility = true;
    private boolean attached = true;
    private final Collection<Node> detatched = new ArrayList<>();

    public OptimizableContentPane() {
        this.optimizationRule = new DefaultOptimizationRule();

        localToSceneTransformProperty().addListener((ov, oldValue, newValue) -> {
            transform = newValue;
            updateOptimizationRule();
        });

        boundsInLocalProperty().addListener((ov, oldValue, newValue) -> {
            updateOptimizationRule();
        });

        visibleProperty().addListener((ov, oldValue, newValue) -> {
            if (!optimizing) {
                visibility = newValue;
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
        transform = OptimizableContentPane.this.
            localToSceneTransformProperty().get();
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