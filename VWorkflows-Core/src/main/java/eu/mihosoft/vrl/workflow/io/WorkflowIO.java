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
package eu.mihosoft.vrl.workflow.io;

import com.thoughtworks.xstream.XStream;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.DefaultValueObject;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.IdGenerator;
import eu.mihosoft.vrl.workflow.NodeLookupImpl;
import eu.mihosoft.vrl.workflow.IOConnector;
import eu.mihosoft.vrl.workflow.ThruConnector;
import eu.mihosoft.vrl.workflow.ThruConnectorImpl;
import eu.mihosoft.vrl.workflow.VConnections;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.ValueObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class WorkflowIO {
    
    public static VFlow loadFromXML(Path p) throws IOException {
        VFlow workflow = FlowFactory.newFlow();
        
        VFlowModel flow = WorkflowIO.loadFromXML(p, workflow.getIdGenerator());
        workflow.setNodeLookup(flow.getNodeLookup());
        workflow.setModel(flow);
        
        return workflow;
    }
    
    public static VFlowModel loadFromXML(String xml, IdGenerator generator) {
        XStream xstream = new XStream();
        
        configureStream(xstream);
        
        return flowFromPersistentFlow((PersistentFlow) xstream.fromXML(xml), generator);
    }
    
    public static VFlow loadFromXML(String xml) {
        VFlow workflow = FlowFactory.newFlow();
        
        VFlowModel flow = WorkflowIO.loadFromXML(xml, workflow.getIdGenerator());
        workflow.setNodeLookup(flow.getNodeLookup());
        workflow.setModel(flow);
        
        return workflow;
    }
    
    public static VFlow loadFromXML(InputStream xmlStream) {
        VFlow workflow = FlowFactory.newFlow();
        
        VFlowModel flow = WorkflowIO.loadFromXML(xmlStream, workflow.getIdGenerator());
        workflow.setNodeLookup(flow.getNodeLookup());
        workflow.setModel(flow);
        
        return workflow;
    }
    
    public static VFlowModel loadFromXML(Path p, IdGenerator generator) throws IOException {
        
        XStream xstream = new XStream();
        
        configureStream(xstream);
        
        InputStream is = Files.newInputStream(p, StandardOpenOption.READ);
        
        PersistentFlow pFlow = (PersistentFlow) xstream.fromXML(is);
        
        return flowFromPersistentFlow(pFlow, generator);
    }
    
    public static VFlowModel loadFromXML(InputStream xmlStream, IdGenerator generator) {
        
        XStream xstream = new XStream();
        
        configureStream(xstream);
        
        return flowFromPersistentFlow((PersistentFlow) xstream.fromXML(xmlStream), generator);
    }
    
    private static void configureStream(XStream xstream) {
        xstream.alias("flow", PersistentFlow.class);
        xstream.alias("node", PersistentNode.class);
        xstream.alias("connection", PersistentConnection.class);
        xstream.alias("vobj", PersistentValueObject.class);
        xstream.alias("connector", PersistentConnector.class);

//        xstream.setMode(XStream.ID_REFERENCES);
    }
    
    public static void saveToXML(Path p, VFlowModel flow) throws IOException {
        
        OutputStream os = Files.newOutputStream(p,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
        
        saveToXML(flow, os);
    }
    
    public static void saveToXML(VFlowModel flow, OutputStream xmlStream) {
        XStream xstream = new XStream();
        
        configureStream(xstream);
        
        xstream.toXML(toPersistentNode(flow, null), xmlStream);
    }
    
    public static String saveToXML(VFlowModel flow) {
        XStream xstream = new XStream();
        
        configureStream(xstream);
        
        String xml = xstream.toXML(toPersistentNode(flow, null));
        
        return xml;
    }
    
    public static PersistentNode toPersistentNode(VNode node, PersistentFlow parent) {
        
        if (node instanceof VFlowModel) {
            
            VFlowModel flow = (VFlowModel) node;
            
            List<PersistentConnection> connectionList = new ArrayList<>();
            
            for (eu.mihosoft.vrl.workflow.Connections connections : flow.getAllConnections().values()) {
                for (eu.mihosoft.vrl.workflow.Connection c : connections.getConnections()) {
                    connectionList.add(
                            new PersistentConnection(c.getId(),
                                    c.getSender().getId(),
                                    c.getReceiver().getId(),
                                    c.getType(),
                                    c.getVisualizationRequest()));
                }
            }
            
            List<PersistentNode> nodeList = new ArrayList<>();
            
            List<String> connectionTypes = new ArrayList<>();
            
            connectionTypes.addAll(flow.getAllConnections().keySet());
            
            PersistentFlow pFlow = new PersistentFlow(parent,
                    node.getId(),
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
                    new ArrayList<PersistentConnector>());
            
            for (Connector c : node.getConnectors()) {
                pFlow.addConnector(toPersistentConnector(c));
            }
            
            for (String mainType : node.getMainInputTypes()) {
                pFlow.getMainInputs().put(mainType, node.getMainInput(mainType).getLocalId());
            }
            
            for (String mainType : node.getMainOutputTypes()) {
                pFlow.getMainOutputs().put(mainType, node.getMainOutput(mainType).getLocalId());
            }
            
            for (VNode n : flow.getNodes()) {
                nodeList.add(toPersistentNode(n, pFlow));
            }
            
            return pFlow;
        } else {
            PersistentNode pNode = new PersistentNode(node.getId(),
                    node.getTitle(),
                    node.getX(),
                    node.getY(),
                    node.getWidth(),
                    node.getHeight(),
                    node.getValueObject(),
                    node.getVisualizationRequest(),
                    new ArrayList<PersistentConnector>());
            
            for (Connector c : node.getConnectors()) {
                pNode.addConnector(toPersistentConnector(c));
            }
            
            for (String mainType : node.getMainInputTypes()) {
                pNode.getMainInputs().put(mainType, node.getMainInput(mainType).getLocalId());
            }
            
            for (String mainType : node.getMainOutputTypes()) {
                pNode.getMainOutputs().put(mainType, node.getMainOutput(mainType).getLocalId());
            }
            
            return pNode;
        }
    }
    
    public static VFlowModel flowFromPersistentFlow(
            PersistentFlow flow, IdGenerator generator) {
        
        VFlowModel flowModel = createFlowFromPersistent(flow, null, generator);
        
        addConnectionsFromPersistentFlow(flow, flowModel, generator);
        
        return flowModel;
    }
    
    private static void addConnectionsFromPersistentFlow(PersistentFlow flow, VFlowModel flowModel, IdGenerator generator) {
        
        Map<String, List<PersistentConnection>> flowConnections = new HashMap<>();
        
        for (PersistentConnection c : flow.getConnections()) {
            List<PersistentConnection> connectionsOfType = flowConnections.get(c.getType());
            
            if (connectionsOfType == null) {
                connectionsOfType = new ArrayList<>();
                flowConnections.put(c.getType(), connectionsOfType);
            }
            connectionsOfType.add(c);
        }
        
        for (String type : flowConnections.keySet()) {
            List<PersistentConnection> connections = flowConnections.get(type);
            if (!connections.isEmpty()) {
                flowModel.addConnections(fromPersistentConnections(type, connections, flowModel), type);
            }
        }
        
        for (PersistentNode pn : flow.getNodes()) {
            VNode fn = flowModel.getNodeLookup().getById(pn.getId());
            
            if (fn instanceof VFlowModel && pn instanceof PersistentFlow) {
                addConnectionsFromPersistentFlow((PersistentFlow) pn, (VFlowModel) fn, generator);
            }
        }
    }
    
    private static VFlowModel createFlowFromPersistent(
            PersistentFlow flow, VFlowModel parent, IdGenerator generator) {
        
        VFlowModel result;
        
        if (parent == null) {
            result = FlowFactory.newFlowModel();
            result.setIdGenerator(generator);
            result.setNodeLookup(new NodeLookupImpl(result));
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
        result.setValueObject(toValueObject(result, flow.getValueObject()));
        result.setVisible(flow.isVisible());
        result.setVisualizationRequest(flow.getVReq());
        
        for (PersistentNode n : flow.getNodes()) {
            addFlowNode(result, n, generator);
        }
        
        for (PersistentConnector connector : flow.getConnectors()) {
            result.addConnector(fromPersistentConnector(connector, result));
        }
        
        for (String type : flow.getMainInputs().keySet()) {
            result.setMainInput(result.getConnector(flow.getMainInputs().get(type)));
        }
        
        for (String type : flow.getMainOutputs().keySet()) {
            result.setMainOutput(result.getConnector(flow.getMainOutputs().get(type)));
        }
//
//        Map<String, List<PersistentConnection>> flowConnections = new HashMap<>();
//
//        for (PersistentConnection c : flow.getConnections()) {
//            List<PersistentConnection> connectionsOfType = flowConnections.get(c.getType());
//
//            if (connectionsOfType == null) {
//                connectionsOfType = new ArrayList<>();
//                flowConnections.put(c.getType(), connectionsOfType);
//            }
//            connectionsOfType.add(c);
//        }
//
//        for (String type : flowConnections.keySet()) {
//            List<PersistentConnection> connections = flowConnections.get(type);
//            if (!connections.isEmpty()) {
//                result.addConnections(fromPersistentConnections(type, connections, result), type);
//            }
//        }

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
            result.setValueObject(toValueObject(result, node.getValueObject()));
            result.setVisualizationRequest(node.getVReq());
            
            for (PersistentConnector c : node.getConnectors()) {
                result.addConnector(fromPersistentConnector(c, result));
            }
        }
    }

//    public static PersistentConnection toPersistentConnection(eu.mihosoft.vrl.workflow.Connection c) {
//        return new PersistentConnection(c.getId(), c.getSenderId(), c.getReceiverId(), c.getType(), c.getVisualizationRequest());
//    }
    public static eu.mihosoft.vrl.workflow.Connections fromPersistentConnections(String connectionType, List<PersistentConnection> connections, VFlowModel flow) {
        eu.mihosoft.vrl.workflow.Connections result = VConnections.newConnections(connectionType);
        
        for (PersistentConnection c : connections) {
            Connector s = flow.getNodeLookup().getConnectorById(c.getSenderId());
            Connector r = flow.getNodeLookup().getConnectorById(c.getReceiverId());
            result.add(c.getId(), s, r, c.getVReq());
        }
        
        return result;
    }
    
    public static <T> List<T> listToSerializableList(List<T> input) {
        List<T> result = new ArrayList<>();
        result.addAll(input);
        return result;
    }
    
    public static Connector fromPersistentConnector(PersistentConnector pC, VNode n) {

//        ConnectorIOImpl result
//                = new ConnectorImpl(n, c.getType(), c.getLocalId(), c.isInput(), c.isOutput());
//        
        Connector c;
        if (pC.isPassthru()) {
            
            VFlowModel flowModel = (VFlowModel) n;
            
            if (pC.isInput()) {
                VNode innerNode = flowModel.newNode();
                Connector innerConnector = innerNode.setMainInput(
                        innerNode.addInput(pC.getType()));
                c = new ThruConnectorImpl(n, pC.getType(),
                        pC.getLocalId(), true, innerNode, innerConnector);
            } else {
                VNode innerNode = flowModel.newNode();
                Connector innerConnector = innerNode.setMainOutput(
                        innerNode.addOutput(pC.getType()));
                c = new ThruConnectorImpl(n, pC.getType(),
                        pC.getLocalId(), false, innerNode, innerConnector);
            }
            
        } else {
            c = new IOConnector(n, pC.getType(), pC.getLocalId(), pC.isInput());
        }
        
        c.setMaxNumberOfConnections(pC.getMaxNumConnections());
        
        return c;
    }

    /**
     * Converts a connector to an equivalent persistent connector.
     * <b>Note:</b> the corresponding node won't be defined. If the persistent
     * connector is added to the persistent node, the node will call the
     * connectors <code>setNode()</code> method to ensure the correct node is
     * referenced.
     *
     * @param c connector to convert
     * @return the equivalent persistent connector to the specified connector
     */
    public static PersistentConnector toPersistentConnector(Connector c) {
        PersistentConnector pC = new PersistentConnector(
                c.getType(), c.getLocalId(), c.isInput(),
                c.isOutput(), c instanceof ThruConnector);
        
        pC.setMaxNumConnections(c.getMaxNumberOfConnections());
        
        return pC;
    }
    
    public static PersistentValueObject toPersistentValueObject(ValueObject vObj) {
        return new PersistentValueObject(vObj.getParent().getId(), vObj.getValue(), vObj.getVisualizationRequest());
    }
    
    public static ValueObject toValueObject(VNode node, PersistentValueObject vObj) {
        ValueObject result = new DefaultValueObject();
        
        result.setParent(node);
        result.setValue(vObj.getValue());
        
        for (String key : vObj.getStorage().keySet()) {
            result.getVisualizationRequest().set(
                    key, vObj.getStorage().get(key));
        }        
        
        return result;
    }
    
}
