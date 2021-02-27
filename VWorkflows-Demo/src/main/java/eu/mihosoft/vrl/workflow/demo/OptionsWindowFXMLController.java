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
import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.incubating.LayoutGeneratorSmart;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller Class
 * 
 * 
 * @author Tobias Mertz
 */
public class OptionsWindowFXMLController implements Initializable {

    @FXML
    public Pane contentPane;
    
    private LayoutGeneratorSmart generator;
    private Stage optionsstage;
    private VFlow workflow;
    private final String[] layouts = new String[4];
    private final String[] graphmodes = new String[3];

    /**
     * Initializes the controller class.
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.layouts[0] = "ISOM Layout";
        this.layouts[1] = "FR Layout";
        this.layouts[2] = "KK Layout";
        this.layouts[3] = "DAG Layout";
        this.graphmodes[0] = "VFlowModel";
        this.graphmodes[1] = "jgraph";
        this.graphmodes[2] = "nodelist";
        ObservableList<String> layoutlist = this.selLayout.getItems();
        layoutlist.add(this.layouts[0]);
        layoutlist.add(this.layouts[1]);
        layoutlist.add(this.layouts[2]);
        layoutlist.add(this.layouts[3]);
        ObservableList<String> graphmodelist = this.selGraphmode.getItems();
        graphmodelist.add(this.graphmodes[0]);
        graphmodelist.add(this.graphmodes[1]);
        graphmodelist.add(this.graphmodes[2]);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Menu items">
    @FXML
    private CheckBox checkForcePush;
    
    @FXML
    private CheckBox checkSeparateDisjunctGraphs;
    
    @FXML
    private CheckBox checkSeparateEdgeTypes;
    
    @FXML
    private CheckBox checkDisplaceIdents;
    
    @FXML
    private TextField tfMaxiterations;
    
    @FXML
    private CheckBox checkRecursive;
    
    @FXML
    private TextField tfAspectratio;
    
    @FXML
    private TextField tfScaling;
    
    @FXML
    private ComboBox<String> selLayout;
    
    @FXML
    private CheckBox checkAlignNodes;
    
    @FXML
    private CheckBox checkRemoveCycles;
    
    @FXML
    private CheckBox checkOrigin;
    
    @FXML
    private TextField tfSubflowscale;
    
    @FXML
    private CheckBox checkRotate;
    
    @FXML
    private CheckBox checkPushBack;
    
    @FXML
    private CheckBox checkAutoscaleNodes;
    
    @FXML
    private CheckBox checkJungLayout;
    
    @FXML
    private ComboBox<String> selGraphmode;
    
    @FXML
    private TextField tfDirection;
    
    @FXML
    private TextField tfAlignmentThreshold;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setter">
    public void setGenerator(LayoutGeneratorSmart pgenerator) {
        this.generator = pgenerator;
    }
    
    public void setStage(Stage poptionsstage) {
        this.optionsstage = poptionsstage;
    }
    
    public void setWorkflow(VFlow pworkflow) {
        this.workflow = pworkflow;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Getter">
    public LayoutGeneratorSmart getGenerator() {
        return this.generator;
    }
    
    public Stage getStage() {
        return this.optionsstage;
    }
    
    public VFlow getWorkflow() {
        return this.workflow;
    }
    // </editor-fold>

    @FXML
    void onOkPress(ActionEvent e) {
        accept();
        this.optionsstage.close();
    }

    @FXML
    void onCancelPress(ActionEvent e) {
        set();
        this.optionsstage.close();
    }
    
    @FXML
    void onShowPress(ActionEvent e) {
        int i;
        accept();
        switch(this.generator.getGraphmode()) {
            case 0:
                this.generator.setWorkflow(this.workflow.getModel());
                this.generator.generateLayout();
                break;
            case 1:
                LayoutGeneratorSmart altlay = new LayoutGeneratorSmart();
                altlay.setWorkflow(this.workflow.getModel());
                altlay.generateLayout();
                DirectedGraph<VNode, Connection> jgraph = 
                        altlay.getModelGraph();
                this.generator.setModelGraph(jgraph);
                this.generator.generateLayout();
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
                    this.generator.setNodelist(nodelist);
                    this.generator.generateLayout();
                }
                break;
        }
    }
    
    /**
     * Sets the values of all menu items to the current values of the layout 
     * generator.
     */
    public void set() {
        this.checkForcePush.setSelected(this.generator.getLaunchForcePush());
        this.checkSeparateDisjunctGraphs.setSelected(this.generator
                .getLaunchSeparateDisjunctGraphs());
        this.checkSeparateEdgeTypes.setSelected(this.generator
                .getLaunchSeparateEdgeTypes());
        this.checkDisplaceIdents.setSelected(this.generator
                .getLaunchDisplaceIdents());
        this.tfMaxiterations.setText(Integer.toString(this.generator
                .getMaxiterations()));
        this.checkRecursive.setSelected(this.generator.getRecursive());
        this.tfAspectratio.setText(Double.toString(this.generator
                .getAspectratio()));
        this.tfScaling.setText(Double.toString(this.generator.getScaling()));
        this.selLayout.setValue(this.layouts[this.generator
                .getLayoutSelector()]);
        this.checkAlignNodes.setSelected(this.generator.getLaunchAlignNodes());
        this.checkRemoveCycles.setSelected(this.generator
                .getLaunchRemoveCycles());
        this.checkOrigin.setSelected(this.generator.getLaunchOrigin());
        this.tfSubflowscale.setText(Double.toString(this.generator
                .getSubflowscale()));
        this.checkRotate.setSelected(this.generator.getLaunchRotate());
        this.checkPushBack.setSelected(this.generator.getLaunchPushBack());
        this.checkAutoscaleNodes.setSelected(this.generator
                .getAutoscaleNodes());
        this.checkJungLayout.setSelected(this.generator.getLaunchJungLayout());
        this.selGraphmode.setValue(this.graphmodes[this.generator
                .getGraphmode()]);
        this.tfDirection.setText(Double.toString(this.generator
                .getDirection()));
        this.tfAlignmentThreshold.setText(Double.toString(this.generator
                .getAlignmentThreshold()));
    }

