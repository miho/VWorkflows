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
package eu.mihosoft.vrl.workflow.impl;

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowModel;
import eu.mihosoft.vrl.workflow.NodeLookup;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;

import java.util.Objects;

/**
 * This class allows nodes to be looked up by id.
 * Ids have the format {@code <node id>:<connector id>}.
 * <p>
 * The class can also be used to look up connectors.
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public class DefaultNodeLookup implements NodeLookup {
    private final VFlowModel root;

    public DefaultNodeLookup(VFlowModel root) {
        this.root = root;
    }

    @Override
    public Connector getConnectorById(String globalId) {
        String[] ids = globalId.split(":c:");

        if (ids.length < 2) {
            throw new IllegalArgumentException("wrong connector id format: "
                + globalId + ", correct format: node-id:c:connector-id");
        }

        String nodeId = ids[0];

        VNode node = getById(nodeId);
        String connectorId = ids[1];

        if (node == null) {
            return null;
        }

        return node.getConnector(connectorId);
    }

    @Override
    public VNode getById(String globalId) {
        return getNodeByGlobalId(root, globalId);
    }

    protected VNode getNodeByGlobalId(VFlowModel parent, String id) {
        if (Objects.equals(parent.getId(), id)) {
            return parent;
        }

        for (VNode n : parent.getNodes()) {
            if (n.getId().equals(id)) {
                return n;
            }

            if (n instanceof FlowModel) {
                VNode result = getNodeByGlobalId((VFlowModel) n, id);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }
}
