package com.gluonhq.vworkflows.views;

import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.BottomNavigationButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.vworkflows.VWMobileApp;
import com.gluonhq.vworkflows.lang.FlowToJExprCompiler;
import com.gluonhq.vworkflows.lang.FlowToJExprCompiler.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.gluonhq.vworkflows.views.helper.DoubleFlowNodeSkin;
import com.gluonhq.vworkflows.views.helper.DoubleValue;
import com.gluonhq.vworkflows.views.helper.OperatorFlowNodeSkin;
import com.gluonhq.vworkflows.views.helper.OperatorValue;
import com.gluonhq.vworkflows.views.helper.FunctionFlowNodeSkin;
import com.gluonhq.vworkflows.views.helper.FunctionValue;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.DefaultValueObject;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.ValueObject;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import eu.mihosoft.vrl.workflow.fx.FXValueSkinFactory;
import eu.mihosoft.vrl.workflow.fx.ScalableContentPane;
import javafx.fxml.FXML;

import javafx.beans.InvalidationListener;

public class MainPresenter extends GluonPresenter<VWMobileApp> {

    @FXML private View main;
    @FXML private BottomNavigationButton blockFor;
    @FXML private BottomNavigationButton blockIf;
    @FXML private BottomNavigationButton blockVar;
    @FXML private BottomNavigationButton blockMath;

    @FXML private ResourceBundle resources;
    
