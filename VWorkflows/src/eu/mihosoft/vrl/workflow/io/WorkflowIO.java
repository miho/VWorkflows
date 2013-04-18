/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.io;

import com.thoughtworks.xstream.XStream;
import eu.mihosoft.vrl.workflow.DefaultValueObject;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.IdGenerator;
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

    public static VFlowModel loadFromXML(Path p, IdGenerator generator) throws IOException {

        XStream xstream = new XStream();

        configureStream(xstream);

        StringBuilder xml = new StringBuilder();

        for (String line : Files.readAllLines(p, StandardCharsets.UTF_8)) {
            xml.append(line).append("\n");
        }

        return flowFromPersistentFlow((PersistentFlow) xstream.fromXML(xml.toString()), generator);
    }

    private static void configureStream(XStream xstream) {
        xstream.alias("flow", PersistentFlow.class);
        xstream.alias("node", PersistentNode.class);
        xstream.alias("connection", PersistentConnection.class);
        xstream.alias("vobj", DefaultValueObject.class);

//        xstream.setMode(XStream.ID_REFERENCES);
    }

    public static void saveToXML(Path p, VFlowModel flow) throws IOException {
        XStream xstream = new XStream();

        configureStream(xstream);

        String xml = xstream.toXML(toPersistentNode(flow, null));

        InputStream is = new ByteArrayInputStream(xml.getBytes());
        Files.copy(is, p, StandardCopyOption.REPLACE_EXISTING);
    }

    public static PersistentNode toPersistentNode(VNode node, PersistentFlow parent) {

        if (node instanceof VFlowModel) {

            VFlowModel flow = (VFlowModel) node;

            List<PersistentConnection> connectionList = new ArrayList<>();

            for (eu.mihosoft.vrl.workflow.Connections connections : flow.getAllConnections().values()) {
                for (eu.mihosoft.vrl.workflow.Connection c : connections.getConnections()) {
                    connectionList.add(
                            new PersistentConnection(c.getId(),
                            c.getSenderId(),
                            c.getReceiverId(),
                            c.getType(),
                            c.getVisualizationRequest()));
                }
            }

            List<PersistentNode> nodeList = new ArrayList<>();

            List<String> connectionTypes = new ArrayList<>();

            connectionTypes.addAll(flow.getAllConnections().keySet());

            PersistentFlow pFlow = new PersistentFlow(parent,
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
                    node.getVisualizationRequest(),
                    node.getInputTypes(), node.getOutputTypes());

            for (VNode n : flow.getNodes()) {
                nodeList.add(toPersistentNode(n, pFlow));
            }

            return pFlow;
        } else {
            return new PersistentNode(node.getId(),
                    node.getTitle(),
                    node.getX(),
                    node.getY(),
                    node.getWidth(),
                    node.getHeight(),
                    node.getValueObject(),
                    node.getVisualizationRequest(),
                    node.getInputTypes(), node.getOutputTypes());
        }
    }

    public static VFlowModel flowFromPersistentFlow(
            PersistentFlow flow, IdGenerator generator) {
        return createFlowFromPersistent(flow, null, generator);
    }

    private static VFlowModel createFlowFromPersistent(
            PersistentFlow flow, VFlowModel parent, IdGenerator generator) {

        VFlowModel result = null;

        if (parent == null) {
            result = FlowFactory.newFlowModel();
            result.setIdGenerator(generator);
        } else {
            result = parent.newFlowNode();
        }

        result.setId(flow.getId());
        generator.addId(flow.getId());
        result.setTitle(flow.getTitle());
        result.setX(flow.getX());
        result.setY(flow.getY());
        result.setWidth(flow.getWidth());
        result.setHeight(flow.getHeight());
        result.setValueObject(flow.getValueObject());
        result.setVisible(flow.isVisible());
        result.setVisualizationRequest(flow.getVReq());

        for (String input : flow.getInputTypes()) {
            result.setInput(true, input);
        }

        for (String output : flow.getOutputTypes()) {
            result.setOutput(true, output);
        }

        Map<String, List<PersistentConnection>> flowConnections = new HashMap<>();

        for (String type : flow.getConnectionTypes()) {
            flowConnections.put(type, new ArrayList<PersistentConnection>());
        }

        for (PersistentConnection c : flow.getConnections()) {
            flowConnections.get(c.getType()).add(c);
        }

        for (String type : flowConnections.keySet()) {
            List<PersistentConnection> connections = flowConnections.get(type);
            result.addConnections(fromPersistentConnections(type, connections), type);
        }

        for (PersistentNode n : flow.getNodes()) {
            addFlowNode(result, n, generator);
        }

        return result;
    }

    private static void addFlowNode(VFlowModel flow, PersistentNode node, IdGenerator generator) {

        if (node instanceof PersistentFlow) {
            createFlowFromPersistent((PersistentFlow) node, flow, generator);
        } else {
            VNode result = flow.newNode();
            result.setId(node.getId());
            generator.addId(node.getId());
            result.setTitle(node.getTitle());
            result.setX(node.getX());
            result.setY(node.getY());
            result.setWidth(node.getWidth());
            result.setHeight(node.getHeight());
            result.setValueObject(node.getValueObject());
            result.setVisualizationRequest(node.getVReq());

            for (String input : node.getInputTypes()) {
                result.setInput(true, input);
            }

            for (String output : node.getOutputTypes()) {
                result.setOutput(true, output);
            }
        }
    }

    public static PersistentConnection toPersistentConnection(eu.mihosoft.vrl.workflow.Connection c) {
        return new PersistentConnection(c.getId(), c.getSenderId(), c.getReceiverId(), c.getType(), c.getVisualizationRequest());
    }

    public static eu.mihosoft.vrl.workflow.Connections fromPersistentConnections(String connectionType, List<PersistentConnection> connections) {
        eu.mihosoft.vrl.workflow.Connections result = VConnections.newConnections(connectionType);

        for (PersistentConnection c : connections) {
            result.add(c.getId(), c.getSenderId(), c.getReceiverId(), c.getVReq());
        }

        return result;
    }
    
    public static <T> List<T> listToSerializableList(List<T> input) {
        List<T> result = new ArrayList<>();
        result.addAll(input);
        return result;
    }
}
