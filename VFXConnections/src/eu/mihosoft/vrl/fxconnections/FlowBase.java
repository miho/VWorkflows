/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FlowBase<T extends FlowNode> implements Flow<T> {

    private Connections connections = VFXConnections.newConnections();
    private Map<String, T> nodes = new HashMap<>();

    @Override
    public Connection connect(T s, T r) {

        nodes.put(s.getId(), s);
        nodes.put(r.getId(), r);

        return getConnections().add(s.getId(), r.getId());
    }

    @Override
    public Collection<T> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    @Override
    public T remove(T n) {
        T result = nodes.remove(n.getId());

        Collection<Connection> connectionsToRemove =
                getConnections().getAllWith(n.getId());

        for (Connection c : connectionsToRemove) {
            getConnections().remove(c);   
        }
        
        return result;
    }

    /**
     * @return the connections
     */
    public Connections getConnections() {
        return connections;
    }

}
