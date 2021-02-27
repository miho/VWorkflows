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
package eu.mihosoft.vrl.workflow.io;

import eu.mihosoft.vrl.workflow.ValueObject;
import eu.mihosoft.vrl.workflow.VisualizationRequest;

import java.util.List;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public class PersistentFlow extends PersistentNode {

    private List<PersistentConnection> connections;
    private List<PersistentNode> nodes;
//    private List<String> connectionTypes;
    private PersistentFlow parent;
    private boolean visible;

    public PersistentFlow() {
    }

    public PersistentFlow(List<PersistentConnection> connections, List<PersistentNode> nodes) {
        this.connections = connections;
        this.nodes = nodes;
    }

    public PersistentFlow(PersistentFlow parent, String id,
            List<PersistentConnection> connections, List<PersistentNode> nodes, String title,
            double x, double y, double width, double height,
            ValueObject valueObject, boolean visible, VisualizationRequest vReq,
            List<PersistentConnector> connectors) {

        super(id, title, x, y, width, height, valueObject, vReq,
                connectors);
        this.connections = connections;
        this.nodes = nodes;
        this.parent = parent;
        this.visible = visible;
    }

    /**
     * @return the connections
     */
    public List<PersistentConnection> getConnections() {
        return connections;
    }

    /**
     * @param connections the connections to set
     */
    public void setConnections(List<PersistentConnection> connections) {
        this.connections = connections;
    }

    /**
     * @return the nodes
     */
    public List<PersistentNode> getNodes() {
        return nodes;
    }

    /**
     * @param nodes the nodes to set
     */
    public void setNodes(List<PersistentNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * @return the parent
     */
    public PersistentFlow getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(PersistentFlow parent) {
        this.parent = parent;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
