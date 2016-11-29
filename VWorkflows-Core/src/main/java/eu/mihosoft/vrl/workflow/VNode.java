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
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.util.Collection;

/**
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public interface VNode extends Model, Selectable {

    StringProperty titleProperty();

    void setTitle(String title);

    String getTitle();

    StringProperty idProperty();

    /**
     * Defines the local id of this node.
     *
     * @param id id to set
     */
    void setId(String id);

    /**
     * Returns the local id of this node.
     *
     * @return
     */
    String getId();

    /**
     * Returns the global id of this node
     *
     * @return global id of this node
     */
    //    String getGlobalId();
    DoubleProperty xProperty();

    DoubleProperty yProperty();

    void setX(double x);

    void setY(double x);

    double getX();

    double getY();

    DoubleProperty widthProperty();

    DoubleProperty heightProperty();

    void setWidth(double w);

    void setHeight(double h);

    double getWidth();

    double getHeight();

    //    ObservableList<VNode> getChildren();
    //    ObservableList<Connector<FlowNode>> getInputs();
    //    ObservableList<Connector<FlowNode>> getOutputs();
    void setValueObject(ValueObject obj);

    ValueObject getValueObject();

    ObjectProperty<ValueObject> valueObjectProperty();

    VFlowModel getFlow();

    //    boolean isInputOfType(String type);
    //
    //    boolean isOutputOfType(String type);
    //    boolean isInput();
    //
    //    boolean isOutput();
    //    void setInput(boolean state, String type);
    //    void setOutput(boolean state, String type);
    Connector addInput(String type);

    Connector addOutput(String type);

    Connector addConnector(Connector c);

    boolean removeConnector(Connector c);

    //    ObservableList<String> getInputTypes();
    //
    //    ObservableList<String> getOutputTypes();
    Collection<String> getMainInputTypes();

    Collection<String> getMainOutputTypes();

    Connector getMainInput(String type);

    Connector getMainOutput(String type);

    Connector setMainInput(Connector connector);

    Connector setMainOutput(Connector connector);

    Connector getConnector(String localId);

    ObservableList<Connector> getConnectors();

    ObservableList<Connector> getInputs();

    ObservableList<Connector> getOutputs();

    BooleanProperty selectableProperty();

    boolean isSelectable();


    /**
     * Returns the distance to the root element.
     */
    int getDepth();

    /**
     * Returns the root element of this flow.
     *
     * @return root element
     */
    FlowModel getRoot();

}