    public void initialize() {
        main.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = getApp().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
                        getApp().getDrawer().open()));
                appBar.setTitleText("Main");
                appBar.getActionItems().add(MaterialDesignIcon.SEARCH.button(e -> 
                        System.out.println("Search")));
            }
        });

        // create scalable root pane
        ScalableContentPane canvas = new ScalableContentPane();
        canvas.getStyleClass().setAll("vflow-background");
        canvas.setMinScaleX(0.5);
        canvas.setMinScaleY(0.5);
        canvas.setMaxScaleX(1.0);
        canvas.setMaxScaleY(1.0);

        VFlow flow = FlowFactory.newFlow();
        flow.setVisible(true);

        FlowToJExprCompiler compiler = new FlowToJExprCompiler();

        InvalidationListener l  = o -> {
            Result code = compiler.compile(flow);

            System.out.println("> expression: " + code);
        };

        flow.getNodes().addListener(l);
        flow.getConnections("data").getConnections().addListener(l);

        FXValueSkinFactory fXSkinFactory = new FXValueSkinFactory(canvas);
        fXSkinFactory.addSkinClassForValueType(OperatorValue.class, OperatorFlowNodeSkin.class);
        fXSkinFactory.addSkinClassForValueType(DoubleValue.class, DoubleFlowNodeSkin.class);
        fXSkinFactory.addSkinClassForValueType(FunctionValue.class, FunctionFlowNodeSkin.class);
        flow.setSkinFactories(fXSkinFactory);

        main.setCenter(canvas);

        blockVar.setOnAction(e -> {
            int size = flow.getNodes().size();
            ValueObject valueObject = new DefaultValueObject();
            valueObject.setValue(new DoubleValue("Value"));
            VNode n1 = flow.newNode(valueObject);
            n1.setX(size * 10);
            n1.setY(size * 10);
            n1.setTitle("Number");
            Connector c1o = n1.addOutput("data");
            c1o.getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            n1.setMainOutput(c1o);
        });

        blockFor.setOnAction(e -> {
            int size = flow.getNodes().size();
            ValueObject valueObject = new DefaultValueObject();
            valueObject.setValue(new FunctionValue("Function"));
            VNode n1 = flow.newNode(valueObject);
            n1.setX(size * 10);
            n1.setY(size * 10);
            n1.setTitle("Function");
            Connector c1i = n1.addInput("data");
            c1i.getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            n1.setMainInput(c1i);
            Connector c1o = n1.addOutput("data");
            c1o.getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            n1.setMainOutput(c1o);
        });

        blockMath.setOnAction(e -> {
            int size = flow.getNodes().size();
            ValueObject valueObject = new DefaultValueObject();
            valueObject.setValue(new OperatorValue("Operator"));
            VNode n1 = flow.newNode(valueObject);
            n1.setX(size * 10);
            n1.setY(size * 10);
            n1.setTitle("Operator");
            Connector c1i = n1.addInput("data");
            c1i.getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            n1.setMainInput(c1i);
            Connector c2i = n1.addInput("data");
            c2i.getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            Connector c1o = n1.addOutput("data");
            c1o.getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            n1.setMainOutput(c1o);
        });
    }

    // private String flowToString(VFlow f) {

    //     Predicate<Connector> notConnected = (c) -> {
    //         return f.getConnections(c.getType()).getAllWith(c).isEmpty();
    //     };

    //     Predicate<Connector> connected = (c) -> {
    //         return !f.getConnections(c.getType()).getAllWith(c).isEmpty();
    //     };

    //     Predicate<VNode> isTerminal = (n) -> {
    //         return n.getInputs().size() == 0;
    //     };

    //     Predicate<VNode> isReturnValue = (n) -> {
    //         return n.getOutputs().stream().filter(connected).count() == 0;
    //     };

    //     List<VNode> retVal = f.getNodes().filtered(isReturnValue);

    //     if (retVal.size() != 1) {
    //         return "ERROR: wrong number of return values. Got '" + retVal.size() + "', expected 1.";
    //     }

    //     List<VNode> terminals = f.getNodes().filtered(isTerminal);

    //     VNode root = retVal.get(0);

    //     return nodeToString(root);
        
    // }

    // String nodeToString(VNode n) {
    //     if(n.getValueObject().getValue()==null) {
    //         return "/*null*/";
    //     } else

    //     if(n.getValueObject().getValue() instanceof DoubleValue) {
    //         DoubleValue v = (DoubleValue) n.getValueObject().getValue();
    //         return ""+v.getValue();
    //     } else

    //     if(n.getValueObject().getValue() instanceof FunctionValue) {
    //         FunctionValue v = (FunctionValue) n.getValueObject().getValue();

    //         // TODO ensure exactly one connected object

    //         switch(v.getValue()) {
    //             case SINE:   return "Math.sin(" + nodeToString(getInputs(n, "data").get(0))+ ")";
    //             case COSINE: return "Math.cos(" + nodeToString(getInputs(n, "data").get(0))+ ")";
    //             default:     return "/*unsupported*/";            
    //         }
    //     } else 

    //     if(n.getValueObject().getValue() instanceof OperatorValue) {
    //         OperatorValue v = (OperatorValue) n.getValueObject().getValue();

    //         // TODO ensure exactly two connected object

    //         switch(v.getValue()) {
    //             case ADD:      return "("+nodeToString(getInputs(n, "data").get(0)) + " + " + nodeToString(getInputs(n, "data").get(1))+")";
    //             case SUBTRACT: return "("+nodeToString(getInputs(n, "data").get(0)) + " - " + nodeToString(getInputs(n, "data").get(1))+")";                        
    //             case MULTIPLY: return "("+nodeToString(getInputs(n, "data").get(0)) + " * " + nodeToString(getInputs(n, "data").get(1))+")";
    //             case DIVIDE:   return "("+nodeToString(getInputs(n, "data").get(0)) + " / " + nodeToString(getInputs(n, "data").get(1))+")";                        
                            
    //             default: 
    //                         return "/*unsupported*/";            
    //         }
    //     }
        
    //     return "/*unsupported*/";
    // }

    // List<VNode> getInputs(VNode n, String type) {
    //     List<VNode> result = new ArrayList<>();
    //     for(Connector c : n.getInputs()) {
    //         List<VNode> inputs = n.getFlow().getAllConnections().get(type).getAllWith(c).stream().
    //           map(conn->conn.getSender().getNode()).collect(Collectors.toList());
    //         result.addAll(inputs);  
    //     }

    //     return result;
    // }
    
}
