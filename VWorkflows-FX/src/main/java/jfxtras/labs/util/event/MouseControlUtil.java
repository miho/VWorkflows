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
package jfxtras.labs.util.event;

import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import jfxtras.scene.control.window.NodeUtil;
import jfxtras.scene.control.window.SelectableNode;
import jfxtras.scene.control.window.WindowUtil;

/**
 * This is a utility class that provides methods for mouse gesture control.
 * Currently, it can be used to make nodes draggable.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class MouseControlUtil {

    // no instanciation allowed
    private MouseControlUtil() {
        throw new AssertionError(); // not in this class either!
    }

    /**
     * Makes a node draggable via mouse gesture.
     *
     * <p>
     * <b>Note:</b> Existing handlers will be replaced!</p>
     *
     * @param n the node that shall be made draggable
     */
    public static void makeDraggable(final Node n) {

        makeDraggable(n, null, null, false);
    }

    /**
     * Makes a node draggable via mouse gesture.
     *
     * <p>
     * <b>Note:</b> Existing handlers will be replaced!</p>
     *
     * @param n the node that shall be made draggable
     */
    public static void makeDraggable(final Node n, boolean centerNode) {

        makeDraggable(n, null, null);
    }

    /**
     * Adds a selection rectangle gesture to the specified parent node.
     *
     * A rectangle node must be specified that is used to indicate the selection
     * area.
     *
     * <p>
     * <b>Note:</b></p>
     *
     * To support selection a node must implement the
     * {@link jfxtras.scene.control.window.SelectableNode} interface.
     *
     * @param root parent node
     * @param rect selectionn rectangle
     *
     * @see jfxtras.scene.control.window.Clipboard
     * @see jfxtras.scene.control.window.WindowUtil#getDefaultClipboard()
     */
    public static void addSelectionRectangleGesture(final Parent root,
            final Rectangle rect) {
        addSelectionRectangleGesture(root, rect, null, null, null);
    }

    /**
     * Adds a selection rectangle gesture to the specified parent node.
     *
     * A rectangle node must be specified that is used to indicate the selection
     * area.
     *
     * <p>
     * <b>Note:</b></p>
     *
     * To support selection a node must implement the
     * {@link jfxtras.scene.control.window.SelectableNode} interface.
     *
     * @param root parent node
     * @param rect selection rectangle
     * @param dragHandler additional drag handler (optional, may be
     * <code>null</code>)
     * @param pressHandler additional press handler (optional, may be
     * <code>null</code>)
     * @param releaseHandler additional release handler (optional, may be
     * <code>null</code>)
     *
     * @see jfxtras.scene.control.window.Clipboard
     * @see jfxtras.scene.control.window.WindowUtil#getDefaultClipboard()
     */
    public static void addSelectionRectangleGesture(
            final Parent root,
            final Rectangle rect,
            EventHandler<MouseEvent> dragHandler,
            EventHandler<MouseEvent> pressHandler,
            EventHandler<MouseEvent> releaseHandler) {

        EventHandlerGroup<MouseEvent> dragHandlerGroup = new EventHandlerGroup<>();
        EventHandlerGroup<MouseEvent> pressHandlerGroup = new EventHandlerGroup<>();
        EventHandlerGroup<MouseEvent> releaseHandlerGroup = new EventHandlerGroup<>();

        if (dragHandler != null) {
            dragHandlerGroup.addHandler(dragHandler);
        }

        if (pressHandler != null) {
            pressHandlerGroup.addHandler(pressHandler);
        }

        if (releaseHandler != null) {
            releaseHandlerGroup.addHandler(releaseHandler);
        }

        root.setOnMouseDragged(dragHandlerGroup);
        root.setOnMousePressed(pressHandlerGroup);
        root.setOnMouseReleased(releaseHandlerGroup);

        RectangleSelectionControllerImpl selectionHandler
                = new RectangleSelectionControllerImpl();

        selectionHandler.apply(root, rect,
                dragHandlerGroup, pressHandlerGroup, releaseHandlerGroup);
    }

    /**
     * Makes a node draggable via mouse gesture.
     *
     * <p>
     * <b>Note:</b> Existing handlers will be replaced!</p>
     *
     * @param n the node that shall be made draggable
     * @param dragHandler additional drag handler
     * @param pressHandler additional press handler
     */
    public static void makeDraggable(final Node n,
            EventHandler<MouseEvent> dragHandler,
            EventHandler<MouseEvent> pressHandler) {
        makeDraggable(n, dragHandler, pressHandler, false);
    }

    /**
     * Makes a node draggable via mouse gesture.
     *
     * <p>
     * <b>Note:</b> Existing handlers will be replaced!</p>
     *
     * @param n the node that shall be made draggable
     * @param dragHandler additional drag handler
     * @param pressHandler additional press handler
     */
    public static void makeDraggable(final Node n,
            EventHandler<MouseEvent> dragHandler,
            EventHandler<MouseEvent> pressHandler, boolean centerNode) {

        EventHandlerGroup<MouseEvent> dragHandlerGroup = new EventHandlerGroup<>();
        EventHandlerGroup<MouseEvent> pressHandlerGroup = new EventHandlerGroup<>();

        if (dragHandler != null) {
            dragHandlerGroup.addHandler(dragHandler);
        }

        if (pressHandler != null) {
            pressHandlerGroup.addHandler(pressHandler);
        }

        n.setOnMouseDragged(dragHandlerGroup);
        n.setOnMousePressed(pressHandlerGroup);

        n.layoutXProperty().unbind();
        n.layoutYProperty().unbind();

        _makeDraggable(n, dragHandlerGroup, pressHandlerGroup, centerNode);
    }

