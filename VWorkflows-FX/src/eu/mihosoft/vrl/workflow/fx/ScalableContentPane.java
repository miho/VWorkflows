/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;


import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;

/**
 * Scales content to always fit in the bounds of this pane. Useful for workflows
 * with lots of windows.
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public class ScalableContentPane extends Pane {

    private Scale contentScaleTransform;
    private Property<Pane> contentPaneProperty =
            new SimpleObjectProperty<>();
    private double contentScaleWidth = 1.0;
    private double contentScaleHeight = 1.0;
    private boolean aspectScale = true;
    private boolean autoRescale = true;

    /**
     * Constructor.
     */
    public ScalableContentPane() {
        setContentPane(new Pane());

        setPrefWidth(USE_PREF_SIZE);
        setPrefHeight(USE_PREF_SIZE);
    }

    /**
     * @return the content pane
     */
    public Pane getContentPane() {
        return contentPaneProperty.getValue();
    }

    /**
     * Defines the content pane of this scalable pane.
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
    }

    /**
     * Returns the content pane property.
     * @return the content pane property
     */
    public Property<Pane> contentPaneProperty() {
        return contentPaneProperty;
    }

    /**
     * Returns the content scale transform.
     * @return the content scale transform
     */
    public final Scale getContentScaleTransform() {
        return contentScaleTransform;
    }

    @Override
    protected void layoutChildren() {

        super.layoutChildren();

//        double realWidth =
//                Math.max(getWidth(),
//                getContentPane().prefWidth(0));
//
//        double realHeigh = Math.max(getHeight(),
//                getContentPane().prefHeight(0));
        
         double realWidth =
                getContentPane().prefWidth(0);

        double realHeigh = 
                getContentPane().prefHeight(0);

        double leftAndRight = getInsets().getLeft() + getInsets().getRight();
        double topAndBottom = getInsets().getTop() + getInsets().getBottom();

        double contentWidth =
                getWidth() - leftAndRight;
        double contentHeight =
                getHeight() - topAndBottom;

        contentScaleWidth = contentWidth / realWidth;
        contentScaleHeight = contentHeight / realHeigh;

        if (isAspectScale()) {
            double scale = Math.min(contentScaleWidth, contentScaleHeight);
            contentScaleWidth = scale;
            contentScaleHeight = scale;
        }

        getContentScaleTransform().setX(contentScaleWidth);
        getContentScaleTransform().setY(contentScaleHeight);

        getContentPane().relocate(
                getInsets().getLeft(), getInsets().getTop());

        getContentPane().resize(
                (contentWidth) / contentScaleWidth,
                (contentHeight) / contentScaleHeight);
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
                    requestLayout();
                }
            }
        };

        final ChangeListener<Number> numberListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                if (isAutoRescale()) {
                    requestLayout();
                }
            }
        };

        getContentPane().getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {


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
     * @return <code>true</code> if keeping aspect ratio of the content;
     *         <code>false</code> otherwise
     */
    public boolean isAspectScale() {
        return aspectScale;
    }

    /**
     * Defines whether to keep aspect ration of the content.
     * @param aspectScale the state to set
     */
    public void setAspectScale(boolean aspectScale) {
        this.aspectScale = aspectScale;
    }

    /**
     * Indicates whether content is automatically scaled.
     * @return <code>true</code> if content is automatically scaled;
     *         <code>false</code> otherwise
     */
    public boolean isAutoRescale() {
        return autoRescale;
    }

    /**
     * Defines whether to automatically rescale content.
     * @param autoRescale the state to set
     */
    public void setAutoRescale(boolean autoRescale) {
        this.autoRescale = autoRescale;
    }
}
