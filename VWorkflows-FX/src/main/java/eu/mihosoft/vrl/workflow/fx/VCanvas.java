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

import javafx.beans.property.BooleanProperty;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import jfxtras.labs.util.event.MouseControlUtil;


/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VCanvas extends ScalableContentPane {

    private final InnerCanvas innerCanvas = new InnerCanvas();

    public VCanvas() {

        Rectangle rect = new Rectangle();
        rect.setStroke(new Color(1, 1, 1, 1));
        rect.setFill(new Color(0, 0, 0, 0.5));

        contentProperty().addListener((ov, oldV, newV) -> {
            if (newV != null) {
                MouseControlUtil.
                        addSelectionRectangleGesture((Parent) newV, rect);
            }
        });

        setContent(innerCanvas);
        getStyleClass().add("vflow-background");
    }

    public BooleanProperty translateToMinNodePosProperty() {

        if (!(getContent() instanceof InnerCanvas)) {
            throw new UnsupportedOperationException(
                    "Only supported for content panes of type InnerCanvas");
        }

        return ((InnerCanvas) getContent()).translateToMinNodePosProperty();
    }

    public void setTranslateToMinNodePos(boolean value) {
        if (!(getContent() instanceof InnerCanvas)) {
            throw new UnsupportedOperationException(
                    "Only supported for content panes of type InnerCanvas");
        }

        ((InnerCanvas) getContent()).translateToMinNodePosProperty().set(value);
    }

    public boolean getTranslateToMinNodePos() {
        if (!(getContent() instanceof InnerCanvas)) {
            throw new UnsupportedOperationException(
                    "Only supported for content panes of type InnerCanvas");
        }

        return ((InnerCanvas) getContent()).
                translateToMinNodePosProperty().get();
    }

    public TranslateBehavior getTranslateBehavior() {
        if (!(getContent() instanceof InnerCanvas)) {
            throw new UnsupportedOperationException(
                    "Only supported for content panes of type InnerCanvas");
        }

        return ((InnerCanvas) getContent()).translateBehaviorProperty().get();
    }

    public ObjectProperty<TranslateBehavior> translateBehaviorProperty() {

        if (!(getContent() instanceof InnerCanvas)) {
            throw new UnsupportedOperationException(
                    "Only supported for content panes of type InnerCanvas");
        }

        return ((InnerCanvas) getContent()).translateBehaviorProperty();
    }

    public void setTranslateBehavior(TranslateBehavior behavior) {
        if (!(getContent() instanceof InnerCanvas)) {
            throw new UnsupportedOperationException(
                    "Only supported for content panes of type InnerCanvas");
        }

        ((InnerCanvas) getContent()).translateBehaviorProperty().set(behavior);
    }
    
    public void resetTranslation() {
        if (!(getContent() instanceof InnerCanvas)) {
            throw new UnsupportedOperationException(
                    "Only supported for content panes of type InnerCanvas");
        }

        ((InnerCanvas) getContent()).resetTranslation();
    }

}