//    public static void makeResizable(Node n) {
//        
//    }
    private static void _makeDraggable(
            final Node n,
            EventHandlerGroup<MouseEvent> dragHandler,
            EventHandlerGroup<MouseEvent> pressHandler, boolean centerNode) {

        DraggingControllerImpl draggingController
                = new DraggingControllerImpl();
        draggingController.apply(n, dragHandler, pressHandler, centerNode);
    }
}

class DraggingControllerImpl {

    private double nodeX;
    private double nodeY;
    private double mouseX;
    private double mouseY;
    private EventHandler<MouseEvent> mouseDraggedEventHandler;
    private EventHandler<MouseEvent> mousePressedEventHandler;
    private boolean centerNode = false;

    public DraggingControllerImpl() {
        //
    }

    public void apply(Node n,
            EventHandlerGroup<MouseEvent> draggedEvtHandler,
            EventHandlerGroup<MouseEvent> pressedEvtHandler,
            boolean centerNode) {
        init(n);
        draggedEvtHandler.addHandler(mouseDraggedEventHandler);
        pressedEvtHandler.addHandler(mousePressedEventHandler);
        this.centerNode = centerNode;
    }

    private void init(final Node n) {
        mouseDraggedEventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                performDrag(n, event);

                event.consume();
            }
        };

        mousePressedEventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                performDragBegin(n, event);
                event.consume();
            }
        };
    }

    public void performDrag(
            Node n, MouseEvent event) {
        final double parentScaleX = n.getParent().
                localToSceneTransformProperty().getValue().getMxx();
        final double parentScaleY = n.getParent().
                localToSceneTransformProperty().getValue().getMyy();

        // Get the exact moved X and Y
        double offsetX = event.getSceneX() - mouseX;
        double offsetY = event.getSceneY() - mouseY;

        nodeX += offsetX;
        nodeY += offsetY;

        double scaledX;
        double scaledY;

        if (centerNode) {
            Point2D p2d = n.getParent().sceneToLocal(mouseX, mouseY);
            scaledX = p2d.getX();
            scaledY = p2d.getY();
        } else {
            scaledX = nodeX * 1 / (parentScaleX);
            scaledY = nodeY * 1 / (parentScaleY);
        }

        n.setLayoutX(scaledX);
        n.setLayoutY(scaledY);

        // again set current Mouse x AND y position
        mouseX = event.getSceneX();
        mouseY = event.getSceneY();

    }

    public void performDragBegin(
            Node n, MouseEvent event) {

        final double parentScaleX = n.getParent().
                localToSceneTransformProperty().getValue().getMxx();
        final double parentScaleY = n.getParent().
                localToSceneTransformProperty().getValue().getMyy();

        // record the current mouse X and Y position on Node
        mouseX = event.getSceneX();
        mouseY = event.getSceneY();

        if (centerNode) {
            Point2D p2d = n.getParent().sceneToLocal(mouseX, mouseY);
            nodeX = p2d.getX();
            nodeY = p2d.getY();
        } else {
            nodeX = n.getLayoutX() * parentScaleX;
            nodeY = n.getLayoutY() * parentScaleY;
        }

        n.toFront();
    }
}

class RectangleSelectionControllerImpl {

