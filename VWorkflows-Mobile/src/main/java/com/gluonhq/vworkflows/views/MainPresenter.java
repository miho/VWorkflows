package com.gluonhq.vworkflows.views;

import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.BottomNavigationButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.vworkflows.VWMobileApp;
import java.util.ResourceBundle;

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import eu.mihosoft.vrl.workflow.fx.FXValueSkinFactory;
import eu.mihosoft.vrl.workflow.fx.ScalableContentPane;
import javafx.fxml.FXML;

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
        canvas.widthProperty().addListener((o, ov, nv) -> System.out.println("nv = " + nv));
        canvas.getStyleClass().setAll("vflow-background");
        canvas.setMinScaleX(0.5);
        canvas.setMinScaleY(0.5);
        canvas.setMaxScaleX(1.0);
        canvas.setMaxScaleY(1.0);

        VFlow flow = FlowFactory.newFlow();
        flow.setVisible(true);

        FXValueSkinFactory fXSkinFactory = new FXValueSkinFactory(canvas);
        flow.setSkinFactories(fXSkinFactory);

        main.setCenter(canvas);

        blockVar.setOnAction(e -> {
            int size = flow.getNodes().size();
            VNode n1 = flow.newNode();
            n1.setX(size * 10);
            n1.setY(size * 10);
            n1.setTitle("Variable");
            Connector c1o = n1.addOutput("data");
            c1o.getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            n1.setMainOutput(c1o);
        });

        blockFor.setOnAction(e -> {
            int size = flow.getNodes().size();
            VNode n1 = flow.newNode();
            n1.setX(size * 10);
            n1.setY(size * 10);
            n1.setTitle("For loop");
            Connector c1i = n1.addInput("data");
            c1i.getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            n1.setMainInput(c1i);
            Connector c1o = n1.addOutput("control");
            c1o.getVisualizationRequest().set(VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
            n1.setMainOutput(c1o);
        });
    }
    
}
