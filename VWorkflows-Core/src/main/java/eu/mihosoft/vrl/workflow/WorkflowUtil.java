/*
 * Copyright 2012-2021 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * Please cite the following publication(s):
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
 */
package eu.mihosoft.vrl.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Workflow utility class.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class WorkflowUtil {
    
    public static final String CONTROL_FLOW = "control";
    public static final String DATA_FLOW = "data";
    public static final String EVENT_FLOW = "event";
    

    private WorkflowUtil() {
        throw new AssertionError();
    }

    /**
     * Returns a predicate that indicates whether a connector is connected with
     * the specified connection type.
     *
     * <b>Note:</b> the predicate can be used to filter streams and collections.
     *
     * @param connectionType connection type (e.g. "data" or "control")
     * @return a predicate that indicates whether a connector is connected with
     * the specified connection type
     *
     * @see java.util.stream.Stream
     */
    public static Predicate<Connector> connectorConnected(String connectionType) {
        return (Connector c) -> {
            return c.getType().equals(connectionType)
                    && !c.getNode().getFlow().
                    getConnections(connectionType).
                    getAllWith(c).isEmpty();
        };
    }

    /**
     * Returns a predicate that indicates whether a connector is not connected
     * with the specified connection type.
     *
     * @param connectionType connection type (e.g. "data" or "control")
     * @return a predicate that indicates whether a connector is not connected
     * with the specified connection type
     *
     * @see java.util.stream.Stream
     */
    public static Predicate<Connector> connectorNotConnected(
            String connectionType) {
        return connectorConnected(connectionType).negate();
    }

    /**
     * Returns a predicate that indicates whether a node is connected with the
     * specified connection type.
     *
     * @param connectionType connection type (e.g. "data" or "control")
     * @return a predicate that indicates whether a node is connected with the
     * specified connection type
     *
     * @see java.util.stream.Stream
     */
    public static Predicate<VNode> nodeConnected(String connectionType) {
        return (VNode n) -> {
            return !n.getInputs().filtered(connectorConnected(connectionType)).
                    isEmpty();
        };
    }

    /**
     * Returns a predicate that indicates whether a node is not connected with
     * the specified connection type.
     *
     * @param connectionType connection type (e.g. "data" or "control")
     * @return a predicate that indicates whether a node is not connected with
     * the specified connection type
     *
     * @see java.util.stream.Stream
     */
    public static Predicate<VNode> nodeNotConnected(String connectionType) {
        return nodeConnected(connectionType).negate();
    }

    /**
     * Returns a predicate that indicates whether the number of connections of
     * the specified connector is bigger than the expected number of
     * connections. Only connections of the specifed type are counted.
     *
     * @param expectedNumConn expected number of connections
     * @param connectionType connection type (e.g. "data" or "control")
     * @return a predicate that indicates whether the number of connections of
     * the specified connector is bigger than the expected number of connections
     *
     * @see java.util.stream.Stream
     */
    public static Predicate<Connector> moreThanConnections(int expectedNumConn,
            String connectionType) {
        return (Connector c) -> {
            return c.getType().equals(connectionType)
                    && c.getNode().getFlow().
                    getConnections(connectionType).
                    getAllWith(c).size() > expectedNumConn;
        };
    }

    /**
     * Returns a predicate that indicates whether the number of connections of
     * the specified connector is smaller than the expected number of
     * connections. Only connections of the specifed type are counted.
     *
     * @param expectedNumConn expected number of connections
     * @param connectionType connection type (e.g. "data" or "control")
     * @return a predicate that indicates whether the number of connections of
     * the specified connector is smaller than the expected number of
     * connections
     *
     * @see java.util.stream.Stream
     */
    public static Predicate<Connector> lessThanConnections(int expectedNumConn,
            String connectionType) {
        return (Connector c) -> {
            return c.getType().equals(connectionType)
                    && c.getNode().getFlow().
                    getConnections(connectionType).
                    getAllWith(c).size() < expectedNumConn;
        };
    }

    /**
     * Returns a predicate that indicates whether the number of connections of
     * the specified connector is equal to the expected number of connections.
     * Only connections of the specifed type are counted.
     *
     * @param expectedNumConn expected number of connections
     * @param connectionType connection type (e.g. "data" or "control")
     * @return a predicate that indicates whether the number of connections of
     * the specified connector is equal to the expected number of connections
     *
     * @see java.util.stream.Stream
     */
    public static Predicate<Connector> numberOfConnections(int expectedNumConn,
            String connectionType) {
        return (Connector c) -> {
            return c.getType().equals(connectionType)
                    && c.getNode().getFlow().
                    getConnections(connectionType).
                    getAllWith(c).size() == expectedNumConn;
        };
    }
    
    public static boolean isRoot(VNode node, String connectionType) {

        Predicate<Connector> notConnected = (Connector c) -> {
            return c.getType().equals(connectionType)
                    && !c.getNode().getFlow().
                    getConnections(connectionType).
                    getAllWith(c).isEmpty();
        };

        Predicate<VNode> rootNode = (VNode n) -> {
            return n.getInputs().filtered(notConnected).isEmpty();
        };

        return rootNode.test(node);
    }
    
    public static List<VNode> getPathInLayerFromRoot(VNode sender, String connectionType) {

        List<VNode> result = new ArrayList<>();
        
        if (sender.getMainOutput(connectionType)==null) {
            return result;
        }

        if (!isRoot(sender, connectionType)) {
            System.err.println("sender is no root!");
            return result;
        }

        result.add(sender);

        Connections connections = sender.getFlow().getConnections(connectionType);
        Collection<Connection> connectionsWithSender
                = connections.getAllWith(sender.getMainOutput(connectionType));

        while (!connectionsWithSender.isEmpty()) {

            VNode newSender = null;

            for (Connection c : connectionsWithSender) {

                if (newSender == c.getReceiver().getNode()) {
                    System.err.println("circular flow!");
                    return result;
                }

                newSender = c.getReceiver().getNode();

                result.add(newSender);
                break; // we only support one connection per controlflow conector
            }

            if (newSender != null) {
                connectionsWithSender
                        = connections.getAllWith(
                                newSender.getMainOutput(connectionType));
            } else {
                connectionsWithSender.clear();
            }
        }

        return result;
    }
    
    
    public static List<ConnectionResult> connect(Connector s, Connector r) {
        List<ConnectionResult> result = new ArrayList<>();
        if (s.getNode().getFlow() == r.getNode().getFlow()) {
            result.add(s.getNode().getFlow().connect(s, r));
        }
        
        return result;
    }
    
    /**
     * Returns the ancestors of the specified node.
     * @param n node
     * @return the ancestors of the specified node
     */
    public static List<VFlowModel> getAncestors(VNode n) {
        List<VFlowModel> result = new ArrayList<>();
        
        VFlowModel parent = n.getFlow();
        
        while(parent!=null) {
            result.add(parent);
            parent = parent.getFlow();
        }
        
        return result;
    }
    
    /**
     * Returns the first common ancestor of the specified nodes if such a parent node
     * exists.
     * @param n1 first node
     * @param n2 second node
     * @return the common ancestor of the specified nodes if such a parent node
     * exists
     */
    public static Optional<VFlowModel> getCommonAncestor(VNode n1, VNode n2) {
        List<VFlowModel> ancestorsOfN1 = getAncestors(n1);
        List<VFlowModel> ancestorsOfN2 = getAncestors(n2);
        
        for(VFlowModel a1 : ancestorsOfN1) {
            for(VFlowModel a2 : ancestorsOfN2) {
                if (a1.equals(a2)) {
                    return Optional.of(a1);
                }
            }
        }
        
        return Optional.empty();
    }

}
