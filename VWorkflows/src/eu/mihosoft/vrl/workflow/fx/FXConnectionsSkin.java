///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package eu.mihosoft.vrl.workflow.fx;
//
//import eu.mihosoft.vrl.workflow.Connections;
//import eu.mihosoft.vrl.workflow.ConnectionsSkin;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.scene.Node;
//
///**
// *
// * @author Michael Hoffer <info@michaelhoffer.de>
// */
//public class FXConnectionsSkin implements ConnectionsSkin, FXSkin<Connections, Node> {
//
//    private Connections connections;
//    private ObjectProperty<Connections> modelProperty = new SimpleObjectProperty<>();
//
//    public FXConnectionsSkin(Connections connections) {
//        this.connections = connections;
//    }
//
//    @Override
//    public Node getNode() {
//        return null;
//    }
//
//    @Override
//    public void remove() {
////        VFXNodeUtils.removeFromParent(node);
//    }
//
//    @Override
//    public void setModel(Connections model) {
//        modelProperty.set(model);
//    }
//
//    @Override
//    public Connections getModel() {
//        return modelProperty.get();
//    }
//
//    @Override
//    public ObjectProperty<Connections> modelProperty() {
//        return modelProperty;
//    }
//
//    @Override
//    public void add() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//}
