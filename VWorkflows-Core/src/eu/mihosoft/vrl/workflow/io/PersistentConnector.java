/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    private ValueObject valueObject;

    public PersistentConnector(String type, String localId, boolean input, boolean output) {
        this.type = type;
        this.localId = localId;
//        this.node = node;
        this.input = input;
        this.output = output;
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
}