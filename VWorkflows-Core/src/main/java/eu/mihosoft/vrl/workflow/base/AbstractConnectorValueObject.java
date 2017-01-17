/*
 * Copyright 2012-2017 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.ConnectorValueObject;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * This class defines a default connector value object. ValueObjects are used to
 * store data in connectors. It is a placeholder for a value object as it's
 * get/setValue methods are not implemented.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public abstract class AbstractConnectorValueObject implements ConnectorValueObject {
    protected final ObjectProperty<Object> value = new SimpleObjectProperty<>(this, "value");
    protected final Connector connector;

    protected VNode parent;
    protected VisualizationRequest visualizationRequest;

    public AbstractConnectorValueObject(Connector connector) {
        this.connector = connector;
        this.parent = connector.getNode();
    }

    @Override
    public VNode getParent() {
        return parent;
    }

    @Override
    public Connector getConnector() {
        return connector;
    }

    @Override
    public Object getValue() {
        return value.get();
    }

    @Override
    public void setValue(Object value) {
        this.value.set(value);
    }

    @Override
    public ObjectProperty<Object> valueProperty() {
        return this.value;
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        return visualizationRequest;
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        this.visualizationRequest = vReq;
    }

    @Override
    public void setParent(VNode parent) {
        this.parent = parent;
    }
}
