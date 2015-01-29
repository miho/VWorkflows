/*
 * InnerCanvas.java
 * 
 * Copyright 2012-2013 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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

import eu.mihosoft.vrl.workflow.VNode;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class InnerCanvas extends Pane {

    private BooleanProperty translateToMinNodePosProperty = new SimpleBooleanProperty(true);

    public InnerCanvas() {

        translateToMinNodePosProperty.addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                requestLayout();
            }
        });

//        needsLayoutProperty().addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                if (newValue) 
//                {
//                    List<VNode> nodeList = new ArrayList<>();
//
//                    double minX = Double.MAX_VALUE;
//                    double minY = Double.MAX_VALUE;
//
//                    // search minX and minY of window nodes
//                    for (Node n : getChildrenUnmodifiable()) {
//                        if (n instanceof FlowNodeWindow) {
//                            FlowNodeWindow w = (FlowNodeWindow) n;
//                            nodeList.add(w.nodeSkinProperty().get().getModel());
//
//                            minX = Math.min(n.getLayoutX(), minX);
//                            minY = Math.min(n.getLayoutY(), minY);
//                        }
//                    }
//
//                    // move windows
//                    for (VNode n : nodeList) {
//                        n.setX(n.getX() - minX);
//                        n.setY(n.getY() - minY);
//                    }
//                }
//            }
//        });
    }

    @Override
    protected void layoutChildren() {

        super.layoutChildren();

        setNeedsLayout(true);

        if (!translateToMinNodePosProperty.get()) {

            // search minX and minY of window nodes
            for (Node n : getChildrenUnmodifiable()) {
                if (n instanceof FlowNodeWindow) {
                    FlowNodeWindow w = (FlowNodeWindow) n;

                    VNode node = w.nodeSkinProperty().get().getModel();
                    node.setX(Math.max(0, node.getX()));
                    node.setY(Math.max(0, node.getY()));
                }
            }

            return;
        }

        List<VNode> nodeList = new ArrayList<>();

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        // search minX and minY of window nodes
        for (Node n : getChildrenUnmodifiable()) {
            if (n instanceof FlowNodeWindow) {
                FlowNodeWindow w = (FlowNodeWindow) n;

                VNode node = w.nodeSkinProperty().get().getModel();
                nodeList.add(node);

                minX = Math.min(node.getX(), minX);
                minY = Math.min(node.getY(), minY);
            }
        }

        // move windows
        for (VNode n : nodeList) {
            n.setX(n.getX() - minX);
            n.setY(n.getY() - minY);
        }
    }

    public BooleanProperty translateToMinNodePosProperty() {
        return translateToMinNodePosProperty;
    }
}
