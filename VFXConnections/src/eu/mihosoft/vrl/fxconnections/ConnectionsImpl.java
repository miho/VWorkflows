/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ConnectionsImpl implements Connections {

    private Map<String, Connection> connections = new HashMap<>();
    private Class<? extends Connection> connectionClass = ConnectionBase.class;

    private static String connectionId(String s, String r) {
        return s + ":" + r;
    }

    private static String connectionId(Connection c) {
        return c.getSenderId() + ":" + c.getReceiverId();
    }

    @Override
    public void addConnection(Connection c) {
        connections.put(connectionId(c), c);
    }

    @Override
    public void removeConnection(Connection c) {
        connections.remove(connectionId(c));
    }

    @Override
    public Connection getConnection(String s, String r) {
        return connections.get(connectionId(s, r));
    }

    @Override
    public void addConnection(String s, String r) {
        connections.put(connectionId(s, r), createConnection(s, r));
    }

    @Override
    public void removeConnection(String s, String r) {
        connections.remove(connectionId(s, r));
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
        
        Connection result=null;
        
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
}
