/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ScalableContentPane extends Region {

    private Scale contentScaleTransform;
    private Property<Pane> contentPaneProperty =
            new SimpleObjectProperty<Pane>();
    private double contentScaleWidth = 1.0;
    private double contentScaleHeight = 1.0;
    private boolean aspectScale = true;

    public ScalableContentPane() {
        setContentPane(new RootPane());

        setPrefWidth(USE_PREF_SIZE);
        setPrefHeight(USE_PREF_SIZE);
    }

    /**
     * @return the view
     */
    public Pane getContentPane() {
        return contentPaneProperty.getValue();
    }

    public void setContentPane(Pane contentPane) {
        contentPaneProperty.setValue(contentPane);
        contentPane.setManaged(false);
//        initContentPaneListener();
//        contentPane.setStyle("-fx-border-color: rgb(0,0,0);");

        contentScaleTransform = new Scale(1, 1);
        getContentScaleTransform().setPivotX(0);
        getContentScaleTransform().setPivotY(0);
        getContentScaleTransform().setPivotZ(0);
        getContentPane().getTransforms().add(getContentScaleTransform());

        getChildren().add(contentPane);
    }

    public Property<Pane> contentPaneProperty() {
        return contentPaneProperty;
    }

    /**
     * @return the contentScaleTransform
     */
    public final Scale getContentScaleTransform() {
        return contentScaleTransform;
    }

    @Override
    protected void layoutChildren() {

//        System.out.println("scaled-content: layout " + System.currentTimeMillis());

        super.layoutChildren();

        double realWidth =
                Math.max(getWidth(),
                getContentPane().prefWidth(0));

        double realHeigh = Math.max(getHeight(),
                getContentPane().prefHeight(0));

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

        double result = getInsets().getLeft() + getInsets().getRight()+1;
//        result = Math.min(result, getContentPane().minWidth(d) * contentScale);

        return result;//getContentPane().minWidth(d) * contentScale;
    }

    @Override
    protected double computeMinHeight(double d) {

        double result = getInsets().getTop() + getInsets().getBottom()+1;
//        result = Math.min(result,
//                +getContentPane().minHeight(d) * contentScale);

        return result;//getContentPane().minHeight(d) * contentScale;
    }

    @Override
    protected double computePrefWidth(double d) {

        double result = 1;//minWidth(d);
//        result = Math.min(result, getContentPane().prefWidth(d) * contentScale);

        return result;//getContentPane().prefWidth(d) * contentScale;
    }

    @Override
    protected double computePrefHeight(double d) {

        double result = 1;//minHeight(d);
//        result = Math.min(result,
//                + getContentPane().prefHeight(d) * contentScale);

        return result;//getContentPane().prefHeight(d) * contentScale;
    }

    private void initContentPaneListener() {

        final ChangeListener<Bounds> boundsListener = new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds t, Bounds t1) {
//                System.out.println("b.w: " + t1.getWidth() + ", b.h: " + t1.getHeight());
                layout();
            }
        };

        final ChangeListener<Number> numberListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                layout();
            }
        };

//        getContentPane().getChildren().addListener(new ListChangeListener<Node>() {
//            @Override
//            public void onChanged(Change<? extends Node> c) {
//
//
//                while (c.next()) {
//                    if (c.wasPermutated()) {
//                        for (int i = c.getFrom(); i < c.getTo(); ++i) {
//                            //permutate
//                        }
//                    } else if (c.wasUpdated()) {
//                        //update item
//                    } else {
//                        if (c.wasRemoved()) {
//                            for (Node n : c.getRemoved()) {
//                                n.boundsInLocalProperty().removeListener(boundsListener);
//                                n.layoutXProperty().removeListener(numberListener);
//                                n.layoutYProperty().removeListener(numberListener);
//                            }
//                        } else if (c.wasAdded()) {
//                            for (Node n : c.getAddedSubList()) {
//                                n.boundsInLocalProperty().addListener(boundsListener);
//                                n.layoutXProperty().addListener(numberListener);
//                                n.layoutYProperty().addListener(numberListener);
//                            }
//                        }
//                    }
//                }
//            }
//        });
    }

    /**
     * @return the aspectScale
     */
    public boolean isAspectScale() {
        return aspectScale;
    }

    /**
     * @param aspectScale the aspectScale to set
     */
    public void setAspectScale(boolean aspectScale) {
        this.aspectScale = aspectScale;
    }
}