    private Rectangle rectangle;
    private Parent root;
    private double nodeX;
    private double nodeY;
    private double firstX;
    private double firstY;
    private double secondX;
    private double secondY;
    private EventHandler<MouseEvent> mouseDraggedEventHandler;
    private EventHandler<MouseEvent> mousePressedHandler;
    private EventHandler<MouseEvent> mouseReleasedHandler;

    private final List<SelectableNode> selectedNodes = new ArrayList<>();

    public RectangleSelectionControllerImpl() {
        //
    }

    public void apply(Parent root,
            Rectangle rect,
            EventHandlerGroup<MouseEvent> draggedEvtHandler,
            EventHandlerGroup<MouseEvent> pressedEvtHandler,
            EventHandlerGroup<MouseEvent> releasedEvtHandler) {
        init(root, rect);
        draggedEvtHandler.addHandler(mouseDraggedEventHandler);
        pressedEvtHandler.addHandler(mousePressedHandler);
        releasedEvtHandler.addHandler(mouseReleasedHandler);
    }

    private void init(final Parent root, final Rectangle rect) {

        this.rectangle = rect;

        this.root = root;

//        root.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent t) {
//                WindowUtil.getDefaultClipboard().unselectAll();
//            }
//        });
        mouseDraggedEventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                performDrag(root, event);
                event.consume();
            }
        };

        mousePressedHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                performDragBegin(root, event);
                event.consume();
            }
        };

        mouseReleasedHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                performDragEnd(root, event);
                event.consume();
            }
        };
    }

    public void performDrag(
            Parent root, MouseEvent event) {

        rectangle.setVisible(true);

        final double parentScaleX = root.
                localToSceneTransformProperty().getValue().getMxx();
        final double parentScaleY = root.
                localToSceneTransformProperty().getValue().getMyy();

        final double translateX = -root.
                localToSceneTransformProperty().getValue().getTx();
        final double translateY = -root.
                localToSceneTransformProperty().getValue().getTy();

        secondX = event.getSceneX();
        secondY = event.getSceneY();

        firstX = Math.max(firstX, 0);
        firstY = Math.max(firstY, 0);

        secondX = Math.max(secondX, 0);
        secondY = Math.max(secondY, 0);

        double x = Math.min(firstX, secondX);
        double y = Math.min(firstY, secondY);

        double width = Math.abs(secondX - firstX);
        double height = Math.abs(secondY - firstY);

        rectangle.setX(x / parentScaleX + translateX / parentScaleX);
        rectangle.setY(y / parentScaleY + translateY / parentScaleY);
        rectangle.setWidth( Math.max(1,width / parentScaleX));
        rectangle.setHeight(Math.max(1,height / parentScaleY));

        selectIntersectingNodes(root, !event.isControlDown());

    }

    private void selectIntersectingNodes(Parent root, boolean deselect) {

        List<Node> selectableNodes = root.getChildrenUnmodifiable().
                filtered(n -> n instanceof SelectableNode);

        boolean rectBigEnough = rectangle.getWidth() > 1 || rectangle.getHeight() > 1;

        for (Node n : selectableNodes) {
            boolean selectN = rectangle.intersects(
                    rectangle.parentToLocal(
                            n.localToParent(n.getBoundsInLocal())));

            SelectableNode sn = (SelectableNode) n;

            if ((deselect || potentiallySelected(sn)) || (selectN && rectBigEnough)) {
                WindowUtil.getDefaultClipboard().select(
                        sn, (selectN && rectBigEnough));
            }
        }

    }

    public void performDragBegin(
            Parent root, MouseEvent event) {

        selectedNodes.addAll(
                WindowUtil.getDefaultClipboard().getSelectedItems());

        if (rectangle.getParent() != null) {
            return;
        }

        // record the current mouse X and Y position on Node
        firstX = event.getSceneX();
        firstY = event.getSceneY();

        NodeUtil.addToParent(root, rectangle);

        rectangle.setWidth(0);
        rectangle.setHeight(0);

        rectangle.toFront();

        rectangle.setVisible(false);
    }

    public void performDragEnd(
            Parent root, MouseEvent event) {

        if (rectangle.getParent() != null) {

            selectIntersectingNodes(root, !event.isControlDown());

            NodeUtil.removeFromParent(rectangle);

        }

        selectedNodes.clear();

    }

    private boolean potentiallySelected(SelectableNode sn) {
        return !selectedNodes.contains(sn)
                && WindowUtil.getDefaultClipboard().
                getSelectedItems().contains(sn);
    }
}
