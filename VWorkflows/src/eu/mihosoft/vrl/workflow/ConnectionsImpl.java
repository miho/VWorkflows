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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ConnectionsImpl implements Connections {

    private String type;
    private Map<String, Connection> connections = new HashMap<>();
    private Class<? extends Connection> connectionClass = ConnectionBase.class;
//    Map<String, Integer> senders = new HashMap<>();
//    Map<String, Integer> receivers = new HashMap<>();
    private ObservableList<Connection> observableConnections =
            FXCollections.observableArrayList();
    private ObjectProperty<VisualizationRequest> vReq = new SimpleObjectProperty<>();

    //    private ObjectProperty<Skin> skinProperty = new SimpleObjectProperty<>();
    
    
    public ConnectionsImpl(String type) {
        this.type = type;
    }
    
    


    private static String connectionId(String id, String s, String r) {
        return "id=" + id + ";[" + s + "]->[" + r + "]";
    }

    private static String connectionId(Connection c) {
        return connectionId(c.getId(), c.getSenderId(), c.getReceiverId());
    }

    @Override
    public void add(Connection c) {

        checkUniqueness(c);

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
    public Connection add(String id, String s, String r, VisualizationRequest vReq) {
        Connection c = createConnection(id, s, r);
        c.setVisualizationRequest(vReq);
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
            Constructor constructor = cls.getConstructor(Connections.class, String.class, String.class, String.class, String.class);
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
            Constructor constructor = getConnectionClass().getConstructor(Connections.class, String.class, String.class, String.class, String.class);
            try {
                result = (Connection) constructor.newInstance(this, id, s, r, type);
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

    @Override
    public VisualizationRequest getVisualizationRequest() {
        return vReq.get();
    }

    /**
     * @param vReq the vReq to set
     */
    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        this.vReq.set(vReq);
    }
//    @Override
//    public void setSkin(Skin<?> skin) {
//        skinProperty.set(skin);
//    }
//
//    @Override
//    public Skin<?> getSkin() {
//        return skinProperty.get();
//    }
//
//    @Override
//    public ObjectProperty<?> skinProperty() {
//        return skinProperty;
//    }

    @Override
    public boolean isInputConnected(String id) {
        Collection<Connection> connectionsWith = getAllWith(id);

        for (Connection connection : connectionsWith) {
            if (connection.getReceiverId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isOutputConnected(String id) {
        Collection<Connection> connectionsWith = getAllWith(id);

        for (Connection connection : connectionsWith) {
            if (connection.getSenderId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean contains(String s, String r) {
        return getAll(s, r).iterator().hasNext();
    }

    /**
     * @return the type
     */
    @Override
    public String getType() {
        return type;
    }
    
    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();
        
        result.append("[ type: ").append(getType()).append('\n');
        
        for (Connection connection : observableConnections) {
            result.append(" --> ").append(connection.toString()).append('\n');
        }
        
        result.append("]");
        
        return result.toString();
    }

    @Override
    public ObjectProperty<VisualizationRequest> visualizationRequestProperty() {
        return this.vReq;
    }
}
