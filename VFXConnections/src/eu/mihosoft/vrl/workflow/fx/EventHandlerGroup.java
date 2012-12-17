/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import java.util.ArrayList;
import java.util.Collection;
import javafx.event.Event;
import javafx.event.EventHandler;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class EventHandlerGroup<T extends Event> implements EventHandler<T> {

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