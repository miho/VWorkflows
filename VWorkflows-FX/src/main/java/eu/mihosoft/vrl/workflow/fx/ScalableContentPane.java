/*
 * Copyright 2012-2016 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

/**
 * Scales content to always fit in the bounds of this pane. Useful for workflows
 * with lots of windows.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ScalableContentPane extends Pane {

    private Scale contentScaleTransform;
    private Property<Pane> contentPaneProperty =
            new SimpleObjectProperty<>();
    private double contentScaleWidth = 1.0;
    private double contentScaleHeight = 1.0;
    private boolean aspectScale = true;
    private boolean autoRescale = true;
    private static boolean applyJDK7Fix = false;
    private final DoubleProperty minScaleXProperty = new SimpleDoubleProperty(Double.MIN_VALUE);
    private final DoubleProperty maxScaleXProperty = new SimpleDoubleProperty(Double.MAX_VALUE);
    private final DoubleProperty minScaleYProperty = new SimpleDoubleProperty(Double.MIN_VALUE);
    private final DoubleProperty maxScaleYProperty = new SimpleDoubleProperty(Double.MAX_VALUE);

    static {
        // JDK7 fix:
        applyJDK7Fix = System.getProperty("java.version").startsWith("1.7");
    }

    /**
     * Constructor.
     */
    public ScalableContentPane() {
        setContentPane(new Pane());

        setPrefWidth(USE_PREF_SIZE);
        setPrefHeight(USE_PREF_SIZE);

        needsLayoutProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                if (t1) {
                    computeScale();
                }
            }
        });
    }

    /**
     * @return the content pane
     */
    public Pane getContentPane() {
        return contentPaneProperty.getValue();
    }

    /**
     * Defines the content pane of this scalable pane.
     *
     * @param contentPane pane to define
     */
    public final void setContentPane(Pane contentPane) {
        contentPaneProperty.setValue(contentPane);
        contentPane.setManaged(false);
        initContentPaneListener();
//        contentPane.setStyle("-fx-border-color: rgb(0,0,0);");

        contentScaleTransform = new Scale(1, 1);
        getContentScaleTransform().setPivotX(0);
        getContentScaleTransform().setPivotY(0);
        getContentScaleTransform().setPivotZ(0);
        getContentPane().getTransforms().add(getContentScaleTransform());

        getChildren().add(contentPane);
        
        ChangeListener<Number> changeListener = new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                requestScale();
            }
        };
        
        minScaleXProperty().addListener(changeListener);
        minScaleYProperty().addListener(changeListener);
        maxScaleXProperty().addListener(changeListener);
        maxScaleYProperty().addListener(changeListener);
                

