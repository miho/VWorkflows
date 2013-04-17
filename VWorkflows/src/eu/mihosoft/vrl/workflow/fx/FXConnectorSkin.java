///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package eu.mihosoft.vrl.workflow.fx;
//
//import eu.mihosoft.vrl.workflow.Connection;
//import eu.mihosoft.vrl.workflow.Connector;
//import eu.mihosoft.vrl.workflow.ConnectorSkin;
//import eu.mihosoft.vrl.workflow.FlowController;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.scene.Parent;
//import javafx.scene.shape.Circle;
//import javafx.scene.shape.Shape;
//
///**
// *
// * @author Michael Hoffer <info@michaelhoffer.de>
// */
//public class FXConnectorSkin implements ConnectorSkin<Connector>, FXSkin<Connector, ConnectorNode> {
//
//    private ConnectorNode circle = new ConnectorNode(20);
//    private Parent parent;
//    private ObjectProperty<Connector> modelProperty = new SimpleObjectProperty<>();
//    private FlowController controller;
//
//    public FXConnectorSkin(Parent parent, Connector c, FlowController controller) {
//        this.parent = parent;
//        this.controller = controller;
//        this.setModel(c);
//
//        circle.setStyle("-fx-background-color: rgba(120,140,255,0.2);-fx-border-color: rgba(120,140,255,0.42);-fx-border-width: 2;");
//    }
//
//    @Override
//    public void add() {
//        NodeUtil.addToParent(parent, circle);
//    }
//
//    @Override
//    public void remove() {
//        NodeUtil.removeFromParent(circle);
//    }
//
//    @Override
//    public final void setModel(Connector model) {
//        this.modelProperty.set(model);
//    }
//
//    @Override
//    public final Connector getModel() {
//        return this.modelProperty.get();
//    }
//
//    @Override
//    public final ObjectProperty<Connector> modelProperty() {
//        return modelProperty;
//    }
//
//    @Override
//    public FlowController getController() {
//        return controller;
//    }
//
//    @Override
//    public void setController(FlowController flow) {
//        this.controller = flow;
//    }
//
//    @Override
//    public ConnectorNode getNode() {
//        return circle;
//    }
//
//    @Override
//    public Parent getContentNode() {
//        return getParent();
//    }
//
//    /**
//     * @return the parent
//     */
//    public Parent getParent() {
//        return parent;
//    }
//
//    /**
//     * @param parent the parent to set
//     */
//    public void setParent(Parent parent) {
//        this.parent = parent;
//    }
//}
