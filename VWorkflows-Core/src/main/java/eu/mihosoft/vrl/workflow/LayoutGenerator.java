/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Tobias Mertz
 */
public interface LayoutGenerator {
    
    /**
     * Sets up the node- and edge-model for the current workflow.
     * @param pworkflow VWorfklow: current workflow to be layouted.
     */
    public void setUp(VFlow pworkflow);
    
    /**
     * Generates a Layout for the workflow given at SetUp.
     */
    public void generateLayout() ;
    
}