//        getContentPane().setStyle("-fx-border-color: red; -fx-border-width: 1;");
    }

    /**
     * Returns the content pane property.
     *
     * @return the content pane property
     */
    public Property<Pane> contentPaneProperty() {
        return contentPaneProperty;
    }

    /**
     * Returns the content scale transform.
     *
     * @return the content scale transform
     */
    public final Scale getContentScaleTransform() {
        return contentScaleTransform;
    }

    @Override
    protected void layoutChildren() {

        super.layoutChildren();


    }

    private void computeScale() {
        double realWidth =
                getContentPane().prefWidth(getHeight());

        double realHeigh =
                getContentPane().prefHeight(getWidth());

        if (applyJDK7Fix) {
//            realWidth += 0.01;
            realHeigh += 0.01; // does not paint without it
        }

        double leftAndRight = getInsets().getLeft() + getInsets().getRight();
        double topAndBottom = getInsets().getTop() + getInsets().getBottom();

        double contentWidth =
                getWidth() - leftAndRight;
        double contentHeight =
                getHeight() - topAndBottom;

        contentScaleWidth = contentWidth / realWidth;
        contentScaleHeight = contentHeight / realHeigh;

        contentScaleWidth = Math.max(contentScaleWidth, getMinScaleX());
        contentScaleWidth = Math.min(contentScaleWidth, getMaxScaleX());

        contentScaleHeight = Math.max(contentScaleHeight, getMinScaleY());
        contentScaleHeight = Math.min(contentScaleHeight, getMaxScaleY());

        if (isAspectScale()) {
            double scale = Math.min(contentScaleWidth, contentScaleHeight);

            getContentScaleTransform().setX(scale);
            getContentScaleTransform().setY(scale);

//            System.out.println("scale: " + scale);
        } else {
            getContentScaleTransform().setX(contentScaleWidth);
            getContentScaleTransform().setY(contentScaleHeight);
        }

        getContentPane().relocate(
                getInsets().getLeft(), getInsets().getTop());

        getContentPane().resize(
                (contentWidth) / contentScaleWidth,
                (contentHeight) / contentScaleHeight);
    }

    public void requestScale() {
        computeScale();
    }

    @Override
    protected double computeMinWidth(double d) {

        double result = getInsets().getLeft() + getInsets().getRight() + 1;

        return result;
    }

    @Override
    protected double computeMinHeight(double d) {

        double result = getInsets().getTop() + getInsets().getBottom() + 1;

        return result;
    }

    @Override
    protected double computePrefWidth(double d) {

        double result = 1;

        return result;
    }

    @Override
    protected double computePrefHeight(double d) {

        double result = 1;

        return result;
    }

    private void initContentPaneListener() {

        final ChangeListener<Bounds> boundsListener = new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds t, Bounds t1) {
                if (isAutoRescale()) {
                    setNeedsLayout(false);
                    getContentPane().requestLayout();
                    requestLayout();

                }
            }
        };

        final ChangeListener<Number> numberListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                if (isAutoRescale()) {
                    setNeedsLayout(false);
                    getContentPane().requestLayout();
                    requestLayout();

                }
            }
        };

        getContentPane().getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Node> c) {

                while (c.next()) {
                    if (c.wasPermutated()) {
                        for (int i = c.getFrom(); i < c.getTo(); ++i) {
                            //permutate
                        }
                    } else if (c.wasUpdated()) {
                        //update item
                    } else {
                        if (c.wasRemoved()) {
                            for (Node n : c.getRemoved()) {
                                n.boundsInLocalProperty().removeListener(boundsListener);
                                n.layoutXProperty().removeListener(numberListener);
                                n.layoutYProperty().removeListener(numberListener);
                            }
                        } else if (c.wasAdded()) {
                            for (Node n : c.getAddedSubList()) {
                                n.boundsInLocalProperty().addListener(boundsListener);
                                n.layoutXProperty().addListener(numberListener);
                                n.layoutYProperty().addListener(numberListener);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Defines whether to keep aspect ration when scaling content.
     *
     * @return <code>true</code> if keeping aspect ratio of the content;
     * <code>false</code> otherwise
     */
    public boolean isAspectScale() {
        return aspectScale;
    }

    /**
     * Defines whether to keep aspect ration of the content.
     *
     * @param aspectScale the state to set
     */
    public void setAspectScale(boolean aspectScale) {
        this.aspectScale = aspectScale;
    }

    /**
     * Indicates whether content is automatically scaled.
     *
     * @return <code>true</code> if content is automatically scaled;
     * <code>false</code> otherwise
     */
    public boolean isAutoRescale() {
        return autoRescale;
    }

    /**
     * Defines whether to automatically rescale content.
     *
     * @param autoRescale the state to set
     */
    public void setAutoRescale(boolean autoRescale) {
        this.autoRescale = autoRescale;
    }

    public DoubleProperty minScaleXProperty() {
        return minScaleXProperty;
    }

    public DoubleProperty minScaleYProperty() {
        return minScaleYProperty;
    }

    public DoubleProperty maxScaleXProperty() {
        return maxScaleXProperty;
    }

    public DoubleProperty maxScaleYProperty() {
        return maxScaleYProperty;
    }

    public double getMinScaleX() {
        return minScaleXProperty().get();
    }

    public double getMaxScaleX() {
        return maxScaleXProperty().get();
    }

    public double getMinScaleY() {
        return minScaleYProperty().get();
    }

    public double getMaxScaleY() {
        return maxScaleYProperty().get();
    }

    public void setMinScaleX(double s) {
        minScaleXProperty().set(s);
    }

    public void setMaxScaleX(double s) {
        maxScaleXProperty().set(s);
    }

    public void setMinScaleY(double s) {
        minScaleYProperty().set(s);
    }

    public void setMaxScaleY(double s) {
        maxScaleYProperty().set(s);
    }
}