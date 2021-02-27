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
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.util.Collection;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public interface VNode extends Model, Selectable {

    public StringProperty titleProperty();

    public void setTitle(String title);

    public String getTitle();

    public StringProperty idProperty();

    /**
     * Defines the local id of this node.
     *
     * @param id id to set
     */
    public void setId(String id);

    /**
     * Returns the local id of this node.
     *
     * @return
     */
    public String getId();

    /**
     * Returns the global id of this node
     *
     * @return global id of this node
     */
//    public String getGlobalId();
    public DoubleProperty xProperty();

    public DoubleProperty yProperty();

    public void setX(double x);

    public void setY(double x);

    public double getX();

    public double getY();

    public DoubleProperty widthProperty();

    public DoubleProperty heightProperty();

    public void setWidth(double w);

    public void setHeight(double h);

    public double getWidth();

    public double getHeight();

//    public ObservableList<VNode> getChildren();
//    public ObservableList<Connector<FlowNode>> getInputs();
//    public ObservableList<Connector<FlowNode>> getOutputs();
    public void setValueObject(ValueObject obj);

    public ValueObject getValueObject();

    public ObjectProperty<ValueObject> valueObjectProperty();

    public VFlowModel getFlow();

//    boolean isInputOfType(String type);
//
//    boolean isOutputOfType(String type);
//    boolean isInput();
//
//    boolean isOutput();
//    void setInput(boolean state, String type);
//    void setOutput(boolean state, String type);
    public Connector addInput(String type);

    public Connector addOutput(String type);

    public Connector addConnector(Connector c);
    
    public boolean removeConnector(Connector c);

//    ObservableList<String> getInputTypes();
//
//    ObservableList<String> getOutputTypes();
    public Collection<String> getMainInputTypes();

    public Collection<String> getMainOutputTypes();

    public Connector getMainInput(String type);

    public Connector getMainOutput(String type);

    public Connector setMainInput(Connector connector);

    public Connector setMainOutput(Connector connector);

    public Connector getConnector(String localId);

    public ObservableList<Connector> getConnectors();

    public ObservableList<Connector> getInputs();

    public ObservableList<Connector> getOutputs();
    
    public BooleanProperty selectableProperty();
    public boolean isSelectable();
    
        
    /**
     * Returns the distance to the root element.
     */
    public int getDepth();
    
    /**
     * Returns the root element of this flow.
     * @return root element
     */
    public FlowModel getRoot();
    
}
