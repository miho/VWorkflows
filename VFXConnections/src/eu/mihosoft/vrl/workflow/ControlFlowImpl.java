/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.beans.property.ObjectProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ControlFlowImpl extends FlowBase implements ControlFlow {

    public ControlFlowImpl() {
        //
    }

    @Override
    public FlowNode remove(FlowNode n) {

        for (Connections cns : getAllConnections()) {

            // collect connections before removal
            Collection<Connection> allWidth = cns.getAllWith(n.getId());

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
                    cns.add(senders.get(i), receivers.get(i));
                }
            }

        }

        return super.remove(n);
    }

    @Override
    public FlowNode newNode() {
        return super.newNode(new ValueObject() {
            @Override
            public FlowNode getParent() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Object getValue() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setValue(Object o) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public ObjectProperty<Object> valueProperty() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public CompatibilityResult compatible(ValueObject other, String flowType) {
                return new CompatibilityResult() {
                    @Override
                    public boolean isCompatible() {
                        return true;
                    }

                    @Override
                    public String getMessage() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getStatus() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
            }

            @Override
            public VisualizationRequest getVisualizationRequest() {
                return new VisualizationRequest() {
                    @Override
                    public String getStyle() {
                        return "default";
                    }

                    @Override
                    public String getOptions() {
                        return "";
                    }
                };
            }
        });
    }
}
