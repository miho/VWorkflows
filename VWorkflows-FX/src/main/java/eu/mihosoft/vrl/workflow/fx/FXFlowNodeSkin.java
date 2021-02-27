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
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import eu.mihosoft.vrl.workflow.skin.VNodeSkin;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import jfxtras.scene.control.window.Window;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Segment;
import org.apache.commons.math3.geometry.euclidean.twod.SubLine;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class FXFlowNodeSkin
        implements FXSkin<VNode, Window>, VNodeSkin<VNode> {

    private final ObjectProperty<VNode> modelProperty = new SimpleObjectProperty<>();
    private FlowNodeWindow node;
    private final ObjectProperty<Parent> parentProperty = new SimpleObjectProperty<>();
    private ChangeListener<String> modelTitleListener;
    private ChangeListener<Number> modelXListener;
    private ChangeListener<Number> modelYListener;
    private ChangeListener<Number> modelWidthListener;
    private ChangeListener<Number> modelHeightListener;
    private ChangeListener<Number> nodeXListener;
    private ChangeListener<Number> nodeYListener;
    private ChangeListener<Number> nodeWidthListener;
    private ChangeListener<Number> nodeHeightListener;

    private FXConnectionSkin newConnectionSkin;
    private MouseEvent newConnectionPressEvent;
    private boolean removeSkinOnly = false;
    VFlow controller;
    Map<Connector, ConnectorShape> connectors = new HashMap<>();
    List<List<ConnectorShape>> shapeLists = new ArrayList<>();
    private final Map<Connector, Integer> connectorToIndexMap = new HashMap<>();
    private final FXSkinFactory skinFactory;
    private final List<Double> connectorSizes = new ArrayList<>();

    private final List<Connector> connectorList = new ArrayList<>();
    private final IntegerProperty numConnectorsProperty = new SimpleIntegerProperty();

    private MapChangeListener<String, Object> vReqLister;

    private static final int TOP = 0;
    private static final int RIGHT = 1;
    private static final int BOTTOM = 2;
    private static final int LEFT = 3;

    public FXFlowNodeSkin(FXSkinFactory skinFactory,
            Parent parent, VNode model, VFlow controller) {
        this.skinFactory = skinFactory;
        setParent(parent);
        setModel(model);

        this.controller = controller;

        init();
    }

    protected FlowNodeWindow createNodeWindow() {
        FlowNodeWindow flowNodeWindow = new FlowNodeWindow(this);

        flowNodeWindow.setTitle(getModel().getTitle());
        flowNodeWindow.setLayoutX(getModel().getX());
        flowNodeWindow.setLayoutY(getModel().getY());
        flowNodeWindow.setPrefWidth(getModel().getWidth());
        flowNodeWindow.setPrefHeight(getModel().getHeight());

        flowNodeWindow.boundsInParentProperty().addListener(
                (ov, oldValue, newValue) -> {
                    for (Connector c : connectorList) {
                        layoutConnector(c, true);
                    }
                });

        flowNodeWindow.resizingProperty().addListener((ov) -> {
//            System.out.println("resizing: " + flowNodeWindow.isResizing());
            connectors.values().forEach(cShape -> {

                cShape.getNode().setCache(!flowNodeWindow.isResizing());
            });
        });

        flowNodeWindow.resizingProperty().addListener((ov) -> {
            if (flowNodeWindow.isResizing()) {
                flowNodeWindow.setCache(false);
            } else {
                flowNodeWindow.setCache(true);
            }
        });

//        flowNodeWindow.cacheProperty().addListener((ov)->{
//            System.out.println("w-cached: " + flowNodeWindow.isCache());
//        });
        return flowNodeWindow;
    }

    private void init() {

        for (int i = 0; i < 4; i++) {
            shapeLists.add(new ArrayList<>());
        }

        node = createNodeWindow();

        registerListeners(getModel());

        modelProperty.addListener((ov, oldVal, newVal) -> {
            removeListeners(oldVal);
            registerListeners(newVal);
        });

        for (Connector connector : getModel().getConnectors()) {
            addConnector(connector);
        }

        getModel().getConnectors().addListener(
                (ListChangeListener.Change<? extends Connector> change) -> {
                    boolean numConnectorsHasChanged = false;
                    while (change.next()) {
                        // TODO handle permutation
//                        if (change.wasPermutated()) {
//                            for (int i = change.getFrom(); i < change.getTo(); ++i) {
//                                // TODO permutate
//                            }
//                        } 
//                        else if (change.wasUpdated()) {
//                            // TODO update item
//                        } 
//                        else 

                        if (change.wasRemoved()) {
                            numConnectorsHasChanged = true;
                            // removed
                            for (Connector connector : change.getRemoved()) {
                                removeConnector(connector);
                            }
                        } else if (change.wasAdded()) {
                            numConnectorsHasChanged = true;
                            // added
                            for (Connector connector : change.getAddedSubList()) {
                                addConnector(connector);
                            }
                        }

                    } // end while change.next()

                    if (numConnectorsHasChanged) {

                        computeConnectorSizes();
//                    computeInputConnectorSize();
//                    computeOutputConnectorSize();
                        adjustConnectorSize();

                        numConnectorsProperty.set(getModel().getConnectors().size());
                    }

                    configureEditCapability();
                });
    }

    private void initVReqListeners() {

        configureEditCapability();

        vReqLister = (change) -> {

            configureEditCapability();
        };

        getModel().getVisualizationRequest().addListener(vReqLister);
        getModel().getFlow().getVisualizationRequest().addListener(vReqLister);
    }

    private void configureEditCapability() {

        Optional<Boolean> disableEditing
                = getModel().getVisualizationRequest().
                get(VisualizationRequest.KEY_DISABLE_EDITING);

        if (disableEditing.isPresent()) {

            boolean disableEditingV = disableEditing.get();

            updateEditabilityConfig(disableEditingV);
        } else {

            VFlowModel parent = getModel().getFlow();

            while (parent != null) {
                Optional<Boolean> disableEditingParent
                        = parent.getVisualizationRequest().
                        get(VisualizationRequest.KEY_DISABLE_EDITING);

                if (disableEditingParent.isPresent()) {
                    updateEditabilityConfig(disableEditingParent.get());
                    break;
                }

                parent = parent.getFlow();
            }

            // if we din't find a parent with the requested value then we
            // make it editable
            if (parent == null) {
                updateEditabilityConfig(false);
            }
        }
    }

    private void updateEditabilityConfig(boolean notEditable) {
        node.setMovable(!notEditable);
        node.setResizableWindow(!notEditable);
        node.setEditableState(!notEditable);

        for (ConnectorShape connectorShape : connectors.values()) {
            connectorShape.getNode().setMouseTransparent(notEditable);
        }

        if (this.getModel() instanceof VFlowModel) {

            VFlowModel flowModel = (VFlowModel) this.getModel();

            flowModel.getAllConnections().values().stream().flatMap(
                    conns -> conns.getConnections().stream()).
                    map(conn -> controller.
                            getNodeSkinLookup().getById(skinFactory,
                                    conn)).
                    filter(cSkin -> cSkin instanceof DefaultFXConnectionSkin).
                    map(cSkin -> (DefaultFXConnectionSkin) cSkin).
                    forEach(cSkin -> cSkin.configureEditCapability(notEditable));

            for (VNode vn : flowModel.getNodes()) {

                FXFlowNodeSkin n = (FXFlowNodeSkin) controller.
                        getNodeSkinLookup().getById(skinFactory, vn.getId());

                if (n != null) {
                    n.configureEditCapability();
                }
            }
        } else if (getModel().getFlow() != null) {
            VFlowModel parent = getModel().getFlow();

            parent.getAllConnections().values().stream().flatMap(
                    conns -> conns.getConnections().stream()).
                    map(conn -> controller.
                            getNodeSkinLookup().getById(skinFactory,
                                    conn)).
                    filter(cSkin -> cSkin instanceof DefaultFXConnectionSkin).
                    map(cSkin -> (DefaultFXConnectionSkin) cSkin).
                    forEach(cSkin -> cSkin.configureEditCapability(notEditable));
        }
    }

    void layoutConnectors() {

        for (Connector c : connectorList) {

            layoutConnector(c, false);
        }
    }

    private void layoutConnector(Connector c, boolean updateOthers) {

        Optional<Boolean> autoLayout
                = c.getVisualizationRequest().
                get(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT);

        boolean switchEdges = autoLayout.orElse(false);

        ConnectorShape connectorShape = connectors.get(c);

        connectorShape.getNode().setLayoutX(computeConnectorXValue(c) - connectorShape.getRadius());
        connectorShape.getNode().setLayoutY(computeConnectorYValue(c) - connectorShape.getRadius());

        Collection<Connection> conns = getModel().getFlow().
                getConnections(c.getType()).getAllWith(c);
        //----------------------------B       

        Optional<Boolean> preferTD = c.getVisualizationRequest().
                get(VisualizationRequest.KEY_CONNECTOR_PREFER_TOP_DOWN);
        boolean preferTopDown = preferTD.orElse(false);

        if (preferTopDown && conns.isEmpty()) {
            int oldEdgeIndex = connectorToIndexMap.get(c);

            int newEdgeIndex = c.isInput() ? TOP : BOTTOM;

            connectorShape.getNode().setLayoutX(computeConnectorXValue(c)- connectorShape.getRadius());
            connectorShape.getNode().setLayoutY(computeConnectorYValue(c)- connectorShape.getRadius());

            if (newEdgeIndex != oldEdgeIndex) {

                shapeLists.get(oldEdgeIndex).remove(connectorShape);
                shapeLists.get(newEdgeIndex).add(connectorShape);
                connectorToIndexMap.put(c, newEdgeIndex);

                // update all other connectors
                layoutConnectors();
            }
        }

        //----------------------------E
        if (conns.isEmpty()) {
            return;
        }

        Pair<Integer, Integer> edges = new Pair<>(LEFT, RIGHT);

        if (switchEdges) {
            List<Pair<Integer, Integer>> edgesList
                    = new ArrayList<>(conns.size());

            for (Connection connection : conns) {
                Pair<Integer, Integer> tmpEdges
                        = getConnectorEdges(connection);
                edgesList.add(tmpEdges);
            }

            List<Integer> frequencies = edgesList.stream().distinct().
                    map(e -> Collections.frequency(edgesList, e)).
                    collect(Collectors.toList());

            int max = 0;
            int maxIndex = -1;

            for (int i = 0; i < frequencies.size(); i++) {
                int freq = frequencies.get(i);
                if (freq > max) {
                    max = freq;
                    maxIndex = i;
                }
            }

            if (maxIndex > -1) {
                edges = edgesList.get(maxIndex);
            }

            int oldEdgeIndex = connectorToIndexMap.get(c);
            int newEdgeIndex;

            if (c.isInput()) {
                newEdgeIndex = edges.getSecond();

                if (newEdgeIndex == RIGHT) {
                    newEdgeIndex = TOP;
                } else if (newEdgeIndex == BOTTOM) {
                    newEdgeIndex = LEFT;
                }

            } else {
                newEdgeIndex = edges.getFirst();

                if (newEdgeIndex == TOP) {
                    newEdgeIndex = RIGHT;
                } else if (newEdgeIndex == LEFT) {
                    newEdgeIndex = BOTTOM;
                }
            }

            if (newEdgeIndex != oldEdgeIndex) {

                shapeLists.get(oldEdgeIndex).remove(connectorShape);

                shapeLists.get(newEdgeIndex).add(connectorShape);
                connectorToIndexMap.put(c, newEdgeIndex);

                // update all other connectors
                layoutConnectors();
            }
        } // end if switchEdges

        connectorShape.getNode().setLayoutX(computeConnectorXValue(c)
                - connectorShape.getRadius());
        connectorShape.getNode().setLayoutY(computeConnectorYValue(c)
                - connectorShape.getRadius());

//        System.out.println("c: " + c);
        if (updateOthers) {
            for (Connection connection : conns) {

                String nId;
                Connector cTmp;

                if (c.isInput()) {
                    nId = connection.getSender().getNode().getId();
                    cTmp = connection.getSender();
                } else {
                    nId = connection.getReceiver().getNode().getId();
                    cTmp = connection.getReceiver();
                }

                // TODO analyze potential performance issue with lookup
                List<VNodeSkin> skins = controller.getNodeSkinLookup().
                        getById(nId);

                for (VNodeSkin skin : skins) {
                    FXFlowNodeSkin fxSkin = (FXFlowNodeSkin) skin;
                    fxSkin.layoutConnector(cTmp, false);
                }
            }
        }
    }

    private double computeConnectorXValue(Connector connector) {

        ConnectorShape connectorNode = connectors.get(connector);

        double posX = node.getLayoutX();

        int edgeIndex = connector.isInput() ? LEFT : RIGHT;

        if (connectorToIndexMap.containsKey(connector)) {
            edgeIndex = connectorToIndexMap.get(connector);
        }

        if (edgeIndex == RIGHT) {
            posX += node.getWidth();
        }

        if (edgeIndex == LEFT || edgeIndex == RIGHT) {
            return posX;
        }

        double midPointOfNode = posX + node.getWidth() * 0.5;

        double connectorWidth = connectorNode.getNode().getBoundsInLocal().getWidth();
        double gap = 5;

        double numConnectors = shapeLists.get(edgeIndex).size();

        int connectorIndex = shapeLists.get(edgeIndex).indexOf(connectorNode);

        double totalWidth = numConnectors * connectorWidth
                + (numConnectors - 1) * gap;

        double startX = midPointOfNode - totalWidth / 2;

        double offsetX = +(connectorWidth + gap) * connectorIndex
                + connectorWidth / 2;

        double x = startX + offsetX;

        return x;
    }

    private double computeConnectorYValue(Connector connector) {

        ConnectorShape connectorNode = connectors.get(connector);

        double posY = node.getLayoutY();

        int edgeIndex = connector.isInput() ? LEFT : RIGHT;

        if (connectorToIndexMap.containsKey(connector)) {
            edgeIndex = connectorToIndexMap.get(connector);
        }

        if (edgeIndex == BOTTOM) {
            posY += node.getHeight();
        }

        if (edgeIndex == TOP || edgeIndex == BOTTOM) {
            return posY;
        }

        double connectorHeight = connectorNode.getNode().getBoundsInLocal().getHeight();
        double gap = 5;

        double numConnectors = shapeLists.get(edgeIndex).size();

        int connectorIndex = shapeLists.get(edgeIndex).indexOf(connectorNode);

        double totalHeight = numConnectors * connectorHeight
                + (numConnectors - 1) * gap;

        double midPointOfNode = node.getLayoutY()
                + node.getHeight() / 2;

        double startY = midPointOfNode - totalHeight / 2;

        double y = startY;

        double offsetY = +(connectorHeight + gap) * connectorIndex
                + connectorHeight / 2;

        y += offsetY;

        return y;
    }

    protected void addConnector(final Connector connector) {
        connectorList.add(connector);
        ConnectorShape connectorShape = createConnectorShape(connector);

        final Node connectorNode = connectorShape.getNode();
        connectorNode.setManaged(false);

        connectors.put(connector, connectorShape);
//--------------------B
        Optional<Boolean> preferTD = connector.getVisualizationRequest().
                get(VisualizationRequest.KEY_CONNECTOR_PREFER_TOP_DOWN);
        boolean preferTopDown = preferTD.orElse(false);
        int inputDefault = preferTopDown ? TOP : LEFT;
        int outputDefault = preferTopDown ? BOTTOM : RIGHT;
//--------------------E
        if (connector.isInput()) {
//            inputList.add(connectorNode);
            shapeLists.get(inputDefault).add(connectorShape);
            connectorToIndexMap.put(connector, inputDefault);
        } else if (connector.isOutput()) {
//            outputList.add(connectorNode);
            shapeLists.get(outputDefault).add(connectorShape);
            connectorToIndexMap.put(connector, outputDefault);
        }

        node.boundsInLocalProperty().addListener((ov, oldValue, newValue) -> {
            computeConnectorSizes();
            adjustConnectorSize();
        });

        NodeUtil.addToParent(getParent(), connectorNode);

        connectorNode.onMouseEnteredProperty().set(
                (EventHandler<MouseEvent>) (MouseEvent t) -> {
                    connectorNode.toFront();
                });

        connectorNode.onMousePressedProperty().set(
                (EventHandler<MouseEvent>) (MouseEvent t) -> {
                    // we are already connected and manipulate the existing connection
                    // rather than creating a new one
                    if (controller.getConnections(connector.getType()).
                    isInputConnected(connector)) {
                        return;
                    }

                    t.consume();
                    newConnectionPressEvent = t;
                });

        connectorNode.onMouseDraggedProperty().set(
                (EventHandler<MouseEvent>) (MouseEvent t) -> {
                    if (connectorNode.isMouseTransparent()) {
                        return;
                    }

                    // we are already connected and manipulate the existing connection
                    // rather than creating a new one
                    if (controller.getConnections(connector.getType()).
                    isInputConnected(connector)) {
                        return;
                    }

                    int numOfExistingConnections = connector.getNode().getFlow().
                    getConnections(connector.getType()).
                    getAllWith(connector).size();

                    if (numOfExistingConnections < connector.
                    getMaxNumberOfConnections()) {

                        if (newConnectionSkin == null) {
                            newConnectionSkin
                            = new FXNewConnectionSkin(getSkinFactory(),
                                    getParent(), connector,
                                    getController(), connector.getType()).init();

                            newConnectionSkin.add();

                            MouseEvent.fireEvent(
                                    newConnectionSkin.getReceiverUI(),
                                    newConnectionPressEvent);
                        }

                        t.consume();
                        MouseEvent.fireEvent(
                                newConnectionSkin.getReceiverUI(), t);

                        t.consume();
                        MouseEvent.fireEvent(
                                newConnectionSkin.getReceiverUI(), t);
                    }
                });

        connectorNode.onMouseReleasedProperty().set(
                (EventHandler<MouseEvent>) (MouseEvent t) -> {
                    if (connectorNode.isMouseTransparent()) {
                        return;
                    }

                    connector.click(NodeUtil.mouseBtnFromEvent(t), t);

                    // we are already connected and manipulate the existing connection
                    // rather than creating a new one
                    if (controller.getConnections(connector.getType()).
                    isInputConnected(connector)) {
                        return;
                    }

                    t.consume();
                    try {
                        MouseEvent.fireEvent(
                                newConnectionSkin.getReceiverUI(), t);
                    } catch (Exception ex) {
                        // TODO exception is not critical here (node already removed)
                    }

                    newConnectionSkin = null;
                });
    }

    protected ConnectorShape createConnectorShape(Connector connector) {
        return new ConnectorCircle(controller,
                getSkinFactory(), connector, 20);
    }

    private void computeConnectorSizes() {

        double inset = 120;
        double minInset = 60;
        double minSize = 8;

        connectorSizes.clear();

        for (int i = 0; i < shapeLists.size(); i++) {
            List<ConnectorShape> shapeList = shapeLists.get(i);

            double connectorHeight
                    = computeConnectorSize(inset, shapeList.size());

            if (connectorHeight < minSize) {
                double diff = minSize - connectorHeight;
                inset = Math.max(inset - diff * shapeList.size(), minInset);
                connectorHeight = computeConnectorSize(inset, shapeList.size());
            }

            connectorSizes.add(connectorHeight);
        }
    }

    private double computeConnectorSize(double inset, int numConnectors) {

        if (numConnectors == 0) {
            return 0;
        }

        double maxSize = 15;

        double connectorHeight = maxSize * 2;
        double originalConnectorHeight = connectorHeight;
        double gap = 5;

        double totalHeight = numConnectors * connectorHeight
                + (numConnectors - 1) * gap;

        connectorHeight = Math.min(totalHeight,
                node.getPrefHeight() - inset) / (numConnectors);
        connectorHeight = Math.min(connectorHeight, maxSize * 2);

        if (numConnectors == 1) {
            connectorHeight = originalConnectorHeight;
        }

        return connectorHeight;
    }

    private double getMinConnectorSize() {
        Optional<Double> minSize = connectorSizes.stream().min(Double::compare);

        if (minSize.isPresent()) {
            return minSize.get();
        }

        return 0;
    }

    private List<Segment> nodeToSegments(VNode n) {
        List<Segment> result = new ArrayList<>();

        Vector2D np0 = new Vector2D(n.getX(), n.getY());
        Vector2D np1 = new Vector2D(n.getX() + n.getWidth(), n.getY());
        Vector2D np2 = new Vector2D(
                n.getX() + n.getWidth(),
                n.getY() + n.getHeight());
        Vector2D np3 = new Vector2D(n.getX(), n.getY() + n.getHeight());

        Line edge0 = new Line(np0, np1, 1e-5);
        Line edge1 = new Line(np1, np2, 1e-5);
        Line edge2 = new Line(np2, np3, 1e-5);
        Line edge3 = new Line(np3, np0, 1e-5);

        result.add(new Segment(np0, np1, edge0));
        result.add(new Segment(np1, np2, edge1));
        result.add(new Segment(np2, np3, edge2));
        result.add(new Segment(np3, np0, edge3));

        return result;
    }

    private Optional<Integer> getIntersectionIndex(
            Segment connectionSegment, List<Segment> edges) {

        SubLine connectionSegmentL = new SubLine(connectionSegment);

        int i = 0;
        for (Segment s : edges) {

            if (new SubLine(s).intersection(connectionSegmentL, true) != null) {
                return Optional.of(i);
            }

            i++;
        }

        return Optional.empty();
    }

    private Vector2D getNodeCenter(VNode n) {
        return new Vector2D(
                n.getX() + n.getWidth() * 0.5,
                n.getY() + n.getHeight() * 0.5);
    }

    private Segment getConnectionEdge(VNode sender, VNode receiver) {
        Vector2D senderC = getNodeCenter(sender);
        Vector2D receiverC = getNodeCenter(receiver);

        Line l = new Line(senderC, receiverC, 1e-5);

        return new Segment(senderC, receiverC, l);
    }

    private Pair<Integer, Integer> getConnectorEdges(Connection c) {

        VNode senderN = c.getSender().getNode();
        List<Segment> n1Edges = nodeToSegments(senderN);
        VNode receiverN = c.getReceiver().getNode();
        List<Segment> n2Edges = nodeToSegments(receiverN);

        Segment segment = getConnectionEdge(senderN, receiverN);

        Optional<Integer> senderIntersection
                = getIntersectionIndex(segment, n1Edges);
        Optional<Integer> receiverIntersection
                = getIntersectionIndex(segment, n2Edges);

        if (!senderIntersection.isPresent()
                || !receiverIntersection.isPresent()) {
            // rectangles overlap. therefore we use default layout
            return new Pair<>(RIGHT, LEFT);
        }

        return new Pair<>(senderIntersection.get(), receiverIntersection.get());
    }

    private void adjustConnectorSize() {

        double maxConnectorSize = Double.MAX_VALUE;

        Optional<Double> maxSize
                = getModel().getVisualizationRequest().
                get(VisualizationRequest.KEY_MAX_CONNECTOR_SIZE);

        if (maxSize.isPresent()) {
            maxConnectorSize = maxSize.get();
        }

        for (int i = 0; i < connectorSizes.size(); i++) {
            for (ConnectorShape connector : shapeLists.get(i)) {
                double size = connectorSizes.get(i);

                if (connector instanceof ConnectorShape) {

                    connector.setRadius(
                            Math.min(size * 0.5, maxConnectorSize * 0.5));
                }
            }
        }
    }

    private void removeConnector(Connector connector) {
        connectorList.remove(connector);
        ConnectorShape connectorShape = connectors.remove(connector);

        if (connectorShape != null && connectorShape.getNode().getParent() != null) {
            // TODO: remove connectors&connections?
            if (connector.isInput()) {
                shapeLists.get(LEFT).remove(connectorShape);
            } else if (connector.isOutput()) {
                shapeLists.get(RIGHT).remove(connectorShape);
            }
            NodeUtil.removeFromParent(connectorShape.getNode());
        }
    }

    @Override
    public Window getNode() {
        return node;
    }

    @Override
    public Parent getContentNode() {
        return node.getWorkflowContentPane();
    }

    @Override
    public void remove() {
        removeSkinOnly = true;

        List<Connector> delList = new ArrayList<>(connectorList);

        for (Connector c : delList) {
            removeConnector(c);
        }
        if (node != null && node.getParent() != null) {
            NodeUtil.removeFromParent(node);
        }

        removeListeners(getModel());

        getModel().getVisualizationRequest().removeListener(vReqLister);

        node.onRemovedFromSceneGraph();
    }

    @Override
    public final void setModel(VNode model) {
        modelProperty.set(model);
    }

    @Override
    public final VNode getModel() {
        return modelProperty.get();
    }

    @Override
    public final ObjectProperty<VNode> modelProperty() {
        return modelProperty;
    }

    final void setParent(Parent parent) {
        parentProperty.set(parent);
    }

    Parent getParent() {
        return parentProperty.get();
    }

    ObjectProperty<Parent> parentProperty() {
        return parentProperty;
    }

    @Override
    public void add() {
        NodeUtil.addToParent(getParent(), node);
    }

    private void removeListeners(VNode flowNode) {
        flowNode.titleProperty().removeListener(modelTitleListener);
        flowNode.xProperty().removeListener(modelXListener);
        flowNode.yProperty().removeListener(modelYListener);
        flowNode.widthProperty().removeListener(modelWidthListener);
        flowNode.heightProperty().removeListener(modelHeightListener);

        node.layoutXProperty().removeListener(nodeXListener);
        node.layoutYProperty().removeListener(nodeXListener);
        node.prefWidthProperty().removeListener(nodeWidthListener);
        node.prefHeightProperty().removeListener(nodeHeightListener);

        flowNode.getVisualizationRequest().addListener(vReqLister);
    }

    private void initListeners() {
        modelTitleListener = (ov, oldValue, newValue) -> {
            node.setTitle(newValue);
        };

        modelXListener = (ov, oldValue, newValue) -> {
            node.setLayoutX(newValue.doubleValue());
        };

        modelYListener = (ov, oldValue, newValue) -> {
            node.setLayoutY(newValue.doubleValue());
        };

        modelWidthListener = (ov, oldValue, newValue) -> {
            node.setPrefWidth(newValue.doubleValue());
        };

        modelHeightListener = (ov, oldValue, newValue) -> {
            node.setPrefHeight(newValue.doubleValue());
        };

        nodeXListener = (ov, oldValue, newValue) -> {
            getModel().setX(newValue.doubleValue());
        };

        nodeYListener = (ov, oldValue, newValue) -> {
            getModel().setY(newValue.doubleValue());
        };

        nodeWidthListener = (ov, oldValue, newValue) -> {
            getModel().setWidth(newValue.doubleValue());
        };

        nodeHeightListener = (ov, oldValue, newValue) -> {
            getModel().setHeight(newValue.doubleValue());
        };

        node.onCloseActionProperty().set(
                (EventHandler<ActionEvent>) (ActionEvent t) -> {
                    if (!removeSkinOnly) {
                        modelProperty().get().getFlow().remove(modelProperty().get());
                    }
                });
    }

    private void registerListeners(VNode flowNode) {

        initListeners();

        flowNode.titleProperty().addListener(modelTitleListener);
        flowNode.xProperty().addListener(modelXListener);
        flowNode.yProperty().addListener(modelYListener);
        flowNode.widthProperty().addListener(modelWidthListener);
        flowNode.heightProperty().addListener(modelHeightListener);

        node.layoutXProperty().addListener(nodeXListener);
        node.layoutYProperty().addListener(nodeYListener);
        node.prefWidthProperty().addListener(nodeWidthListener);
        node.prefHeightProperty().addListener(nodeHeightListener);

        initVReqListeners();

    }

    /**
     * @return the controller
     */
    @Override
    public VFlow getController() {
        return controller;
    }

    /**
     * @param controller the controller to set
     */
    @Override
    public void setController(VFlow controller) {
        this.controller = controller;
    }

    /**
     * @return the skinFactory
     */
    @Override
    public FXSkinFactory getSkinFactory() {
        return this.skinFactory;
    }

    public ConnectorShape getConnectorShape(Connector c) {
        return connectors.get(c);
    }

    public void configureCanvas(VCanvas content) {
        //
    }
}
