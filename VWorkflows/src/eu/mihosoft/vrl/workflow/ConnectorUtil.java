/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ConnectorUtil {

    private static final String SPLITTER = "::";
    private static final String NODE_IDENTIFIER = "node:=";
    private static final String CONNECTOR_IDENTIFIER = "conn:=";

    static Connector getInput(FlowModel flow, String globalId) {

        String[] ids = splitGlobalId(globalId);
        String nodeId = ids[0];
        String connId = ids[1];

        FlowNode node = flow.getNodeLookup().getById(nodeId);
        Connector conn = node.getInputById(connId);

        return conn;
    }

    public static Connector getInput(FlowFlowNode flow, String globalId) {
        return getInput(flow, globalId);
    }

    static Connector getOutput(FlowModel flow, String globalId) {

        String[] ids = splitGlobalId(globalId);
        String nodeId = ids[0];
        String connId = ids[1];

        FlowNode node = flow.getNodeLookup().getById(nodeId);
        Connector conn = node.getOutputById(connId);

        return conn;
    }

    static Connector getOutput(FlowFlowNode flow, String globalId) {
        return getOutput(flow, globalId);
    }

    private static String[] splitGlobalId(String id) {
        String[] result = id.split(SPLITTER);

        if (result.length != 2) {
            throw new IllegalArgumentException("Illegal id specified: " + id);
        }

        result[0] = result[0].substring(NODE_IDENTIFIER.length());
        result[0] = result[0].substring(CONNECTOR_IDENTIFIER.length());

        return result;
    }

    public static String globalId(Connector c) {
        return NODE_IDENTIFIER
                + c.getParent().getId()
                + SPLITTER
                + CONNECTOR_IDENTIFIER + c.getId();
    }
}
