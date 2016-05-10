/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 * Interface for layout generator classes.
 * @author Tobias Mertz
 */
public interface LayoutGenerator {
    
    public boolean getDebug();
    public VFlow getWorkflow();
    //public VNode[] getModelNodes();
    public void setDebug(boolean pdebug);
    public void setWorkflow(VFlow pworkflow);
    //public void setModelNodes(VNode[] pnodes);
    
    /**
     * Generates a Layout for the workflow given at SetUp.
     */
    public void generateLayout() ;
    
}
