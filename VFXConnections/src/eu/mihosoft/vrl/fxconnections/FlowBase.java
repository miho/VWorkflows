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
public class FlowBase implements Flow {

    private Connections connections = VFXConnections.newConnections();
    private Map<String, FlowNode> nodes = new HashMap<>();

    @Override
    public ConnectionResult tryConnect(FlowNode s, FlowNode r) {
        CompatibilityResult result = r.getValueObject().
                compatible(s.getValueObject());
        
        return new ConnectionResultImpl(result, null);
    }
    
    @Override
    public ConnectionResult connect(FlowNode s, FlowNode r) {

        ConnectionResult result = tryConnect(s, r);
        
        if (result.getStatus().isCompatible()) {
            return result;
        }

        nodes.put(s.getId(), s);
        nodes.put(r.getId(), r);

        Connection connection = getConnections().add(s.getId(), r.getId());
        
        return new ConnectionResultImpl(result.getStatus(), connection);
    }

    @Override
    public Collection<FlowNode> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    @Override
    public FlowNode remove(FlowNode n) {
        FlowNode result = nodes.remove(n.getId());

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
