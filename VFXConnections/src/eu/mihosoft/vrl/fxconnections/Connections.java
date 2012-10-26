/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface Connections {
    public void addConnection(Connection c);
    public void removeConnection(Connection c);
    public Connection getConnection(String s, String r);
    public void addConnection(String s, String r);
    public void removeConnection(String s, String r);
    
    public void setConnectionClass(Class<? extends Connection> cls);
    public Class<? extends Connection> getConnectionClass();
}
