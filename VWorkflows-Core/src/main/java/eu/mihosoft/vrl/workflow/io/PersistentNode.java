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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public class PersistentNode {

    private double x;
    private double y;
    private double width;
    private double height;
    private String title;
    private PersistentValueObject valueObject;
    private VisualizationRequest vReq;
    private String id;
    private List<PersistentConnector> connectors;
    private Map<String,String> mainInputs = new HashMap<>();
    private Map<String,String> mainOutputs = new HashMap<>();

    public PersistentNode() {
    }

    public PersistentNode(String id, String title,
            double x, double y, double width, double height,
            ValueObject valueObject, VisualizationRequest vReq,
            List<PersistentConnector> connectors) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.title = title;
        this.valueObject = WorkflowIO.toPersistentValueObject(valueObject);
        this.vReq = vReq;
        this.id = id;
        this.connectors = WorkflowIO.listToSerializableList(connectors);
    }
    
    

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the valueObject
     */
    public PersistentValueObject getValueObject() {
        return valueObject;
    }

    /**
     * @param valueObject the valueObject to set
     */
    public void setValueObject(PersistentValueObject valueObject) {
        this.valueObject = valueObject;
    }

    /**
     * @return the vReq
     */
    public VisualizationRequest getVReq() {
        return vReq;
    }

    /**
     * @param vReq the vReq to set
     */
    public void setVReq(VisualizationRequest vReq) {
        this.vReq = vReq;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the inputTypes
     */
    public List<PersistentConnector> getConnectors() {
        return connectors;
    }

    /**
     * @param connectors the inputTypes to set
     */
    public void setConnectors(List<PersistentConnector> connectors) {
        
        for (PersistentConnector persistentConnector : connectors) {
            addConnector(persistentConnector);
        }
    }
    
        /**
     * @param connector the inputTypes to set
     */
    public void addConnector(PersistentConnector connector ) {

//            connector.setNode(this);
            
            connectors.add(connector);
    }

    /**
     * @return the mainInputs
     */
    public Map<String,String> getMainInputs() {
        return mainInputs;
    }

    /**
     * @param mainInputs the mainInputs to set
     */
    public void setMainInputs(Map<String,String> mainInputs) {
        this.mainInputs = mainInputs;
    }

    /**
     * @return the mainOutputs
     */
    public Map<String,String> getMainOutputs() {
        return mainOutputs;
    }

    /**
     * @param mainOutputs the mainOutputs to set
     */
    public void setMainOutputs(Map<String,String> mainOutputs) {
        this.mainOutputs = mainOutputs;
    }
}
