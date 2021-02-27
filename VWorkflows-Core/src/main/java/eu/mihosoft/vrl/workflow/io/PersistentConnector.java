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

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public class PersistentConnector{

//    private PersistentNode node;
    private String type;
    private String localId;
    private VisualizationRequest vRequest;
    private boolean input;
    private boolean output;
    private boolean passthru;
    private ValueObject valueObject;
    private int maxNumConnections;

    public PersistentConnector(String type, String localId, boolean input, boolean output, boolean passthru) {
        this.type = type;
        this.localId = localId;
//        this.node = node;
        this.input = input;
        this.output = output;
        this.passthru = passthru;
    }

    public String getType() {
        return this.type;
    }

    public String getLocalId() {
        return this.localId;
    }

    public void setLocalId(String id) {
        this.localId = id;
    }

//    public PersistentNode getNode() {
//        return this.node;
//    }


    public VisualizationRequest getVisualizationRequest() {
        return this.vRequest;
    }


    public void setVisualizationRequest(VisualizationRequest vReq) {
        this.vRequest = vReq;
    }

    /**
     * @return the input
     */
    public boolean isInput() {
        return input;
    }

    /**
     * @return the output
     */
    public boolean isOutput() {
        return output;
    }

//    /**
//     * @param node the node to set
//     */
//    public void setNode(PersistentNode node) {
//        this.node = node;
//    }

    /**
     * @return the valueObject
     */
    public ValueObject getValueObject() {
        return valueObject;
    }

    /**
     * @param valueObject the valueObject to set
     */
    public void setValueObject(ValueObject valueObject) {
         this.valueObject = valueObject;
    }

    /**
     * @return the passthru
     */
    public boolean isPassthru() {
        return passthru;
    }

    /**
     * @param passthru the passthru to set
     */
    public void setPassthru(boolean passthru) {
        this.passthru = passthru;
    }

    /**
     * @return the maxNumConnections
     */
    public int getMaxNumConnections() {
        return maxNumConnections;
    }

    /**
     * @param maxNumConnections the maxNumConnections to set
     */
    public void setMaxNumConnections(int maxNumConnections) {
        this.maxNumConnections = maxNumConnections;
    }
}