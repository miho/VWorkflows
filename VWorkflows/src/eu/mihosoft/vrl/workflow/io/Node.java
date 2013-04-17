/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.io;

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.NodeValueObject;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import java.util.List;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Node {

    private double x;
    private double y;
    private double width;
    private double height;
    private String title;
    private NodeValueObject valueObject;
    private VisualizationRequest vReq;
    private String id;
    private List<Connector> inputs;
    private List<Connector> outputs;

    public Node() {
    }

    public Node(String id, String title,
            double x, double y, double width, double height,
            NodeValueObject valueObject, VisualizationRequest vReq,
            List<Connector> inputs, List<Connector> outputs) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.title = title;
        this.valueObject = valueObject;
        this.vReq = vReq;
        this.id = id;
        
        this.inputs = inputs;
        this.outputs = outputs;
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
    public NodeValueObject getValueObject() {
        return valueObject;
    }

    /**
     * @param valueObject the valueObject to set
     */
    public void setValueObject(NodeValueObject valueObject) {
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
     * @return the inputs
     */
    public List<Connector> getInputs() {
        return inputs;
    }

    /**
     * @param inputs the inputs to set
     */
    public void setInputs(List<Connector> inputs) {
        this.inputs = inputs;
    }

    /**
     * @return the outputs
     */
    public List<Connector> getOutputs() {
        return outputs;
    }

    /**
     * @param outputs the outputs to set
     */
    public void setOutputs(List<Connector> outputs) {
        this.outputs = outputs;
    }
}
