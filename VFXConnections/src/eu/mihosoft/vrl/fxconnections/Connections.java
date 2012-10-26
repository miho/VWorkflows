/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface Connections {
    public void add(Connection c);
    public void remove(Connection c);
    public Connection get(String s, String r);
    public void add(String s, String r);
    public void remove(String s, String r);
    
    public void setConnectionClass(Class<? extends Connection> cls);
    public Class<? extends Connection> getConnectionClass();
    
    public Iterable<Connection> getConnections();
    
    public Collection<Connection> getAllWith(String id);
}
