/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ClickEvent extends Event {

    public static final EventType<ClickEvent> ANY = new EventType<>(Event.ANY, "ClickEvent:ANY");

    private final Object object;
    private final MouseButton button;

    public ClickEvent(EventType<? extends Event> et, Object obj, MouseButton btn) {
        super(et);
        this.object = obj;
        this.button = btn;
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

    

}
