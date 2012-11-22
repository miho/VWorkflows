/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.Collection;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface Connections extends Model{

    public void add(Connection c);

    public void remove(Connection c);

    public Connection get(String id, String s, String r);

    public Iterable<Connection> getAll(String s, String r);

    public Connection add(String s, String r);

    public void remove(String id, String s, String r);

    public void removeAll(String s, String r);

    public void setConnectionClass(Class<? extends Connection> cls);

    public Class<? extends Connection> getConnectionClass();

    public ObservableList<Connection> getConnections();

    public Collection<Connection> getAllWith(String id);
}
