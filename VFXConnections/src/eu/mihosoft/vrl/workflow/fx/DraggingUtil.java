/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;


/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class DraggingUtil {

    /**
     * Makes a node draggable via mouse gesture.
     *
     * <p> <b>Note:</b> Existing Handlers will be overwritten!</p>
     *
     * @param n the node that shall be made draggable
     */
    public static void makeDraggable(final Node n) {

        MouseEventHandlerGroup dragHandler = new MouseEventHandlerGroup();
        MouseEventHandlerGroup pressHandler = new MouseEventHandlerGroup();
        n.setOnMouseDragged(dragHandler);
        n.setOnMousePressed(pressHandler);
        
        n.layoutXProperty().unbind();
        n.layoutYProperty().unbind();
        
        _makeDraggable(n, dragHandler, pressHandler);
    }
    

    public static void makeResizable(Node n) {
        
    }

    private static void _makeDraggable(
            final Node n,
            MouseEventHandlerGroup dragHandler,
            MouseEventHandlerGroup pressHandler) {


        DraggingControllerImpl draggingController =
                new DraggingControllerImpl();
        draggingController.apply(n, dragHandler, pressHandler);
    }
}
class MouseEventHandlerGroup extends EventHandlerGroup<MouseEvent> {
    //
}

class EventHandlerGroup<T extends Event> implements EventHandler<T> {

    private Collection<EventHandler<T>> handlers =
            new ArrayList<EventHandler<T>>();

    public void addHandler(EventHandler<T> eventHandler) {
        handlers.add(eventHandler);
    }

    @Override
    public void handle(T t) {
        for (EventHandler<T> eventHandler : handlers) {
            eventHandler.handle(t);
        }
    }
}

class DraggingControllerImpl {

    private double nodeX;
    private double nodeY;
    private double mouseX;
    private double mouseY;
    private EventHandler<MouseEvent> mouseDraggedEventHandler;
    private EventHandler<MouseEvent> mousePressedEventHandler;

    public DraggingControllerImpl() {
        //
    }

    public void apply(Node n,
            EventHandlerGroup<MouseEvent> draggedEvtHandler,
            EventHandlerGroup<MouseEvent> pressedEvtHandler) {
        init(n);
        draggedEvtHandler.addHandler(mouseDraggedEventHandler);
        pressedEvtHandler.addHandler(mousePressedEventHandler);
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

        double scaledX = nodeX * 1 / parentScaleX;
        double scaledY = nodeY * 1 / parentScaleY;

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

        nodeX = n.getLayoutX() * parentScaleX;
        nodeY = n.getLayoutY() * parentScaleY;

        n.toFront();
    }
}

class ResizingControllerImpl {

    private double nodeX;
    private double nodeY;
    private double mouseX;
    private double mouseY;
    private EventHandler<MouseEvent> mouseDraggedEventHandler;
    private EventHandler<MouseEvent> mousePressedEventHandler;

    public ResizingControllerImpl(Node n) {
    }

    public EventHandler<MouseEvent> getDraggingEventHandler() {
        return mouseDraggedEventHandler;
    }

    public void apply(Node n,
            EventHandlerGroup<MouseEvent> draggedEvtHandler,
            EventHandlerGroup<MouseEvent> pressedEvtHandler) {
        init(n);
        draggedEvtHandler.addHandler(mouseDraggedEventHandler);
        pressedEvtHandler.addHandler(mousePressedEventHandler);
    }

    private void init(final Node n) {
        mouseDraggedEventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                performDrag(n, event, mouseX, mouseY, nodeX, nodeY);

                event.consume();
            }
        };

        mousePressedEventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                performDragEnd(n, event, mouseX, mouseY, nodeX, nodeY);
                event.consume();
            }
        };
    }

    public static void performDrag(
            Node n, MouseEvent event,
            double mouseX, double mouseY,
            double nodeX, double nodeY) {
        final double parentScaleX = n.getParent().
                localToSceneTransformProperty().getValue().getMxx();
        final double parentScaleY = n.getParent().
                localToSceneTransformProperty().getValue().getMyy();

        // Get the exact moved X and Y

        double offsetX = event.getSceneX() - mouseX;
        double offsetY = event.getSceneY() - mouseY;

        nodeX += offsetX;
        nodeY += offsetY;

        double scaledX = nodeX * 1 / parentScaleX;
        double scaledY = nodeY * 1 / parentScaleY;

        n.setLayoutX(scaledX);
        n.setLayoutY(scaledY);

        // again set current Mouse x AND y position
        mouseX = event.getSceneX();
        mouseY = event.getSceneY();
    }

    public static void performDragEnd(
            Node n, MouseEvent event,
            double mouseX, double mouseY,
            double nodeX, double nodeY) {

        final double parentScaleX = n.getParent().
                localToSceneTransformProperty().getValue().getMxx();
        final double parentScaleY = n.getParent().
                localToSceneTransformProperty().getValue().getMyy();

        // record the current mouse X and Y position on Node
        mouseX = event.getSceneX();
        mouseY = event.getSceneY();

        nodeX = n.getLayoutX() * parentScaleX;
        nodeY = n.getLayoutY() * parentScaleY;

        n.toFront();
    }
}