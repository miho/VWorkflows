/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionSkin;
import eu.mihosoft.vrl.workflow.Connections;
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
import javafx.geometry.Bounds;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
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
    private Canvas content;
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

        content = new Canvas();

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
//                    parentContent.requestOptimization();
////                    requestLayout();
//                    requestParentLayout();
//                }
//            });
//        }
    }

    private void showFlowInWindow(VFlow flow, List<String> stylesheets, Stage stage, String title) {

        // create scalable root pane
        Canvas canvas = new Canvas();

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

        for (String connId : skin.connectors.keySet()) {
            for (Connections connectionsI : skin.controller.getAllConnections().values()) {
                connections.addAll(connectionsI.getAllWith(connId));
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
