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

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.skin.ConnectionSkin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.InvalidationListener;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.scene.control.window.CloseIcon;
import jfxtras.scene.control.window.MinimizeIcon;
import jfxtras.scene.control.window.Window;
import jfxtras.scene.control.window.WindowIcon;
//import jfxtras.labs.scene.control.window.CloseIcon;
//import jfxtras.labs.scene.control.window.MinimizeIcon;
//import jfxtras.labs.scene.control.window.Window;
//import jfxtras.labs.scene.control.window.WindowIcon;
import jfxtras.scene.control.window.WindowUtil;


/**
 * A window that represents a flow node.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class FlowNodeWindow extends Window {

    private final ObjectProperty<FXFlowNodeSkin> nodeSkinProperty
            = new SimpleObjectProperty<>();
    private VCanvas content;
    private Pane inputContainer;
    private Pane outputContainer;
    private OptimizableContentPane parentContent;
    private final CloseIcon closeIcon = new CloseIcon(this);
    private final MinimizeIcon minimizeIcon = new MinimizeIcon(this);

    private ChangeListener<Boolean> selectionListener;

    private Callback<FlowNodeWindow, CloseIcon> showCloseIcon = (FlowNodeWindow w) -> {
        if (!getLeftIcons().contains(closeIcon)) {
            getLeftIcons().add(closeIcon);
        }

        return closeIcon;
    };

    private Callback<FlowNodeWindow, CloseIcon> hideCloseIcon = (FlowNodeWindow w) -> {

        getLeftIcons().remove(closeIcon);

        return closeIcon;
    };

    private Callback<FlowNodeWindow, MinimizeIcon> showMinimizeIcon = (FlowNodeWindow w) -> {
        if (!getLeftIcons().contains(minimizeIcon)) {
            getLeftIcons().add(minimizeIcon);
        }

        return minimizeIcon;
    };

    private Callback<FlowNodeWindow, MinimizeIcon> hideMinimizeIcon = (FlowNodeWindow w) -> {

        getLeftIcons().remove(minimizeIcon);

        return minimizeIcon;
    };

    /**
     * Construxtor.
     *
     * @param skin the skin of the node that shall be visualized by this window.
     */
    public FlowNodeWindow(final FXFlowNodeSkin skin) {

        nodeSkinProperty().set(skin);
        setEditableState(true);

        initUI(skin);
        initListenersAndBindings(skin);
        initCaching();
    }

    private void initUI(final FXFlowNodeSkin skin) {
        // only register content if this window visualizes a flow
        if (skin.getModel() instanceof VFlowModel) {

            VFlowModel flowNodeModel = (VFlowModel) skin.getModel();

            parentContent = new OptimizableContentPane();
            content = new VCanvas();
            content.setPadding(new Insets(5));
            content.setMinScaleX(0.01);
            content.setMinScaleY(0.01);
            content.setMaxScaleX(1);
            content.setMaxScaleY(1);

            HBox.setHgrow(content, Priority.SOMETIMES);

            addResetViewMenu(content);

            inputContainer = new VBox();
            outputContainer = new VBox();
            HBox paramBox = new HBox(inputContainer, content, getOutputContainer());

            parentContent.getChildren().add(paramBox);
            super.setContentPane(parentContent);

            InvalidationListener refreshViewListener = (o) -> {
                content.resetScale();
                content.resetTranslation();
            };
        }

        // 
        addCollapseIcon(skin);
        configureCanvas(skin);
    }

    private void initListenersAndBindings(final FXFlowNodeSkin skin) {
        addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent t) -> {
            connectorsToFront();
        });

        setSelectable(skin.getModel().isSelectable());
        skin.getModel().selectableProperty().bindBidirectional(this.selectableProperty());

        WindowUtil.getDefaultClipboard().select(FlowNodeWindow.this, skin.getModel().isSelected());

        selectionListener = (ov, oldValue, newValue) -> {
            WindowUtil.getDefaultClipboard().select(FlowNodeWindow.this, newValue);
        };

        skin.getModel().selectedProperty().addListener(selectionListener);

        skin.getModel().requestSelection(FlowNodeWindow.this.isSelected());
        FlowNodeWindow.this.selectedProperty().addListener((ov, oldValue, newValue) -> {
            skin.getModel().requestSelection(newValue);
        });

        skinProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue != null) {
                Node titlebar = newValue.getNode().lookup("." + getTitleBarStyleClass());

                titlebar.addEventHandler(MouseEvent.ANY, (MouseEvent evt) -> {
                    if (evt.getClickCount() == 1
                            && evt.getEventType() == MouseEvent.MOUSE_RELEASED
                            && evt.isDragDetect()) {
                        skin.getModel().requestSelection(!skin.getModel().isSelected());
                    }
                });
            }
        });

        onClosedActionProperty().addListener((ov, oldValue, newValue) -> {
            onRemovedFromSceneGraph();
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

    ObservableList<Node> getChildrenModifiable() {
        return super.getChildren();
    }

    public static void addResetViewMenu(VCanvas canvas) {
        final ContextMenu cm = new ContextMenu();
        MenuItem resetViewItem = new MenuItem("Reset View");
        resetViewItem.setOnAction((ActionEvent e) -> {
            canvas.resetTranslation();
            canvas.resetScale();
        });
        cm.getItems().add(resetViewItem);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                cm.show(canvas, e.getScreenX(), e.getScreenY());
            }
        });
    }

    private void showFlowInWindow(VFlow flow, List<String> stylesheets, String title) {

        // create scalable root pane
        VCanvas canvas = new VCanvas();
        addResetViewMenu(canvas);
        canvas.setMinScaleX(0.2);
        canvas.setMinScaleY(0.2);
        canvas.setMaxScaleX(1);
        canvas.setMaxScaleY(1);

        canvas.setTranslateToMinNodePos(true);

        // create skin factory for flow visualization
        FXSkinFactory fXSkinFactory
                = nodeSkinProperty.get().getSkinFactory().
                newInstance((Parent) canvas.getContent(), null);

        // generate the ui for the flow
        flow.addSkinFactories(fXSkinFactory);

        // the usual stage/scene setup
        Scene scene = new Scene(canvas, 800, 800);
        scene.getStylesheets().setAll(stylesheets);

        VFlow rootFlow = flow.getRootFlow();
        Stage stage = new FlowStage(rootFlow, this, canvas);

        stage.setWidth(800);
        stage.setHeight(600);

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public Pane getWorkflowContentPane() {
        return (Pane) content.getContent();
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

        collapseIcon.setOnAction((ActionEvent t) -> {
            FXFlowNodeSkin skin1 = nodeSkinProperty.get();
            if (skin1 != null) {
                VFlowModel model = (VFlowModel) skin1.getModel();
                model.setVisible(!model.isVisible());
            }
        });

        getRightIcons().add(collapseIcon);

        if (skin.modelProperty() != null) {
            skin.modelProperty().addListener((ov, oldValue, newValue) -> {
                if (newValue instanceof VFlowModel) {
                    getRightIcons().add(collapseIcon);
                } else {
                    getRightIcons().remove(collapseIcon);
                }
            });
        }

        // adds an icon that opens a new view in a separate window
        final WindowIcon newViewIcon = new WindowIcon();

        newViewIcon.setOnAction((ActionEvent t) -> {
            FXFlowNodeSkin skin1 = nodeSkinProperty.get();
            if (skin1 != null) {
                String nodeId = skin1.getModel().getId();
                for (VFlow vf : skin1.getController().getSubControllers()) {
                    if (vf.getModel().getId().equals(nodeId)) {
                        showFlowInWindow(vf,
                                NodeUtil.getStylesheetsOfAncestors(
                                        FlowNodeWindow.this),
                                getLocation(vf));
                        break;
                    }
                }
            }
        });

        getLeftIcons().add(newViewIcon);

    }

    private String getLocation(VFlow f) {

        VFlowModel parent = f.getModel().getFlow();

        List<String> names = new ArrayList<>();

        names.add(f.getModel().getTitle());

        while (parent != null) {
            names.add(parent.getTitle());
            parent = parent.getFlow();
        }

        Collections.reverse(names);

        StringBuilder sb = new StringBuilder();

        names.forEach(n -> sb.append("/").append(n));

        return sb.toString();
    }

    @Override
    public void toFront() {
        super.toFront();
        connectorsToFront();
    }

    private void connectorsToFront() {
        // move connectors to front
        FXFlowNodeSkin skin = nodeSkinProperty().get();

        for (List<ConnectorShape> shapeList : skin.shapeLists) {
            for (ConnectorShape cs : shapeList) {
                cs.getNode().toFront();
            }
        }

        List<Connection> connections = new ArrayList<>();

        for (Connector connector : skin.connectors.keySet()) {
            for (Connections connectionsI : skin.controller.getAllConnections().values()) {
                connections.addAll(connectionsI.getAllWith(connector));
            }
        }

        for (Connection conn : connections) {
            ConnectionSkin skinI = skin.controller.getNodeSkinLookup().
                    getById(skin.getSkinFactory(), conn);

            if (skinI instanceof FXConnectionSkin) {
                FXConnectionSkin fxSkin = (FXConnectionSkin) skinI;
                fxSkin.receiverToFront();
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
        if (content != null) {
            content.getStyleClass().setAll("vnode-content");
            skin.configureCanvas(content);
        }
    }

    void onRemovedFromSceneGraph() {
        nodeSkinProperty().get().getModel().selectedProperty().
                removeListener(selectionListener);
    }

    private void setCloseableState(boolean b) {
        if (b) {
            getShowCloseIconCallback().call(this);
        } else {
            getHideCloseIconCallback().call(this);
        }
    }

    private void setMinimizableState(boolean b) {
        if (b) {
            getShowMinimizeIconCallback().call(this);
        } else {
            getHideMinimizeIconCallback().call(this);
        }
    }

    final void setEditableState(boolean b) {
        setCloseableState(b);
        setMinimizableState(b);
//        setSelectable(b);
    }

    /**
     * @return the paramContainer
     */
    public Pane getInputContainer() {
        return inputContainer;
    }

    /**
     * @return the outputContainer
     */
    public Pane getOutputContainer() {
        return outputContainer;
    }

    private Callback<FlowNodeWindow, CloseIcon> getShowCloseIconCallback() {
        return showCloseIcon;
    }

    public void setShowCloseIconCallback(Callback<FlowNodeWindow, CloseIcon> callback) {
        this.showCloseIcon = callback;
    }

    private Callback<FlowNodeWindow, CloseIcon> getHideCloseIconCallback() {
        return hideCloseIcon;
    }

    public void setHideCloseIconCallback(Callback<FlowNodeWindow, CloseIcon> callback) {
        this.hideCloseIcon = callback;
    }

    private Callback<FlowNodeWindow, MinimizeIcon> getShowMinimizeIconCallback() {
        return showMinimizeIcon;
    }

    public void setShowMinimizeIconCallback(Callback<FlowNodeWindow, MinimizeIcon> callback) {
        this.showMinimizeIcon = callback;
    }

    private Callback<FlowNodeWindow, MinimizeIcon> getHideMinimizeIconCallback() {
        return hideMinimizeIcon;
    }

    public void setHideMinimizeIconCallback(Callback<FlowNodeWindow, MinimizeIcon> callback) {
        this.hideMinimizeIcon = callback;
    }

    private void initCaching() {
        
        localToSceneTransformProperty().addListener((ov)->{
             Bounds bounds = this.localToScene(getBoundsInLocal());
             if(bounds.getWidth()<10 || bounds.getHeight()<10) {
                 setCache(false);
             } else {
                 setCache(true);
             }
        });

//
//        boolean[] wasMoving = {false};
//
//        InvalidationListener cacheListener = (ov) -> {
//            
//            if (isMoving()) {
//                setCache(true);
//                Parent parent = getParent();
//                if (parent != null) {
//                    parent.getChildrenUnmodifiable().stream().
//                            filter(n -> n instanceof FlowNodeWindow
//                                    || n instanceof ConnectorCircle).
//                            forEach(n -> n.setCache(true));
//                }
//            } else {
//                setCache(false);
//                System.out.println("was: " + wasMoving[0]);
//                if (wasMoving[0]) {
//                    Parent parent = getParent();
//                    if (parent != null) {
//                        parent.getChildrenUnmodifiable().stream().
//                                filter(n -> n instanceof FlowNodeWindow
//                                        || n instanceof ConnectorCircle).
//                                forEach(n -> n.setCache(false));
//                    }
//                }
//            }
//            
//            wasMoving[0] = isMoving();
//        };
//
//        movingProperty().addListener(cacheListener);
//
//        cacheProperty().addListener(state -> setTitle("cache: " + isCache()));
    }

    static class FlowStage extends Stage {

        public FlowStage(VFlow rootFlow, FlowNodeWindow flowNodeWindow, VCanvas canvas) {
            String nodeId = flowNodeWindow.
                    nodeSkinProperty().get().getModel().getId();

            Stage stage = this;
            rootFlow.getNodes().addListener(
                    (ListChangeListener.Change<? extends VNode> c) -> {
                        while (c.next()) {
                            if (c.wasAdded()) {
                                for (VNode n : c.getAddedSubList()) {
                                    if (n.getId().equals(nodeId)) {
                                        ((Pane)canvas.getContent()).getChildren().clear();
                                        VFlow flow = (VFlow) rootFlow.getFlowById(n.getId());
                                        flow.addSkinFactories(new FXValueSkinFactory(null));
                                    }
                                }
                            }

                            if (c.wasRemoved()) {
                                for (VNode n : c.getRemoved()) {
                                    if (n.getId().equals(nodeId)) {
                                        stage.close();
                                    }
                                }
                            }
                        }
                    });
        }

    }
}
