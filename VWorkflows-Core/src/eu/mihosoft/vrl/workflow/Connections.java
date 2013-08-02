/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.Collection;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public interface Connections extends Model {

    public void add(Connection c);

    public Connection add(String s, String r);

    public Connection add(String id, String s, String r, VisualizationRequest vReq);

    public void remove(Connection c);

    public Connection get(String id, String s, String r);

    public Iterable<Connection> getAll(String s, String r);

    public void remove(String id, String s, String r);

    public void removeAll(String s, String r);

    public void setConnectionClass(Class<? extends Connection> cls);

    public Class<? extends Connection> getConnectionClass();

    public ObservableList<Connection> getConnections();

    public Collection<Connection> getAllWith(String id);

    public boolean isInputConnected(String id);

    public boolean isOutputConnected(String id);

    public boolean contains(String s, String r);

    public String getType();
}
