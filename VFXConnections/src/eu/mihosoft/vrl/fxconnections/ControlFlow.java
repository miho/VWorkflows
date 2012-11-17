/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ControlFlow extends FlowBase<FlowNode> {

    public ControlFlow() {
        //
    }

    @Override
    public FlowNode remove(FlowNode n) {

        // collect connections before removal
        Collection<Connection> allWidth = getConnections().getAllWith(n.getId());

        // collect all senders and receivers of the node that shall be removed
        List<String> senders = new ArrayList<>();
        List<String> receivers = new ArrayList<>();
        for (Connection c : allWidth) {
            senders.add(c.getSenderId());
            receivers.add(c.getReceiverId());
        }

        // if #senders equals #receivers reconnect senders of the node that 
        // shall be removed width its receivers
        if (senders.size() == receivers.size()) {
            for (int i = 0; i < senders.size(); i++) {
                getConnections().add(senders.get(i), receivers.get(i));
            }
        }

        return super.remove(n);
    }
}
