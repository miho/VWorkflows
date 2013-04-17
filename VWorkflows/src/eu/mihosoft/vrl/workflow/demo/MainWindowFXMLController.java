/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.demo;

import eu.mihosoft.vrl.workflow.DefaultWorkflow;
import eu.mihosoft.vrl.workflow.FlowController;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.FlowFlowNode;
import eu.mihosoft.vrl.workflow.FlowNode;
import eu.mihosoft.vrl.workflow.IdGenerator;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import eu.mihosoft.vrl.workflow.io.WorkflowIO;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import jfxtras.labs.scene.layout.ScalableContentPane;

/**
 * FXML Controller class
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class MainWindowFXMLController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ScalableContentPane canvas = new ScalableContentPane();

//        Pane root = canvas.getContentPane();

        Pane root = new Pane();

        canvas.setContentPane(root);

        root.setStyle("-fx-background-color: linear-gradient(to bottom, rgb(10,32,60), rgb(42,52,120));");
        
        contentPane.getChildren().add(canvas);
        
        rootPane = root;
    }
    private Pane rootPane;
    private FlowController workflow;

    @FXML
    public void onLoadAction(ActionEvent e) {
        System.out.print(" >> loading workflow from xml");

        workflow = FlowFactory.newFlow();
        try {
            FlowFlowNode flow = WorkflowIO.loadFromXML(Paths.get("flow01.xml"), workflow.getIdGenerator());
            workflow.setModel(flow);
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

    @FXML
    public void onGenerateAction(ActionEvent e) {
        
        System.out.print(" >> generate workflow");

        workflow = new DefaultWorkflow();
        workflowTest(workflow, 3, 6);

        System.out.println(" [done]");
        
        updateUI();
    }
    
    @FXML
    public Pane contentPane;

    /**
     * @return the rootPane
     */
    public Pane getRootPane() {
        return rootPane;
    }

  

    public void workflowTest(FlowController workflow, int depth, int width) {

        if (depth < 1) {
            return;
        }

        FlowNode prevNode = null;

        for (int i = 0; i < width; i++) {

            FlowNode n;

            if (i % 2 == 0) {
                FlowController subFlow = workflow.newSubFlow();
                n = subFlow.getModel();
                workflowTest(subFlow, depth - 1, width);
            } else {
                n = workflow.newNode();

            }

//            n.setTitle("Node " + i);
            n.setTitle("Node " + n.getId());
            
            
            System.out.println("n: " + n.getId());
            
            n.setWidth(300);
            n.setHeight(200);

            n.setX((i % 5) * (n.getWidth() + 30));
            n.setY((i / 5) * (n.getHeight() + 30));

//            if (prevNode != null) {
//                workflow.connect(prevNode, n, "control");
//            }

            prevNode = n;
        }

    }

    private void updateUI() {
        
        rootPane.getChildren().clear();
        
        if (workflow == null) {
            return;
        }
        
        workflow.getModel().setVisible(true);

        workflow.setSkinFactory(new FXSkinFactory(rootPane));
    }
}
