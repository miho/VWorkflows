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
package eu.mihosoft.vrl.workflow.demo;

import edu.uci.ics.jung.graph.DirectedGraph;
import eu.mihosoft.vrl.workflow.ClickEvent;
import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.incubating.LayoutGenerator;
import eu.mihosoft.vrl.workflow.incubating.LayoutGeneratorNaive;
import eu.mihosoft.vrl.workflow.incubating.LayoutGeneratorSmart;
import eu.mihosoft.vrl.workflow.MouseButton;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import eu.mihosoft.vrl.workflow.fx.FlowNodeWindow;
import eu.mihosoft.vrl.workflow.fx.FXFlowNodeSkin;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import eu.mihosoft.vrl.workflow.fx.InnerCanvas;
import eu.mihosoft.vrl.workflow.fx.NodeUtil;
import eu.mihosoft.vrl.workflow.fx.OptimizableContentPane;
import eu.mihosoft.vrl.workflow.fx.ScalableContentPane;
import eu.mihosoft.vrl.workflow.fx.VCanvas;
import eu.mihosoft.vrl.workflow.io.WorkflowIO;
import java.io.File;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Translate;
import javax.imageio.ImageIO;
import jfxtras.scene.control.window.Window;

/**
 * FXML Controller class
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class MainWindowFXMLController implements Initializable {

    private int counter = 0;
    private Window clipboard;
    private VFlow specialViewFlow1;
    private VFlow specialViewFlow2;
    private FXMLLoader fxmlLoaderSmart;
    private FXMLLoader fxmlLoaderNaive;
    private OptionsWindowFXMLController optionsSmart;
    private OptionsWindowNaiveFXMLController optionsNaive;
    private Stage optionsstageSmart;
    private Stage optionsstageNaive;
    private LayoutGeneratorSmart smartLayout;
    private LayoutGeneratorNaive naiveLayout;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       canvas = new VCanvas();

        Pane root = (Pane) canvas.getContent();

        contentPane.getChildren().add(canvas);

        rootPane = root;

        onGenerateAction(null);
        
        // layout interface:
        smartLayout = new LayoutGeneratorSmart();
        naiveLayout = new LayoutGeneratorNaive();
        
        fxmlLoaderSmart = new FXMLLoader(getClass()
                .getResource("OptionsWindowFXML.fxml"));
        optionsstageSmart = new Stage();
        optionsstageSmart.setTitle("Smart Layout Options");
        try {
            Parent p = (Parent) fxmlLoaderSmart.load();
            optionsstageSmart.setScene(new Scene(p));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        optionsSmart = fxmlLoaderSmart.getController();
        optionsSmart.setGenerator(smartLayout);
        optionsSmart.setStage(optionsstageSmart);
        
        fxmlLoaderNaive = new FXMLLoader(getClass()
                .getResource("OptionsWindowNaiveFXML.fxml"));
        optionsstageNaive = new Stage();
        optionsstageNaive.setTitle("Naive Layout Options");
        try {
            Parent p = (Parent) fxmlLoaderNaive.load();
            optionsstageNaive.setScene(new Scene(p));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        optionsNaive = fxmlLoaderNaive.getController();
        optionsNaive.setGenerator(naiveLayout);
        optionsNaive.setStage(optionsstageNaive);
    }
    
    private Pane rootPane;
    private VCanvas canvas;
    VFlow workflow;

    @FXML
    public void onLoadAction(ActionEvent e) {
        System.out.print(" >> loading workflow from xml");

        try {
            workflow = WorkflowIO.loadFromXML(Paths.get("flow01.xml"));

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(" [done]");

        updateUI();
    }

    @FXML
    public void onSaveAction(ActionEvent e) {

        if (workflow == null) {
            return;
        }

        System.out.print(" >> saving workflow as xml");
        try {
            WorkflowIO.saveToXML(Paths.get("flow01.xml"), workflow.getModel());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(" [done]");
    }
    
    // <editor-fold defaultstate="collapsed" desc="Menu items">
    @FXML
    private CheckMenuItem checkDebugLayout;
    // </editor-fold>
    
    // <editor-fold desc="Development" defaultstate="collapsed">
    @FXML
    public void onNaiveAction(ActionEvent e) {
        int i;
        this.naiveLayout.setDebug(this.checkDebugLayout.isSelected());
        switch(this.naiveLayout.getGraphmode()) {
            case 0:
                this.naiveLayout.setWorkflow(this.workflow.getModel());
                this.naiveLayout.generateLayout();
                break;
            case 2:
                ObservableList<VNode> obsnodes = workflow.getNodes();
                LinkedList<VNode> nodelist = new LinkedList<>();
                for(i = 0; i < obsnodes.size(); i++) {
                    VNode curr = obsnodes.get(i);
                    if(curr.isSelected()) {
                        nodelist.add(curr);
                    }
                }
                if(!nodelist.isEmpty()) {
                    this.smartLayout.setNodelist(nodelist);
                    this.smartLayout.generateLayout();
                }
                break;
        }
        
    }
    
    @FXML
    public void onSmartRunAction(ActionEvent e) {
        int i;
        this.smartLayout.setDebug(this.checkDebugLayout.isSelected());
        switch(this.smartLayout.getGraphmode()) {
            case 0:
                this.smartLayout.setWorkflow(this.workflow.getModel());
                this.smartLayout.generateLayout();
                break;
            case 1:
                LayoutGeneratorSmart altlay = new LayoutGeneratorSmart();
                altlay.setWorkflow(this.workflow.getModel());
                altlay.generateLayout();
                DirectedGraph<VNode, Connection> jgraph = 
                        altlay.getModelGraph();
                this.smartLayout.setModelGraph(jgraph);
                this.smartLayout.generateLayout();
                break;
            case 2:
                ObservableList<VNode> obsnodes = workflow.getNodes();
                LinkedList<VNode> nodelist = new LinkedList<>();
                for(i = 0; i < obsnodes.size(); i++) {
                    VNode curr = obsnodes.get(i);
                    if(curr.isSelected()) {
                        nodelist.add(curr);
                    }
                }
                if(!nodelist.isEmpty()) {
                    this.smartLayout.setNodelist(nodelist);
                    this.smartLayout.generateLayout();
                }
                break;
        }
    }
    
    @FXML
    public void onSmartOptionsAction(ActionEvent e) {
        optionsSmart.setWorkflow(workflow);
        smartLayout.setDebug(checkDebugLayout.isSelected());
        optionsSmart.set();
        optionsstageSmart.show();
    }
    
    @FXML
    public void onNaiveOptionsAction(ActionEvent e) {
        optionsNaive.setWorkflow(workflow);
        naiveLayout.setDebug(checkDebugLayout.isSelected());
        optionsNaive.set();
        optionsstageNaive.show();
    }
    
    @FXML
    public void onLayoutSnapshotAction(ActionEvent e) {
        String abspath = new File(".").getAbsolutePath();
        String path = abspath.substring(0, abspath.length()-1);
        File dir = new File(path + "snapshots");
        if(!dir.exists()) {
            System.out.println("Creating directory: " + dir.getAbsolutePath());
            dir.mkdir();
        }
        path += "snapshots/";
        WritableImage wim = new WritableImage((int) Math.round(contentPane.getWidth()), (int) Math.round(contentPane.getHeight()));
        SnapshotParameters param = new SnapshotParameters();
        param.setTransform(new Translate(0, 200));
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String now = format.format(calendar.getTime());
        try {
            rootPane.snapshot(param, wim);
            dir = new File(path + now + ".png");
            ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", dir);
            System.out.println("snapshot " + now + ".png saved.");
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        subSnaps((InnerCanvas) canvas.getContent(), path, now);
    }
    
    private void subSnaps(InnerCanvas inner, String path, String now) {
        ObservableList<Node> childnodes = inner.getChildrenUnmodifiable();
        for(Node n : childnodes) {
            if((n instanceof FlowNodeWindow) && (n.isManaged())) {
                FlowNodeWindow w = (FlowNodeWindow) n;
                List<String> style = NodeUtil.getStylesheetsOfAncestors(w);
                FXFlowNodeSkin wskin = w.nodeSkinProperty().get();
                VFlow cont = wskin.getController();
                Collection<VFlow> subconts = cont.getSubControllers();
                for(VFlow currsub : subconts) {
                    if(currsub.getModel().equals(wskin.getModel())) {
                        String title = currsub.getModel().getId().replace(':', '-');
                        if((currsub.getNodes().size() > 0) && (currsub.isVisible())) {
                            VCanvas subcanvas = new VCanvas();
                            FlowNodeWindow.addResetViewMenu(subcanvas);
                            subcanvas.setMinScaleX(0.1);
                            subcanvas.setMinScaleY(0.1);
                            subcanvas.setMaxScaleX(1);
                            subcanvas.setMaxScaleY(1);
                            subcanvas.setTranslateToMinNodePos(true);
                            
                            FXSkinFactory fxSkinFactory = w.nodeSkinProperty().get().getSkinFactory().newInstance((Parent) subcanvas.getContent(), null);
                            currsub.addSkinFactories(fxSkinFactory);
                            
                            Scene subscene = new Scene(subcanvas, (int) Math.round(rootPane.getWidth()), (int) Math.round(rootPane.getHeight()));
                            subscene.getStylesheets().setAll(style);
                            WritableImage wim = new WritableImage((int) Math.round(subscene.getWidth()), (int) Math.round(subscene.getHeight()));
                            try {
                                subscene.snapshot(wim);
                                File dir = new File(path + now + "_" + title + ".png");
                                ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", dir);
                                System.out.println("snapshot " + now + "_" + title + ".png saved");
                            } catch (IOException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if(w.getWorkflowContentPane() instanceof InnerCanvas) {
                                subSnaps((InnerCanvas) w.getWorkflowContentPane(), path, now);
                            }
                        }
                    }
                }
            }
        }
    }
    // </editor-fold>
    
    // <editor-fold desc="Test cases" defaultstate="collapsed">
    @FXML
    public void onOriginalAction(ActionEvent e) {
        onGenerateAction(e);
        VFlowModel flow = workflow.getModel();
        originalTest(flow);
    }
    
    private void originalTest(VFlowModel flow) {
        ObservableList<VNode> nodes = flow.getNodes();
        if(nodes.isEmpty()) return;
        flow.connect(nodes.get(1).getOutputs().get(1), nodes.get(3).getInputs()
                .get(1));
        flow.connect(nodes.get(3).getOutputs().get(1), nodes.get(8).getInputs()
                .get(3));
        flow.connect(nodes.get(5).getOutputs().get(1), nodes.get(1).getInputs()
                .get(1));
        flow.connect(nodes.get(5).getOutputs().get(3), nodes.get(7).getInputs()
                .get(1));
        flow.connect(nodes.get(7).getOutputs().get(1), nodes.get(8).getInputs()
                .get(0));
        flow.connect(nodes.get(8).getOutputs().get(2), nodes.get(9).getInputs()
                .get(1));
        flow.connect(nodes.get(3).getOutputs().get(0), nodes.get(6).getInputs()
                .get(0));
        flow.connect(nodes.get(6).getOutputs().get(0), nodes.get(0).getInputs()
                .get(0));
        flow.connect(nodes.get(0).getOutputs().get(0), nodes.get(9).getInputs()
                .get(0));
        Iterator<VNode> it = nodes.iterator();
        while(it.hasNext()) {
            VNode curr = it.next();
            if(curr instanceof VFlowModel) {
                originalTest((VFlowModel) curr);
            }
        }
    }
    
    @FXML
    public void onAdditionalEdgeAction(ActionEvent e) {
        onGenerateAction(e);
        VFlowModel flow = workflow.getModel();
        additionalEdgeTest(flow);
    }
    
    private void additionalEdgeTest(VFlowModel flow) {
        ObservableList<VNode> nodes = flow.getNodes();
        if(nodes.isEmpty()) return;
        flow.connect(nodes.get(1).getOutputs().get(1), nodes.get(3).getInputs()
                .get(1));
        flow.connect(nodes.get(3).getOutputs().get(1), nodes.get(8).getInputs()
                .get(3));
        flow.connect(nodes.get(5).getOutputs().get(1), nodes.get(1).getInputs()
                .get(1));
        flow.connect(nodes.get(5).getOutputs().get(3), nodes.get(7).getInputs()
                .get(1));
        flow.connect(nodes.get(7).getOutputs().get(1), nodes.get(8).getInputs()
                .get(0));
        flow.connect(nodes.get(8).getOutputs().get(2), nodes.get(9).getInputs()
                .get(1));
        flow.connect(nodes.get(3).getOutputs().get(0), nodes.get(6).getInputs()
                .get(0));
        flow.connect(nodes.get(6).getOutputs().get(0), nodes.get(0).getInputs()
                .get(0));
        flow.connect(nodes.get(0).getOutputs().get(0), nodes.get(9).getInputs()
                .get(0));
        flow.connect(nodes.get(2).getOutputs().get(2), nodes.get(8).getInputs()
                .get(4));
        Iterator<VNode> it = nodes.iterator();
        while(it.hasNext()) {
            VNode curr = it.next();
            if(curr instanceof VFlowModel) {
                additionalEdgeTest((VFlowModel) curr);
            }
        }
    }
    
    @FXML
    public void onAdditionalGraphAction(ActionEvent e) {
        onGenerateAction(e);
        VFlowModel flow = workflow.getModel();
        additionalGraphTest(flow);
    }
    
    private void additionalGraphTest(VFlowModel flow) {
        ObservableList<VNode> nodes = flow.getNodes();
        if(nodes.isEmpty()) return;
        flow.connect(nodes.get(1).getOutputs().get(1), nodes.get(3).getInputs()
                .get(1));
        flow.connect(nodes.get(3).getOutputs().get(1), nodes.get(8).getInputs()
                .get(3));
        flow.connect(nodes.get(5).getOutputs().get(1), nodes.get(1).getInputs()
                .get(1));
        flow.connect(nodes.get(8).getOutputs().get(2), nodes.get(9).getInputs()
                .get(1));
        flow.connect(nodes.get(3).getOutputs().get(0), nodes.get(6).getInputs()
                .get(0));
        flow.connect(nodes.get(6).getOutputs().get(0), nodes.get(0).getInputs()
                .get(0));
        flow.connect(nodes.get(0).getOutputs().get(0), nodes.get(9).getInputs()
                .get(0));
        flow.connect(nodes.get(2).getOutputs().get(1), nodes.get(4).getInputs()
                .get(1));
        flow.connect(nodes.get(4).getOutputs().get(1), nodes.get(7).getInputs()
                .get(1));
        Iterator<VNode> it = nodes.iterator();
        while(it.hasNext()) {
            VNode curr = it.next();
            if(curr instanceof VFlowModel) {
                additionalGraphTest((VFlowModel) curr);
            }
        }
    }
    
    @FXML
    public void onTriLaneAction(ActionEvent e) {
        onGenerateAction(e);
        VFlowModel flow = workflow.getModel();
        triLaneTest(flow);
    }
    
    private void triLaneTest(VFlowModel flow) {
        ObservableList<VNode> nodes = flow.getNodes();
        if(nodes.isEmpty()) return;
        flow.connect(nodes.get(0).getOutputs().get(1), nodes.get(1).getInputs()
                .get(1));
        flow.connect(nodes.get(0).getOutputs().get(1), nodes.get(5).getInputs()
                .get(1));
        flow.connect(nodes.get(0).getOutputs().get(1), nodes.get(6).getInputs()
                .get(1));
        flow.connect(nodes.get(1).getOutputs().get(1), nodes.get(2).getInputs()
                .get(1));
        flow.connect(nodes.get(1).getOutputs().get(1), nodes.get(7).getInputs()
                .get(1));
        flow.connect(nodes.get(5).getOutputs().get(1), nodes.get(3).getInputs()
                .get(1));
        flow.connect(nodes.get(6).getOutputs().get(1), nodes.get(7).getInputs()
                .get(1));
        flow.connect(nodes.get(2).getOutputs().get(1), nodes.get(8).getInputs()
                .get(1));
        flow.connect(nodes.get(7).getOutputs().get(1), nodes.get(8).getInputs()
                .get(1));
        flow.connect(nodes.get(3).getOutputs().get(1), nodes.get(8).getInputs()
                .get(1));
        Iterator<VNode> it = nodes.iterator();
        while(it.hasNext()) {
            VNode curr = it.next();
            if(curr instanceof VFlowModel) {
                triLaneTest((VFlowModel) curr);
            }
        }
    }
    
    @FXML
    public void onDifferentSizesAction(ActionEvent e) {
        onGenerateAction(e);
        VFlowModel flow = workflow.getModel();
        differentSizesTest(flow);
    }
    
    private void differentSizesTest(VFlowModel flow) {
        ObservableList<VNode> nodes = flow.getNodes();
        if(nodes.isEmpty()) return;
        flow.connect(nodes.get(1).getOutputs().get(1), nodes.get(3).getInputs()
                .get(1));
        flow.connect(nodes.get(3).getOutputs().get(1), nodes.get(8).getInputs()
                .get(3));
        flow.connect(nodes.get(5).getOutputs().get(1), nodes.get(1).getInputs()
                .get(1));
        flow.connect(nodes.get(5).getOutputs().get(3), nodes.get(7).getInputs()
                .get(1));
        flow.connect(nodes.get(7).getOutputs().get(1), nodes.get(8).getInputs()
                .get(0));
        flow.connect(nodes.get(8).getOutputs().get(2), nodes.get(9).getInputs()
                .get(1));
        flow.connect(nodes.get(3).getOutputs().get(0), nodes.get(6).getInputs()
                .get(0));
        flow.connect(nodes.get(6).getOutputs().get(0), nodes.get(0).getInputs()
                .get(0));
        flow.connect(nodes.get(0).getOutputs().get(0), nodes.get(9).getInputs()
                .get(0));
        nodes.get(5).setHeight(nodes.get(5).getHeight() * 1.5);
        nodes.get(5).setWidth(nodes.get(5).getWidth() * 1.5);
        nodes.get(7).setHeight(nodes.get(7).getHeight() * 2);
        nodes.get(8).setWidth(nodes.get(8).getWidth() * 2);
        Iterator<VNode> it = nodes.iterator();
        while(it.hasNext()) {
            VNode curr = it.next();
            if(curr instanceof VFlowModel) {
                differentSizesTest((VFlowModel) curr);
            }
        }
    }
    
    @FXML
    public void onCycleAction(ActionEvent e) {
        onGenerateAction(e);
        VFlowModel flow = workflow.getModel();
        cycleTest(flow);
    }
    
    private void cycleTest(VFlowModel flow) {
        ObservableList<VNode> nodes = flow.getNodes();
        if(nodes.isEmpty()) return;
        flow.connect(nodes.get(0).getOutputs().get(1), nodes.get(1).getInputs()
                .get(1));
        flow.connect(nodes.get(1).getOutputs().get(1), nodes.get(2).getInputs()
                .get(1));
        flow.connect(nodes.get(2).getOutputs().get(1), nodes.get(3).getInputs()
                .get(1));
        flow.connect(nodes.get(2).getOutputs().get(1), nodes.get(4).getInputs()
                .get(1));
        flow.connect(nodes.get(4).getOutputs().get(1), nodes.get(0).getInputs()
                .get(1));
        Iterator<VNode> it = nodes.iterator();
        while(it.hasNext()) {
            VNode curr = it.next();
            if(curr instanceof VFlowModel) {
                cycleTest((VFlowModel) curr);
            }
        }
    }
    
    @FXML
    public void onTestTree1(ActionEvent e) {
        // generation
        System.out.println("generating Test Tree 1");
        workflow = FlowFactory.newFlow();
        updateUI();
        workflowTest(workflow, 1, 10);
        // edges
        ObservableList<VNode> nodes = workflow.getNodes();
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(1)
                .getInputs().get(1));
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(3)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(6)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(7)
                .getInputs().get(1));
        workflow.connect(nodes.get(4).getOutputs().get(1), nodes.get(8)
                .getInputs().get(1));
        workflow.connect(nodes.get(5).getOutputs().get(1), nodes.get(9)
                .getInputs().get(1));
    }
    
    @FXML
    public void onTestTree2(ActionEvent e) {
        // generation
        System.out.println("generating Test Tree 2");
        workflow = FlowFactory.newFlow();
        updateUI();
        workflowTest(workflow, 1, 10);
        // edges
        ObservableList<VNode> nodes = workflow.getNodes();
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(1)
                .getInputs().get(1));
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(3)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(6)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(7)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(8)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(9)
                .getInputs().get(1));
    }
    
    @FXML
    public void onTestTree3(ActionEvent e) {
        // generation
        System.out.println("generating Test Tree 3");
        workflow = FlowFactory.newFlow();
        updateUI();
        workflowTest(workflow, 1, 10);
        // edges
        ObservableList<VNode> nodes = workflow.getNodes();
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(1)
                .getInputs().get(1));
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(3)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(6)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(7)
                .getInputs().get(1));
        workflow.connect(nodes.get(5).getOutputs().get(1), nodes.get(8)
                .getInputs().get(1));
        workflow.connect(nodes.get(5).getOutputs().get(1), nodes.get(9)
                .getInputs().get(1));
    }
    
    @FXML
    public void onTestNontree1(ActionEvent e) {
        // generation
        System.out.println("generating Test Nontree 1");
        workflow = FlowFactory.newFlow();
        updateUI();
        workflowTest(workflow, 1, 10);
        // edges
        ObservableList<VNode> nodes = workflow.getNodes();
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(1)
                .getInputs().get(1));
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(6)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(3)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(6)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(7)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(8)
                .getInputs().get(1));
        workflow.connect(nodes.get(5).getOutputs().get(1), nodes.get(9)
                .getInputs().get(1));
        workflow.connect(nodes.get(6).getOutputs().get(1), nodes.get(9)
                .getInputs().get(1));
    }
    
    @FXML
    public void onTestNontree2(ActionEvent e) {
        // generation
        System.out.println("generating Test Nontree 2");
        workflow = FlowFactory.newFlow();
        updateUI();
        workflowTest(workflow, 1, 10);
        // edges
        ObservableList<VNode> nodes = workflow.getNodes();
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(1)
                .getInputs().get(1));
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(3)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(6)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(7)
                .getInputs().get(1));
        workflow.connect(nodes.get(4).getOutputs().get(1), nodes.get(8)
                .getInputs().get(1));
        workflow.connect(nodes.get(5).getOutputs().get(1), nodes.get(9)
                .getInputs().get(1));
        workflow.connect(nodes.get(6).getOutputs().get(1), nodes.get(8)
                .getInputs().get(1));
        workflow.connect(nodes.get(7).getOutputs().get(1), nodes.get(9)
                .getInputs().get(1));
    }
    
    @FXML
    public void onTestNontree3(ActionEvent e) {
        // generation
        System.out.println("generating Test Nontree 3");
        workflow = FlowFactory.newFlow();
        updateUI();
        workflowTest(workflow, 1, 10);
        // edges
        ObservableList<VNode> nodes = workflow.getNodes();
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(3)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(6)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(7)
                .getInputs().get(1));
        workflow.connect(nodes.get(5).getOutputs().get(1), nodes.get(8)
                .getInputs().get(1));
        workflow.connect(nodes.get(6).getOutputs().get(1), nodes.get(8)
                .getInputs().get(1));
        workflow.connect(nodes.get(8).getOutputs().get(1), nodes.get(9)
                .getInputs().get(1));
    }
    
    @FXML
    public void onTestCyclic1(ActionEvent e) {
        // generation
        System.out.println("generating Test Cyclic 1");
        workflow = FlowFactory.newFlow();
        updateUI();
        workflowTest(workflow, 1, 10);
        // edges
        ObservableList<VNode> nodes = workflow.getNodes();
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(3)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(3)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        workflow.connect(nodes.get(4).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(5).getOutputs().get(1), nodes.get(7)
                .getInputs().get(1));
        workflow.connect(nodes.get(6).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(6).getOutputs().get(1), nodes.get(8)
                .getInputs().get(1));
        workflow.connect(nodes.get(7).getOutputs().get(1), nodes.get(6)
                .getInputs().get(1));
        workflow.connect(nodes.get(7).getOutputs().get(1), nodes.get(9)
                .getInputs().get(1));
    }
    
    @FXML
    public void onTestCyclic2(ActionEvent e) {
        // generation
        System.out.println("generating Test Cyclic 2");
        workflow = FlowFactory.newFlow();
        updateUI();
        workflowTest(workflow, 1, 10);
        // edges
        ObservableList<VNode> nodes = workflow.getNodes();
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(1)
                .getInputs().get(1));
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(3)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        workflow.connect(nodes.get(4).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(4).getOutputs().get(1), nodes.get(6)
                .getInputs().get(1));
        workflow.connect(nodes.get(4).getOutputs().get(1), nodes.get(8)
                .getInputs().get(1));
        workflow.connect(nodes.get(5).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(6).getOutputs().get(1), nodes.get(7)
                .getInputs().get(1));
        workflow.connect(nodes.get(6).getOutputs().get(1), nodes.get(8)
                .getInputs().get(1));
        workflow.connect(nodes.get(7).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        workflow.connect(nodes.get(8).getOutputs().get(1), nodes.get(9)
                .getInputs().get(1));
    }
    
    @FXML
    public void onTestCyclic3(ActionEvent e) {
        // generation
        System.out.println("generating Test Cyclic 3");
        workflow = FlowFactory.newFlow();
        updateUI();
        workflowTest(workflow, 1, 10);
        // edges
        ObservableList<VNode> nodes = workflow.getNodes();
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(1)
                .getInputs().get(1));
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(3)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(4).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        workflow.connect(nodes.get(5).getOutputs().get(1), nodes.get(6)
                .getInputs().get(1));
        workflow.connect(nodes.get(6).getOutputs().get(1), nodes.get(7)
                .getInputs().get(1));
        workflow.connect(nodes.get(7).getOutputs().get(1), nodes.get(8)
                .getInputs().get(1));
        workflow.connect(nodes.get(8).getOutputs().get(1), nodes.get(9)
                .getInputs().get(1));
        workflow.connect(nodes.get(9).getOutputs().get(1), nodes.get(0)
                .getInputs().get(1));
    }
    
    @FXML
    public void onTestSizes1(ActionEvent e) {
        // generation
        System.out.println("generating Test Sizes 1");
        workflow = FlowFactory.newFlow();
        updateUI();
        workflowTest(workflow, 1, 6);
        // edges
        ObservableList<VNode> nodes = workflow.getNodes();
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(1)
                .getInputs().get(1));
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(3)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        workflow.connect(nodes.get(4).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        nodes.get(4).setWidth(nodes.get(4).getWidth() * 1.5);
        nodes.get(4).setHeight(nodes.get(4).getHeight() * 1.5);
        VFlowModel node5 = (VFlowModel) nodes.get(4);
        // subflow
        VNode n;
        int i;
        for(i = 0; i < 4; i++) {
            n = node5.newNode();
            n.setTitle("Node " + n.getId());
            n.addInput("event").getVisualizationRequest().set(
                    VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            n.addOutput("event").getVisualizationRequest().set(
                    VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            for (final Connector connector : n.getConnectors()) {
                connector.addClickEventListener(new EventHandler<ClickEvent>() {
                    @Override
                    public void handle(ClickEvent t) {
                        if (t.getButton() != MouseButton.SECONDARY) {
                            return;
                        }
                        if (t.getEvent() instanceof MouseEvent) {
                            MouseEvent evt = (MouseEvent) t.getEvent();
                            ContextMenu menu = new ContextMenu(new MenuItem(
                                    "Connector: " + connector.getId() 
                                            + ", btn: " + t.getButton()));
                            menu.show(rootPane, evt.getScreenX(),
                                    evt.getScreenY());
                        }
                    }
                });
            }
            n.setWidth(300);
            n.setHeight(200);
            n.setX((i % 5) * (n.getWidth() + 30));
            n.setY((i / 5) * (n.getHeight() + 30));
        }
        ObservableList<VNode> subnodes = node5.getNodes();
        node5.connect(subnodes.get(0).getOutputs().get(0), subnodes.get(1)
                .getInputs().get(0));
        node5.connect(subnodes.get(0).getOutputs().get(0), subnodes.get(2)
                .getInputs().get(0));
        node5.connect(subnodes.get(0).getOutputs().get(0), subnodes.get(3)
                .getInputs().get(0));
        node5.connect(subnodes.get(1).getOutputs().get(0), subnodes.get(3)
                .getInputs().get(0));
        node5.connect(subnodes.get(2).getOutputs().get(0), subnodes.get(3)
                .getInputs().get(0));
    }
    
    @FXML
    public void onTestSizes2(ActionEvent e) {
        // generation
        System.out.println("generating Test Sizes 2");
        workflow = FlowFactory.newFlow();
        updateUI();
        workflowTest(workflow, 1, 10);
        // edges
        ObservableList<VNode> nodes = workflow.getNodes();
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(1)
                .getInputs().get(1));
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(0).getOutputs().get(1), nodes.get(3)
                .getInputs().get(1));
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(6)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        workflow.connect(nodes.get(4).getOutputs().get(1), nodes.get(7)
                .getInputs().get(1));
        workflow.connect(nodes.get(5).getOutputs().get(1), nodes.get(8)
                .getInputs().get(1));
        workflow.connect(nodes.get(6).getOutputs().get(1), nodes.get(9)
                .getInputs().get(1));
        workflow.connect(nodes.get(7).getOutputs().get(1), nodes.get(9)
                .getInputs().get(1));
        workflow.connect(nodes.get(8).getOutputs().get(1), nodes.get(9)
                .getInputs().get(1));
        nodes.get(2).setWidth(nodes.get(2).getWidth() * 2);
        nodes.get(5).setWidth(nodes.get(5).getWidth() * 2);
        nodes.get(7).setWidth(nodes.get(7).getWidth() * 2);
        nodes.get(2).setHeight(nodes.get(2).getHeight() * 2);
        nodes.get(5).setHeight(nodes.get(5).getHeight() * 2);
        nodes.get(7).setHeight(nodes.get(7).getHeight() * 2);
    }
    
    @FXML
    public void onTestSizes3(ActionEvent e) {
        // generation
        System.out.println("generating Test Sizes 3");
        workflow = FlowFactory.newFlow();
        updateUI();
        workflowTest(workflow, 1, 10);
        // edges
        ObservableList<VNode> nodes = workflow.getNodes();
        workflow.connect(nodes.get(1).getOutputs().get(1), nodes.get(2)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(2).getOutputs().get(1), nodes.get(8)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(1)
                .getInputs().get(1));
        workflow.connect(nodes.get(3).getOutputs().get(1), nodes.get(6)
                .getInputs().get(1));
        workflow.connect(nodes.get(4).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        workflow.connect(nodes.get(5).getOutputs().get(1), nodes.get(3)
                .getInputs().get(1));
        workflow.connect(nodes.get(5).getOutputs().get(1), nodes.get(6)
                .getInputs().get(1));
        workflow.connect(nodes.get(6).getOutputs().get(1), nodes.get(8)
                .getInputs().get(1));
        workflow.connect(nodes.get(7).getOutputs().get(1), nodes.get(0)
                .getInputs().get(1));
        workflow.connect(nodes.get(7).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        workflow.connect(nodes.get(8).getOutputs().get(1), nodes.get(4)
                .getInputs().get(1));
        workflow.connect(nodes.get(8).getOutputs().get(1), nodes.get(0)
                .getInputs().get(1));
        workflow.connect(nodes.get(9).getOutputs().get(1), nodes.get(5)
                .getInputs().get(1));
        nodes.get(0).setWidth(nodes.get(0).getWidth() * 3);
        nodes.get(1).setWidth(nodes.get(1).getWidth() * 0.5);
        nodes.get(2).setWidth(nodes.get(2).getWidth() * 1.5);
        nodes.get(3).setWidth(nodes.get(3).getWidth() * 1.5);
        nodes.get(5).setWidth(nodes.get(5).getWidth() * 1.5);
        nodes.get(6).setWidth(nodes.get(6).getWidth() * 2);
        nodes.get(8).setWidth(nodes.get(8).getWidth() * 3);
        nodes.get(9).setWidth(nodes.get(9).getWidth() * 3);
        nodes.get(0).setHeight(nodes.get(0).getHeight() * 3);
        nodes.get(1).setHeight(nodes.get(1).getHeight() * 0.5);
        nodes.get(2).setHeight(nodes.get(2).getHeight() * 1.5);
        nodes.get(3).setHeight(nodes.get(3).getHeight() * 1.5);
        nodes.get(5).setHeight(nodes.get(5).getHeight() * 1.5);
        nodes.get(6).setHeight(nodes.get(6).getHeight() * 2);
        nodes.get(8).setHeight(nodes.get(8).getHeight() * 3);
        nodes.get(9).setHeight(nodes.get(9).getHeight() * 3);
    }
    
    @FXML
    public void onTestRunAll(ActionEvent e) {
        System.out.println("starting...");
        String abspath = new File(".").getAbsolutePath();
        String path = abspath.substring(0, abspath.length()-1);
        LayoutGenerator layouter;
        File dir = new File(path + "testimages");
        if(!dir.exists()) {
            System.out.println("Creating directory: " + dir.getAbsolutePath());
            dir.mkdir();
        }
        path += "testimages/";
        dir = new File(path + "smart");
        if(!dir.exists()) {
            System.out.println("Creating directoriy: " + dir.getAbsolutePath());
            dir.mkdir();
        }
        layouter = new LayoutGeneratorSmart(false);
        System.out.println("--- testing LayoutGeneratorSmart");
        runTests(layouter, (path + "smart/"), e);
        dir = new File(path + "naive");
        if(!dir.exists()) {
            System.out.println("Creating directory: " + dir.getAbsolutePath());
            dir.mkdir();
        }
        layouter = new LayoutGeneratorNaive(false);
        System.out.println("--- testing LayoutGeneratorNaive");
        runTests(layouter, (path + "naive/"), e);
        System.out.println("...finished");
    }
    
    /**
     * Runs all tests for the given layout generator and saves snapshots of the 
     * results in the given path.
     * @param layouter LayoutGenerator
     * @param path String
     * @param e ActionEvent
     */
    private void runTests(LayoutGenerator layouter, String path, ActionEvent e) {
        WritableImage wim = new WritableImage(1600, 800);
        SnapshotParameters parameter = new SnapshotParameters();
        parameter.setTransform(new Translate(0, 200));
        File dir;
        int i;
        // test tree 1
        System.out.println("Testing - TestTree1");
        for(i = 0; i < 10; i++) {
            onTestTree1(e);
            layouter.setWorkflow(workflow.getModel());
            layouter.generateLayout();
            try {
                rootPane.snapshot(parameter, wim);
                dir = new File(path + "TestTree1");
                if(!dir.exists()) {
                    System.out.println("Creating directory: " + path 
                            + "TestTree1");
                    dir.mkdir();
                }
                File out = new File(path + "TestTree1/" + i + ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", out);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        }
        // test tree 2
        System.out.println("Testing - TestTree2");
        for(i = 0; i < 10; i++) {
            onTestTree2(e);
            layouter.setWorkflow(workflow.getModel());
            layouter.generateLayout();
            try {
                rootPane.snapshot(parameter, wim);
                dir = new File(path + "TestTree2");
                if(!dir.exists()) {
                    System.out.println("Creating directory: " + path 
                            + "TestTree2");
                    dir.mkdir();
                }
                File out = new File(path + "TestTree2/" + i + ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", out);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, 
                        ex);
            }
        }
        // test tree 3
        System.out.println("Testing - TestTree3");
        for(i = 0; i < 10; i++) {
            onTestTree3(e);
            layouter.setWorkflow(workflow.getModel());
            layouter.generateLayout();
            try {
                rootPane.snapshot(parameter, wim);
                dir = new File(path + "TestTree3");
                if(!dir.exists()) {
                    System.out.println("Creating directory: " + path 
                            + "TestTree3");
                    dir.mkdir();
                }
                File out = new File(path + "TestTree3/" + i + ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", out);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, 
                        ex);
            }
        }
        // test nontree 1
        System.out.println("Testing - TestNontree1");
        for(i = 0; i < 10; i++) {
            onTestNontree1(e);
            layouter.setWorkflow(workflow.getModel());
            layouter.generateLayout();
            try {
                rootPane.snapshot(parameter, wim);
                dir = new File(path + "TestNontree1");
                if(!dir.exists()) {
                    System.out.println("Creating directory: " + path 
                            + "TestNontree1");
                    dir.mkdir();
                }
                File out = new File(path + "TestNontree1/" + i + ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", out);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, 
                        ex);
            }
        }
        // test nontree 2
        System.out.println("Testing - TestNontree2");
        for(i = 0; i < 10; i++) {
            onTestNontree2(e);
            layouter.setWorkflow(workflow.getModel());
            layouter.generateLayout();
            try {
                rootPane.snapshot(parameter, wim);
                dir = new File(path + "TestNontree2");
                if(!dir.exists()) {
                    System.out.println("Creating directory: " + path 
                            + "TestNontree2");
                    dir.mkdir();
                }
                File out = new File(path + "TestNontree2/" + i + ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", out);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, 
                        ex);
            }
        }
        // test nontree 3
        System.out.println("Testing - TestNontree3");
        for(i = 0; i < 10; i++) {
            onTestNontree3(e);
            layouter.setWorkflow(workflow.getModel());
            layouter.generateLayout();
            try {
                rootPane.snapshot(parameter, wim);
                dir = new File(path + "TestNontree3");
                if(!dir.exists()) {
                    System.out.println("Creating directory: " + path 
                            + "TestNontree3");
                    dir.mkdir();
                }
                File out = new File(path + "TestNontree3/" + i + ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", out);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, 
                        ex);
            }
        }
        // test cyclic 1
        System.out.println("Testing - TestCyclic1");
        for(i = 0; i < 10; i++) {
            onTestCyclic1(e);
            layouter.setWorkflow(workflow.getModel());
            layouter.generateLayout();
            try {
                rootPane.snapshot(parameter, wim);
                dir = new File(path + "TestCyclic1");
                if(!dir.exists()) {
                    System.out.println("Creating directory: " + path 
                            + "TestCyclic1");
                    dir.mkdir();
                }
                File out = new File(path + "TestCyclic1/" + i + ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", out);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, 
                        ex);
            }
        }
        // test cyclic 2
        System.out.println("Testing - TestCyclic2");
        for(i = 0; i < 10; i++) {
            onTestCyclic2(e);
            layouter.setWorkflow(workflow.getModel());
            layouter.generateLayout();
            try {
                rootPane.snapshot(parameter, wim);
                dir = new File(path + "TestCyclic2");
                if(!dir.exists()) {
                    System.out.println("Creating directory: " + path 
                            + "TestCyclic2");
                    dir.mkdir();
                }
                File out = new File(path + "TestCyclic2/" + i + ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", out);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, 
                        ex);
            }
        }
        // test cyclic 3
        System.out.println("Testing - TestCyclic3");
        for(i = 0; i < 10; i++) {
            onTestCyclic3(e);
            layouter.setWorkflow(workflow.getModel());
            layouter.generateLayout();
            try {
                rootPane.snapshot(parameter, wim);
                dir = new File(path + "TestCyclic3");
                if(!dir.exists()) {
                    System.out.println("Creating directory: " + path 
                            + "TestCyclic3");
                    dir.mkdir();
                }
                File out = new File(path + "TestCyclic3/" + i + ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", out);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, 
                        ex);
            }
        }
        // test sizes 1
        System.out.println("Testing - TestSizes1");
        for(i = 0; i < 10; i++) {
            onTestSizes1(e);
            layouter.setWorkflow(workflow.getModel());
            layouter.generateLayout();
            try {
                rootPane.snapshot(parameter, wim);
                dir = new File(path + "TestSizes1");
                if(!dir.exists()) {
                    System.out.println("Creating directory: " + path 
                            + "TestSizes1");
                    dir.mkdir();
                }
                File out = new File(path + "TestSizes1/" + i + ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", out);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, 
                        ex);
            }
        }
        // test sizes 2
        System.out.println("Testing - TestSizes2");
        for(i = 0; i < 10; i++) {
            onTestSizes2(e);
            layouter.setWorkflow(workflow.getModel());
            layouter.generateLayout();
            try {
                rootPane.snapshot(parameter, wim);
                dir = new File(path + "TestSizes2");
                if(!dir.exists()) {
                    System.out.println("Creating directory: " + path 
                            + "TestSizes2");
                    dir.mkdir();
                }
                File out = new File(path + "TestSizes2/" + i + ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", out);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, 
                        ex);
            }
        }
        // test sizes 3
        System.out.println("Testing - TestSizes3");
        for(i = 0; i < 10; i++) {
            onTestSizes3(e);
            layouter.setWorkflow(workflow.getModel());
            layouter.generateLayout();
            try {
                rootPane.snapshot(parameter, wim);
                dir = new File(path + "TestSizes3");
                if(!dir.exists()) {
                    System.out.println("Creating directory: " + path 
                            + "TestSizes3");
                    dir.mkdir();
                }
                File out = new File(path + "TestSizes3/" + i + ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", out);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, 
                        ex);
            }
        }
    }
    
    // </editor-fold>
    
    @FXML
    public void onStableAction(ActionEvent e) {
        LayoutGeneratorSmart layouter = new LayoutGeneratorSmart(false);
        layouter.setWorkflow(workflow.getModel());
        layouter.generateLayout();
    }

    @FXML
    public void onGenerateAction(ActionEvent e) {

        counter = 0;

        System.out.print(" >> generate workflow");

        workflow = FlowFactory.newFlow();

        updateUI();

        workflowTest(workflow, 5, 10);
//        workflowTest(workflow, 2, 2);

        System.out.println(" [done]");

        System.out.println(" --> #nodes: " + counter);

    }
    @FXML
    public Pane contentPane;

    /**
     * @return the rootPane
     */
    public Pane getRootPane() {
        return rootPane;
    }

    public void workflowTest(VFlow workflow, int depth, int width) {

        if (depth < 1) {
            return;
        }

        if ("4687".equals(workflow.getModel().getId())) {
            System.out.println("FLOW: " + workflow.getModel().getId());
            specialViewFlow1 = workflow;
        }

        if ("4688".equals(workflow.getModel().getId())) {
            System.out.println("FLOW: " + workflow.getModel().getId());
            specialViewFlow2 = workflow;
        }

        String[] connectionTypes = {"control", "data", "event"};

        for (int i = 0; i < width; i++) {

            counter++;

            VNode n;

            if (i % 2 == 0) {
                VFlow subFlow = workflow.newSubFlow();
                n = subFlow.getModel();
                workflowTest(subFlow, depth - 1, width);
            } else {
                n = workflow.newNode();
            }

            n.setTitle("Node " + n.getId());
            
//            n.getVisualizationRequest().set(VisualizationRequest.KEY_MAX_CONNECTOR_SIZE, 10.0);

            String type = connectionTypes[i % connectionTypes.length];

            n.setMainInput(n.addInput(type)).getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            n.setMainInput(n.addInput("event")).getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            
//            n.getVisualizationRequest().set(VisualizationRequest.KEY_DISABLE_EDITING, true);

            for (int j = 0; j < 3; j++) {
                n.addInput(type).getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            }

//            n.addInput(type);
//            n.addInput(type);
            n.addOutput(type).getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            n.setMainOutput(n.addOutput("event")).getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            n.addOutput(type).getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);

            for (int j = 0; j < 3; j++) {
                n.addOutput(type).getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            }

            for (final Connector connector : n.getConnectors()) {
                connector.addClickEventListener(new EventHandler<ClickEvent>() {

                    @Override
                    public void handle(ClickEvent t) {

                        if (t.getButton() != MouseButton.SECONDARY) {
                            return;
                        }

                        System.out.println("Connector: " + connector.getId() + ", btn: " + t.getButton());
                        if (t.getEvent() instanceof MouseEvent) {
                            MouseEvent evt = (MouseEvent) t.getEvent();

                            ContextMenu menu = new ContextMenu(new MenuItem("Connector: " + connector.getId() + ", btn: " + t.getButton()));

                            menu.show(rootPane, evt.getScreenX(), evt.getScreenY());
                        }
                    }
                });
            }

            n.setWidth(300);
            n.setHeight(200);

            n.setX((i % 5) * (n.getWidth() + 30));
            n.setY((i / 5) * (n.getHeight() + 30));
        }
    }

    private void updateUI() {

        rootPane.getChildren().clear();

//        ScalableContentPane minimapPane1 = createMinimap("Minimap 1");
//        ScalableContentPane minimapPane2 = createMinimap("Minimap 2");
//
//        if (workflow == null) {
//            return;
//        }
        workflow.getModel().setVisible(true);

        FXSkinFactory skinFactory = new FXSkinFactory(rootPane);

        workflow.setSkinFactories(skinFactory);
        
       

//        skinFactory.setConnectionFillColor("control", new Color(1.0, 1.0, 0.0, 0.75));
//        skinFactory.setConnectionStrokeColor("control", new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
//
//        skinFactory.setConnectionFillColor("data", new Color(0.1, 0.1, 0.1, 0.5));
//        skinFactory.setConnectionStrokeColor("data", new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
//
//        skinFactory.setConnectionFillColor("event", new Color(255.0 / 255.0, 100.0 / 255.0, 1, 0.5));
//        skinFactory.setConnectionStrokeColor("event", new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
//        workflow.addSkinFactories(new FXSkinFactory(minimapPane1.getContentPane()),
//                new FXSkinFactory(minimapPane2.getContentPane()));
//        ScalableContentPane minimapPane3 = createMinimap("Minimap 3");
//        ScalableContentPane minimapPane4 = createMinimap("Minimap 4");
//
//        if (specialViewFlow1 != null) {
//            specialViewFlow1.addSkinFactories(new FXSkinFactory(minimapPane3.getContentPane()));
//        }
//
//        if (specialViewFlow2 != null) {
//            specialViewFlow2.addSkinFactories(new FXSkinFactory(minimapPane4.getContentPane()));
//        }
//
//        workflow.newSubFlow();
    }

    private ScalableContentPane createMinimap(String title) {
        //        clipboard = new Window("Clipboard/Broken!");
        //        clipboard.setPrefSize(80, 80);
        //        clipboard.setResizableWindow(false);
        //
        //        clipboard.setVisible(false);
        //        rootPane.getChildren().add(clipboard);
        OptimizableContentPane minimapContent = new OptimizableContentPane();
        ScalableContentPane minimapPane = new ScalableContentPane();
        minimapContent.getChildren().add(minimapPane);
        Window minimap = new Window(title);
        minimap.setStyle("-fx-background-color: rgba(120,140,255,0.2);-fx-border-color: rgba(120,140,255,0.42);-fx-border-width: 2;");
        minimap.setPrefSize(300, 200);
        minimap.setContentPane(minimapContent);
        rootPane.getChildren().add(minimap);
        return minimapPane;
    }
//    void registerShell(VRLShell shell) {
//        shell.addConstant("flow", workflow);
//    }
}
