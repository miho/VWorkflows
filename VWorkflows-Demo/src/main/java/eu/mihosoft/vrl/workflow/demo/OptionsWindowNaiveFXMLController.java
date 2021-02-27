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

import eu.mihosoft.vrl.workflow.incubating.LayoutGeneratorNaive;
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
 * @author Tobias Mertz
 */
public class OptionsWindowNaiveFXMLController implements Initializable {
    
    @FXML
    public Pane contentPane;
    
    private LayoutGeneratorNaive generator;
    private Stage optionsstage;
    private VFlow workflow;
    private final String[] graphmodes = new String[3];

    /**
     * Initializes the controller class.
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.graphmodes[0] = "VFlowModel";
        this.graphmodes[1] = "-";
        this.graphmodes[2] = "nodelist";
        ObservableList<String> graphmodelist = this.selGraphmode.getItems();
        graphmodelist.add(this.graphmodes[0]);
        graphmodelist.add(this.graphmodes[1]);
        graphmodelist.add(this.graphmodes[2]);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Menu items">
    @FXML
    private CheckBox checkCalcVertPos;

    @FXML
    private CheckBox checkRemoveCycles;

    @FXML
    private TextField tfSubflowscale;

    @FXML
    private ComboBox<String> selGraphmode;

    @FXML
    private CheckBox checkAutoscaleNodes;

    @FXML
    private CheckBox checkCreateLayering;

    @FXML
    private TextField tfScaling;

    @FXML
    private CheckBox checkRecursive;

    @FXML
    private CheckBox checkCalcHorPos;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Setter">
    public void setGenerator(LayoutGeneratorNaive pgenerator) {
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
    public LayoutGeneratorNaive getGenerator() {
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
    void onOkPress(ActionEvent event) {
        accept();
        this.optionsstage.close();
    }

    @FXML
    void onCancelPress(ActionEvent event) {
        set();
        this.optionsstage.close();
    }

    @FXML
    void onShowPress(ActionEvent event) {
        int i;
        accept();
        switch(this.generator.getGraphmode()) {
            case 0:
                this.generator.setWorkflow(this.workflow.getModel());
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
        this.checkAutoscaleNodes.setSelected(this.generator
                .getAutoscaleNodes());
        this.checkCalcHorPos.setSelected(this.generator
                .getLaunchCalculateHorizontalPositions());
        this.checkCalcVertPos.setSelected(this.generator
                .getLaunchCalculateVerticalPositions());
        this.checkCreateLayering.setSelected(this.generator
                .getLaunchCreateLayering());
        this.checkRecursive.setSelected(this.generator.getRecursive());
        this.checkRemoveCycles.setSelected(this.generator
                .getLaunchRemoveCycles());
        this.selGraphmode.setValue(this.graphmodes[this.generator
                .getGraphmode()]);
        this.tfScaling.setText(Double.toString(this.generator.getScaling()));
        this.tfSubflowscale.setText(Double.toString(this.generator
                .getSubflowscale()));
    }
    
    /**
     * Sets the values of all parameters of the layout generator to the current 
     * values of the menu items.
     */
    private void accept() {
        int i;
        double j;
        String temp;
        this.generator.setAutoscaleNodes(this.checkAutoscaleNodes.isSelected());
        this.generator.setLaunchCalculateHorizontalPositions(
                this.checkCalcHorPos.isSelected());
        this.generator.setLaunchCalculateVerticalPositions(
                this.checkCalcVertPos.isSelected());
        this.generator.setLaunchCreateLayering(this.checkCreateLayering
                .isSelected());
        this.generator.setRecursive(this.checkRecursive.isSelected());
        this.generator.setLaunchRemoveCycles(this.checkRemoveCycles
                .isSelected());
        temp = this.selGraphmode.getValue();
        for(i = 0; i < this.graphmodes.length; i++) {
            if(this.graphmodes[i].equals(temp)) {
                if(i != 1) this.generator.setGraphmode(i);
            }
        }
        j = this.generator.getScaling();
        temp = this.tfScaling.getText();
        try {
            j = Double.parseDouble(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setScaling(j);
        j = this.generator.getSubflowscale();
        temp = this.tfSubflowscale.getText();
        try {
            j = Double.parseDouble(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setSubflowscale(j);
    }
}
