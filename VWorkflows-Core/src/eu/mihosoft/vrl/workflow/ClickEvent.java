/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ClickEvent extends Event {

    public static final EventType<ClickEvent> ANY = new EventType<>(Event.ANY, "ClickEvent:ANY");

    private final Object object;
    private final MouseButton button;
    private final Object event;

    public ClickEvent(EventType<? extends Event> et, Object obj, MouseButton btn, Object evt) {
        super(et);
        this.object = obj;
        this.button = btn;
        this.event = evt;
    }

    /**
     * @return the object
     */
    public Object getObject() {
        return object;
    }

    /**
     * @return the button
     */
    public MouseButton getButton() {
        return button;
    }

    /**
     * @return the event
     */
    public Object getEvent() {
        return event;
    }

    

}
