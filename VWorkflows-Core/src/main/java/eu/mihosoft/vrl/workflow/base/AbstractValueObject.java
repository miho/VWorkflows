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
package eu.mihosoft.vrl.workflow.base;

import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.ValueObject;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import eu.mihosoft.vrl.workflow.impl.DefaultVisualizationRequest;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * This class defines a value object you can assign a value.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public abstract class AbstractValueObject implements ValueObject {
    protected VNode parent;
    protected final ObjectProperty value = new SimpleObjectProperty(this, "value");
    protected VisualizationRequest visualizationRequest;

    public AbstractValueObject() {
    }

    public AbstractValueObject(VNode parent) {
        this.parent = parent;
    }

    @Override
    public VNode getParent() {
        return parent;
    }

    @Override
    public Object getValue() {
        return valueProperty().get();
    }

    @Override
    public void setValue(Object o) {
        this.valueProperty().set(o);
    }

    @Override
    public ObjectProperty<Object> valueProperty() {
        return value;
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        if (visualizationRequest == null) {
            visualizationRequest = new DefaultVisualizationRequest();
        }

        return visualizationRequest;
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest visualizationRequest) {
        this.visualizationRequest = visualizationRequest;
    }

    /**
     * @param parent the parent to set
     */
    @Override
    public void setParent(VNode parent) {
        this.parent = parent;
    }
}
