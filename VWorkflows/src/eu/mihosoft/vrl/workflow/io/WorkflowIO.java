/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.io;

import com.thoughtworks.xstream.XStream;
import eu.mihosoft.vrl.workflow.EmptyValueObject;
import eu.mihosoft.vrl.workflow.FlowController;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.FlowFlowNode;
import eu.mihosoft.vrl.workflow.FlowNode;
import eu.mihosoft.vrl.workflow.VConnections;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class WorkflowIO {

    public static FlowFlowNode loadFromXML(Path p) throws IOException {

        XStream xstream = new XStream();

        configureStream(xstream);

        StringBuilder xml = new StringBuilder();

        for (String line : Files.readAllLines(p, StandardCharsets.UTF_8)) {
            xml.append(line).append("\n");
        }

        return flowFromPersistentFlow((Flow) xstream.fromXML(xml.toString()));
    }

    private static void configureStream(XStream xstream) {
        xstream.alias("flow", Flow.class);
        xstream.alias("node", Node.class);
        xstream.alias("connection", Connection.class);
        xstream.alias("empty", EmptyValueObject.class);

//        xstream.setMode(XStream.ID_REFERENCES);
    }

    public static void saveToXML(Path p, FlowFlowNode flow) throws IOException {
        XStream xstream = new XStream();


        configureStream(xstream);

        String xml = xstream.toXML(toPersistentNode(flow, null));

        InputStream is = new ByteArrayInputStream(xml.getBytes());
        Files.copy(is, p, StandardCopyOption.REPLACE_EXISTING);
    }

    public static Node toPersistentNode(FlowNode node, Flow parent) {

        if (node instanceof FlowFlowNode) {

            FlowFlowNode flow = (FlowFlowNode) node;

            List<Connection> connectionList = new ArrayList<>();

            for (eu.mihosoft.vrl.workflow.Connections connections : flow.getAllConnections().values()) {
                for (eu.mihosoft.vrl.workflow.Connection c : connections.getConnections()) {
                    connectionList.add(
                            new Connection(c.getId(),
                            c.getSenderId(),
                            c.getReceiverId(),
                            c.getType(),
                            c.getVisualizationRequest()));
                }
            }

            List<Node> nodeList = new ArrayList<>();

            List<String> connectionTypes = new ArrayList<>();

            connectionTypes.addAll(flow.getAllConnections().keySet());

            Flow pFlow = new Flow(parent,
                    node.getId(),
                    connectionTypes,
                    connectionList,
                    nodeList,
                    node.getTitle(),
                    node.getX(),
                    node.getY(),
                    node.getWidth(),
                    node.getHeight(),
                    node.getValueObject(),
                    flow.isVisible(),
                    node.getVisualizationRequest());

            for (FlowNode n : flow.getNodes()) {
                nodeList.add(toPersistentNode(n, pFlow));
            }

            return pFlow;
        } else {
            return new Node(node.getId(),
                    node.getTitle(),
                    node.getX(),
                    node.getY(),
                    node.getWidth(),
                    node.getHeight(),
                    node.getValueObject(),
                    node.getVisualizationRequest());
        }
    }

    public static FlowFlowNode flowFromPersistentFlow(Flow flow) {
        return createFlowFromPersistent(flow, null);
    }

    private static FlowFlowNode createFlowFromPersistent(Flow flow, FlowFlowNode parent) {

        FlowFlowNode result = null;

        if (parent == null) {
            result = FlowFactory.newFlowModel();
        } else {
            result = parent.newFlowNode();
        }

        result.setId(flow.getId());
        result.setTitle(flow.getTitle());
        result.setX(flow.getX());
        result.setY(flow.getY());
        result.setWidth(flow.getWidth());
        result.setHeight(flow.getHeight());
        result.setValueObject(flow.getValueObject());
        result.setVisible(flow.isVisible());
        result.setVisualizationRequest(flow.getVReq());

        Map<String, List<Connection>> flowConnections = new HashMap<>();

        for (String type : flow.getConnectionTypes()) {
            flowConnections.put(type, new ArrayList<Connection>());
        }

        for (Connection c : flow.getConnections()) {
            flowConnections.get(c.getType()).add(c);
        }

        for (String type : flowConnections.keySet()) {
            List<Connection> connections = flowConnections.get(type);
            result.addConnections(fromPersistentConnections(type, connections), type);
        }

        for (Node n : flow.getNodes()) {
            addFlowNode(result, n);
        }

        return result;
    }

    private static void addFlowNode(FlowFlowNode flow, Node node) {

        if (node instanceof Flow) {
            createFlowFromPersistent((Flow) node, flow);
        } else {
            FlowNode result = flow.newNode();
            result.setId(node.getId());
            result.setTitle(node.getTitle());
            result.setX(node.getX());
            result.setY(node.getY());
            result.setWidth(node.getWidth());
            result.setHeight(node.getHeight());
            result.setValueObject(flow.getValueObject());
            result.setVisualizationRequest(node.getVReq());
        }
    }

    public static Connection toPersistentConnection(eu.mihosoft.vrl.workflow.Connection c) {
        return new Connection(c.getId(), c.getSenderId(), c.getReceiverId(), c.getType(), c.getVisualizationRequest());
    }

    public static eu.mihosoft.vrl.workflow.Connections fromPersistentConnections(String connectionType, List<Connection> connections) {
        eu.mihosoft.vrl.workflow.Connections result = VConnections.newConnections(connectionType);

        for (Connection c : connections) {
            result.add(c.getId(), c.getSenderId(), c.getReceiverId(), c.getVReq());
        }

        return result;
    }
}
