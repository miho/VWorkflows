/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

//        if (getParent() instanceof ScalableContentPane) {
//            ScalableContentPane pane = (ScalableContentPane) getParent();
//            pane.requestLayout();
//        }
//
        setNeedsLayout(true);

        if (!translateToMinNodePosProperty.get()) {
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