    /**
     * Sets the values of all parameters of the layout generator to the current 
     * values of the menu items.
     */
    private void accept() {
        int i;
        double j;
        String temp;
        this.generator.setLaunchForcePush(this.checkForcePush.isSelected());
        this.generator.setLaunchSeparateDisjunctGraphs(this
                .checkSeparateDisjunctGraphs.isSelected());
        this.generator.setLaunchSeparateEdgeTypes(this.checkSeparateEdgeTypes
                .isSelected());
        this.generator.setLaunchDisplaceIdents(this.checkDisplaceIdents
                .isSelected());
        i = this.generator.getMaxiterations();
        temp = this.tfMaxiterations.getText();
        try {
            i = Integer.parseInt(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setMaxiterations(i);
        this.generator.setRecursive(this.checkRecursive.isSelected());
        j = this.generator.getAspectratio();
        temp = this.tfAspectratio.getText();
        try {
            j = Double.parseDouble(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setAspectratio(j);
        j = this.generator.getScaling();
        temp = this.tfScaling.getText();
        try {
            j = Double.parseDouble(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setScaling(j);
        temp = this.selLayout.getValue();
        for(i = 0; i < this.layouts.length; i++) {
            if(this.layouts[i].equals(temp)) {
                this.generator.setLayoutSelector(i);
            }
        }
        this.generator.setLaunchAlignNodes(this.checkAlignNodes.isSelected());
        this.generator.setLaunchRemoveCycles(this.checkRemoveCycles
                .isSelected());
        this.generator.setLaunchOrigin(this.checkOrigin.isSelected());
        j = this.generator.getSubflowscale();
        temp = this.tfSubflowscale.getText();
        try {
            j = Double.parseDouble(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setSubflowscale(j);
        this.generator.setLaunchRotate(this.checkRotate.isSelected());
        this.generator.setLaunchPushBack(this.checkPushBack.isSelected());
        this.generator.setAutoscaleNodes(this.checkAutoscaleNodes.isSelected());
        this.generator.setLaunchJungLayout(this.checkJungLayout.isSelected());
        temp = this.selGraphmode.getValue();
        for(i = 0; i < this.graphmodes.length; i++) {
            if(this.graphmodes[i].equals(temp)) {
                this.generator.setGraphmode(i);
            }
        }
        j = this.generator.getDirection();
        temp = this.tfDirection.getText();
        try {
            j = Double.parseDouble(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setDirection(j);
        j = this.generator.getAlignmentThreshold();
        temp = this.tfAlignmentThreshold.getText();
        try {
            j = Double.parseDouble(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setAlignmentThreshold(j);
    }
}
