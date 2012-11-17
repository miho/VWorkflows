/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ConnectionsImpl implements Connections {

    private Map<String, Connection> connections = new HashMap<>();
    private Class<? extends Connection> connectionClass = ConnectionBase.class;

    Map<String, Integer> senders = new HashMap<>();
    Map<String, Integer> receivers = new HashMap<>();

    private static String connectionId(String s, String r) {
        return s + ":" + r;
    }

    private static String connectionId(Connection c) {
        return c.getSenderId() + ":" + c.getReceiverId();
    }

    @Override
    public void add(Connection c) {

        checkUniqueness(c);

        incSenderCounter(c.getSenderId());
        incReceiverCounter(c.getReceiverId());

        connections.put(connectionId(c), c);
    }
    
    @Override
    public void add(String s, String r) {
        add(createConnection(s, r));
    }

    @Override
    public void remove(Connection c) {
        connections.remove(connectionId(c));

        decSenderCounter(c.getSenderId());
        decReceiverCounter(c.getReceiverId());
    }

    @Override
    public Connection get(String s, String r) {
        return connections.get(connectionId(s, r));
    }

    @Override
    public void remove(String s, String r) {
        connections.remove(connectionId(s, r));

        decSenderCounter(s);
        decReceiverCounter(r);
    }

    @Override
    public void setConnectionClass(Class<? extends Connection> cls) {
        try {
            Constructor constructor = cls.getConstructor(String.class, String.class);
            throw new IllegalArgumentException("constructor missing: (String, String)");
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(ConnectionsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.connectionClass = cls;
    }

    @Override
    public Class<? extends Connection> getConnectionClass() {
        return connectionClass;
    }

    private Connection createConnection(String s, String r) {

        Connection result = null;

        try {
            Constructor constructor = getConnectionClass().getConstructor(String.class, String.class);
            try {
                result = (Connection) constructor.newInstance(s, r);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ConnectionsImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(ConnectionsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    @Override
    public Iterable<Connection> getConnections() {
        return connections.values();
    }

    @Override
    public Collection<Connection> getAllWith(String id) {

        Collection<Connection> result = new ArrayList<>();

        for (Connection c : getConnections()) {
            if (c.getSenderId().equals(id) || c.getReceiverId().equals(id)) {
                result.add(c);
            }
        }

        return result;
    }

    private void checkUniqueness(Connection c) {
        
        if (c.getSenderId().equals(c.getReceiverId())) {
            throw new IllegalStateException(
                    "Cannot add connection: sender and receiver are equal: " + c.getSenderId());
        }
        
        Integer senderCount = senders.get(c.getReceiverId());
        Integer receiverCount = receivers.get(c.getSenderId());
        
        if (senderCount!=null && senderCount > 0) {
            throw new IllegalStateException(
                    "Cannot add receiver: a sender with the name \""
                    + c.getReceiverId()
                    + "\" already exists!");
        }
        
        if (receiverCount!=null && receiverCount > 0) {
             throw new IllegalStateException(
                    "Cannot add sender: a receiver with the name \""
                    + c.getReceiverId()
                    + "\" already exists!");
        }
    }

    private void incSenderCounter(String id) {
        Integer count = senders.get(id);

        if (count == null) {
            count = 0;
        }

        count++;

        senders.put(id, count);
    }

    private void incReceiverCounter(String id) {
        Integer count = receivers.get(id);

        if (count == null) {
            count = 0;
        }

        count++;

        receivers.put(id, count);
    }

    private void decSenderCounter(String id) {
        Integer count = senders.get(id);

        if (count == null) {
            count = 0;
        } else {
            count--;
        }

        senders.put(id, count);
    }

    private void decReceiverCounter(String id) {
        Integer count = receivers.get(id);

        if (count == null) {
            count = 0;
        } else {
            count--;
        }

        receivers.put(id, count);
    }
}
