/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ConnectionsImpl implements Connections {

    private Map<String, Connection> connections = new HashMap<>();
    private Class<? extends Connection> connectionClass = ConnectionBase.class;
//    Map<String, Integer> senders = new HashMap<>();
//    Map<String, Integer> receivers = new HashMap<>();
    private ObservableList<Connection> observableConnections =
            FXCollections.observableArrayList();

    private static String connectionId(String id, String s, String r) {
        return "id=" + id + ";[" + s + "]->[" + r + "]";
    }

    private static String connectionId(Connection c) {
        return connectionId(c.getId(), c.getSenderId(), c.getReceiverId());
    }

    @Override
    public void add(Connection c) {

        checkUniqueness(c);

//        incSenderCounter(c.getSenderId());
//        incReceiverCounter(c.getReceiverId());

        connections.put(connectionId(c), c);
        
        observableConnections.add(c);
    }

    @Override
    public Connection add(String s, String r) {

        // search id:
        String id = "0";
        int count = 0;

        while (connections.containsKey(connectionId(id, s, r))) {
            count++;
            id = "" + count;
        }
        
        Connection c = createConnection(id, s, r);

        add(c);
        
        return c;
    }

    @Override
    public void remove(Connection c) {
        connections.remove(connectionId(c));
        
        observableConnections.remove(c);

//        decSenderCounter(c.getSenderId());
//        decReceiverCounter(c.getReceiverId());
    }

    @Override
    public Connection get(String id, String s, String r) {
        return connections.get(connectionId(id, s, r));
    }

    @Override
    public void remove(String id, String s, String r) {
        
        observableConnections.remove(get(id, s, r));
        
        connections.remove(connectionId(id, s, r));

        
//        decSenderCounter(s);
//        decReceiverCounter(r);
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

    private Connection createConnection(String id, String s, String r) {

        Connection result = null;

        try {
            Constructor constructor = getConnectionClass().getConstructor(String.class, String.class, String.class);
            try {
                result = (Connection) constructor.newInstance(id, s, r);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ConnectionsImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(ConnectionsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    @Override
    public ObservableList<Connection> getConnections() {
        return observableConnections;
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
        
        if (connections.containsKey(connectionId(c))) {
            throw new IllegalStateException(
                    "Cannot add connection: a connection with equal id already added!");
        }
        
//        if (c.getSenderId().equals(c.getReceiverId())) {
//            throw new IllegalStateException(
//                    "Cannot add connection: sender and receiver are equal: " + c.getSenderId());
//        }
//
//        Integer senderCount = senders.get(c.getReceiverId());
//        Integer receiverCount = receivers.get(c.getSenderId());
//
//        if (senderCount != null && senderCount > 0) {
//            throw new IllegalStateException(
//                    "Cannot add receiver: a sender with the name \""
//                    + c.getReceiverId()
//                    + "\" already exists!");
//        }
//
//        if (receiverCount != null && receiverCount > 0) {
//            throw new IllegalStateException(
//                    "Cannot add sender: a receiver with the name \""
//                    + c.getReceiverId()
//                    + "\" already exists!");
//        }
    }
//    private void incSenderCounter(String id) {
//        Integer count = senders.get(id);
//
//        if (count == null) {
//            count = 0;
//        }
//
//        count++;
//
//        senders.put(id, count);
//    }
//
//    private void incReceiverCounter(String id) {
//        Integer count = receivers.get(id);
//
//        if (count == null) {
//            count = 0;
//        }
//
//        count++;
//
//        receivers.put(id, count);
//    }
//
//    private void decSenderCounter(String id) {
//        Integer count = senders.get(id);
//
//        if (count == null) {
//            count = 0;
//        } else {
//            count--;
//        }
//
//        senders.put(id, count);
//    }
//
//    private void decReceiverCounter(String id) {
//        Integer count = receivers.get(id);
//
//        if (count == null) {
//            count = 0;
//        } else {
//            count--;
//        }
//
//        receivers.put(id, count);
//    }

    @Override
    public Iterable<Connection> getAll(String s, String r) {

        Collection<Connection> result = new ArrayList<>();

        for (Connection c : connections.values()) {
            if (c.getSenderId().equals(s) && c.getReceiverId().equals(r)) {
                result.add(c);
            }
        }
        
        return result;
    }

    @Override
    public void removeAll(String s, String r) {
        
        Collection<Connection> delList = new ArrayList<>();

        for (Connection c : connections.values()) {
            if (c.getSenderId().equals(s) && c.getReceiverId().equals(r)) {
                delList.add(c);
            }
        }
        
        for (Connection connection : delList) {
            remove(connection);
        }
    }
}
