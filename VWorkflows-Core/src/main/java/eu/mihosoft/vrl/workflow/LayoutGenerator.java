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
    
    public VFlow getWorkflow();
    public boolean getRecursive();
    public boolean getAutoscaleNodes();
    public boolean getDebug();
    public void setWorkflow(VFlow pworkflow);
    public void setRecursive(boolean precursive);
    public void setAutoscaleNodes(boolean pautoscaleNodes);
    public void setDebug(boolean pdebug);
    
    /**
     * Generates a Layout for the workflow given at SetUp.
     */
    public void generateLayout() ;
    
}
