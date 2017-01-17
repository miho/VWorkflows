/*
 * Copyright 2012-2016 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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
// * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
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
