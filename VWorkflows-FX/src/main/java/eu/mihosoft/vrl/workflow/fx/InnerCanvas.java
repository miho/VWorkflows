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

import eu.mihosoft.vrl.workflow.VNode;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class InnerCanvas extends Pane {

    private final BooleanProperty translateToMinNodePosProperty = new SimpleBooleanProperty(true);
    private final ObjectProperty<TranslateBehavior> translateBehavior = new SimpleObjectProperty<>(TranslateBehavior.ALWAYS);

    private double minX;
    private double minY;
    private boolean manualReset;

    public InnerCanvas() {

        translateToMinNodePosProperty.addListener((ov, oldV, newV) -> {
            requestLayout();
        });

        translateBehaviorProperty().addListener((ov, oldV, newV) -> requestLayout());

    }

    @Override
    protected void layoutChildren() {

        super.layoutChildren();

//        setNeedsLayout(true);
        if (!translateToMinNodePosProperty.get()) {

            // window coordinates < 0 are not allowed
            for (Node n : getChildrenUnmodifiable()) {
                if (n instanceof FlowNodeWindow && n.isManaged()) {

                    FlowNodeWindow w = (FlowNodeWindow) n;

                    VNode node = w.nodeSkinProperty().get().getModel();
                    node.setX(Math.max(0, node.getX()));
                    node.setY(Math.max(0, node.getY()));
                }
            }

            return;
        }

        List<VNode> nodeList = new ArrayList<>();

        minX = Double.MAX_VALUE;
        minY = Double.MAX_VALUE;

        // search minX and minY of window nodes
        for (Node n : getChildrenUnmodifiable()) {
            if (n instanceof FlowNodeWindow && n.isManaged()) {
                FlowNodeWindow w = (FlowNodeWindow) n;

                VNode node = w.nodeSkinProperty().get().getModel();
                nodeList.add(node);

                minX = Math.min(node.getX(), minX);
                minY = Math.min(node.getY(), minY);
            }
        }

        boolean partOfSceneGraph = false;

        try {
            javafx.stage.Window w = getScene().getWindow();

            partOfSceneGraph = w != null;
        } catch (Exception ex) {
            //
        }

        if (translateBehaviorProperty().get() == TranslateBehavior.ALWAYS
                || manualReset) {
            translateAllWindowsXY(minX, minY, nodeList);
        } else if (translateBehaviorProperty().get()
                == TranslateBehavior.IF_NECESSARY) {
            if (minX < 0 && getLayoutBounds().getWidth() > 0 && partOfSceneGraph) {
                translateAllWindowsX(minX, nodeList);
            }
            if (minY < 0 && getLayoutBounds().getHeight() > 0 && partOfSceneGraph) {
                translateAllWindowsY(minY, nodeList);
            }
        }

    }

    private void translateAllWindowsXY(double xOffset, double yOffset, List<VNode> nodeList) {
        // move windows
        for (VNode n : nodeList) {
            n.setX(n.getX() - xOffset);
            n.setY(n.getY() - yOffset);
        }
    }

    private void translateAllWindowsX(double xOffset, List<VNode> nodeList) {
        // move windows
        for (VNode n : nodeList) {
            n.setX(n.getX() - xOffset);
        }
    }

    private void translateAllWindowsY(double yOffset, List<VNode> nodeList) {
        // move windows
        for (VNode n : nodeList) {
            n.setY(n.getY() - yOffset);
        }
    }

    public BooleanProperty translateToMinNodePosProperty() {
        return translateToMinNodePosProperty;
    }

    public final ObjectProperty<TranslateBehavior> translateBehaviorProperty() {
        return translateBehavior;
    }

    void resetTranslation() {

        if (manualReset) {
            return;
        }

        manualReset = true;

        try {
            layoutChildren();
        } finally {
            manualReset = false;
        }
    }
}
