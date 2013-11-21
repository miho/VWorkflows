/*
 * FlowNodeWindow.java
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

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionSkin;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import jfxtras.labs.scene.control.window.CloseIcon;
import jfxtras.labs.scene.control.window.MinimizeIcon;
import jfxtras.labs.scene.control.window.Window;
import jfxtras.labs.scene.control.window.WindowIcon;
import jfxtras.labs.util.event.MouseControlUtil;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class FlowNodeWindow extends Window {

    private ObjectProperty<FXFlowNodeSkin> nodeSkinProperty = new SimpleObjectProperty<>();
    private VCanvas content;
    private OptimizableContentPane parentContent;

    public FlowNodeWindow(FXFlowNodeSkin skin) {

        nodeSkinProperty().set(skin);

        getLeftIcons().add(new CloseIcon(this));
        getLeftIcons().add(new MinimizeIcon(this));

//        setTitleBarStyleClass("my-titlebar");

//        setStyle("    -fx-background-color: rgba(120,140,255,0.2);\n"
//                + "    -fx-border-color: rgba(120,140,255,0.42);\n"
//                + "    -fx-border-width: 2;");


        parentContent = new OptimizableContentPane();

        content = new VCanvas();

        parentContent.getChildren().add(content);

        super.setContentPane(parentContent);
        addCollapseIcon(skin);
        configureCanvas(skin);

//        addSelectionRectangle(skin, root);

        addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                connectorsToFront();
            }
        });
        
//        // TODO shouldn't leaf nodes also have a visibility property?
//        if (nodeSkinProperty.get().getModel() instanceof VFlowModel) {
//            VFlowModel model = (VFlowModel) nodeSkinProperty.get().getModel();
//            model.visibleProperty().addListener(new ChangeListener<Boolean>() {
//                @Override
//                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                    
//                    for (Node n : content.getContentPane().getChildren()) {
//                        if (n instanceof Window) {
//                            Window w = (Window) n;
//                            w.requestLayout();
//                            w.getContentPane().requestLayout();
//                        }
//                    }
//                    
////                    System.out.println("TEST");
//////                    parentContent.requestOptimization();
////                    requestLayout();
////                    getParent().requestLayout();
//////                    requestParentLayout();
////                    content.requestLayout();
////                    content.getContentPane().requestLayout();
//                }
//            });
//        }
    }

    private void showFlowInWindow(VFlow flow, List<String> stylesheets, Stage stage, String title) {

        // create scalable root pane
        VCanvas canvas = new VCanvas();

        // define background style
//        canvas.setStyle("-fx-background-color: linear-gradient(to bottom, rgb(10,32,60), rgb(42,52,120));");

        // create skin factory for flow visualization
        FXSkinFactory fXSkinFactory =
                nodeSkinProperty.get().getSkinFactory().newInstance(canvas.getContentPane(), null);

        // copy colors from prototype
//        if (nodeSkinProperty.get().getSkinFactory() != null) {
//            fXSkinFactory.connectionFillColors = nodeSkinProperty.get().getSkinFactory().connectionFillColorTypes();
//            fXSkinFactory.connectionStrokeColors = nodeSkinProperty.get().getSkinFactory().connectionStrokeColorTypes();
//        }

        // generate the ui for the flow
        flow.addSkinFactories(fXSkinFactory);

        // the usual application setup
        Scene scene = new Scene(canvas, 800, 800);

        scene.getStylesheets().setAll(stylesheets);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public Pane getWorkflowContentPane() {
        return content.getContentPane();
    }

    /**
     * @return the nodeSkinProperty
     */
    public final ObjectProperty<FXFlowNodeSkin> nodeSkinProperty() {
        return nodeSkinProperty;
    }

    private void addCollapseIcon(FXFlowNodeSkin skin) {

        if (skin == null) {
            return;
        }

        if (!(skin.getModel() instanceof VFlowModel)) {
            return;
        }

        final WindowIcon collapseIcon = new WindowIcon();

        collapseIcon.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                FXFlowNodeSkin skin = nodeSkinProperty.get();

                if (skin != null) {
                    VFlowModel model = (VFlowModel) skin.getModel();
                    model.setVisible(!model.isVisible());
                }
            }
        });

        getRightIcons().add(collapseIcon);

        if (skin.modelProperty() != null) {
            skin.modelProperty().addListener(new ChangeListener<VNode>() {
                @Override
                public void changed(ObservableValue<? extends VNode> ov,
                        VNode t, VNode t1) {
                    if (t1 instanceof VFlowModel) {
                        getRightIcons().add(collapseIcon);
                    } else {
                        getRightIcons().remove(collapseIcon);
                    }
                }
            });
        }


        // adds an icon that opens a new view in a separate window

        final WindowIcon newViewIcon = new WindowIcon();

        newViewIcon.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                FXFlowNodeSkin skin = nodeSkinProperty.get();

                if (skin != null) {

                    Stage stage = new Stage();
                    stage.setWidth(800);
                    stage.setHeight(600);

                    String nodeId = skin.getModel().getId();

                    for (VFlow vf : skin.getController().getSubControllers()) {
                        if (vf.getModel().getId().equals(nodeId)) {
                            showFlowInWindow(vf,
                                    NodeUtil.getStylesheetsOfAncestors(
                                    FlowNodeWindow.this),
                                    stage, getTitle());
                            break;
                        }
                    }
                }
            }
        });

        getLeftIcons().add(newViewIcon);

    }

    private void addSelectionRectangle(FXFlowNodeSkin skin, Pane root) {
        if (skin == null) {
            return;
        }
        if (!(skin.getModel() instanceof VFlowModel)) {
            return;
        }
        Rectangle rect = new Rectangle();
        rect.setStroke(new Color(1, 1, 1, 1));
        rect.setFill(new Color(0, 0, 0, 0.5));
        MouseControlUtil.
                addSelectionRectangleGesture(root, rect);
    }

    @Override
    public void toFront() {
        super.toFront();
        connectorsToFront();
    }

    private void connectorsToFront() {
        // move connectors to front
        FXFlowNodeSkin skin = nodeSkinProperty().get();

        for (Node n : skin.inputList) {
            n.toFront();
        }
        for (Node n : skin.outputList) {
            n.toFront();
        }

        List<Connection> connections = new ArrayList<>();

        for (Connector connector : skin.connectors.keySet()) {
            for (Connections connectionsI : skin.controller.getAllConnections().values()) {
                connections.addAll(connectionsI.getAllWith(connector));
            }
        }

        for (Connection conn : connections) {
            ConnectionSkin skinI = skin.controller.getNodeSkinLookup().getById(skin.getSkinFactory(), conn);

            if (skinI instanceof FXConnectionSkin) {
                FXConnectionSkin fxSkin = (FXConnectionSkin) skinI;
                fxSkin.toFront();
            }
        }
    }

    private void configureCanvas(FXFlowNodeSkin skin) {


//        if (skin == null) {
//            return;
//        }
//
//        if ((skin.getModel() instanceof VFlowModel)) {
//            return;
//        }

        content.getStyleClass().setAll("vnode-content");
    }
}
