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
public class ConnectionEvent extends Event {

    public static final EventType<ConnectionEvent> ANY = new EventType<>(Event.ANY, "ConnectionEvent:ANY");
    public static final EventType<ConnectionEvent> ADD = new EventType<>(ANY, "ConnectionEvent:ADD");
    public static final EventType<ConnectionEvent> REMOVE = new EventType<>(ANY, "ConnectionEvent:REMOVE");
    private transient Connector sConnector;
    private transient Connector rConnector;

    public ConnectionEvent(EventType<? extends Event> et, Connector sConnector, Connector rConnector) {
        super(et);
        this.sConnector = sConnector;
        this.rConnector = rConnector;
    }

    /**
     * @return the source
     */
    public Connector getSenderConnector() {
        return this.sConnector;
    }

    /**
     * @return the receiver
     */
    public Connector getReceiverConnector() {
        return this.rConnector;
    }
}
