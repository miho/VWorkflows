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
    
    public void setUp(VFlow pworkflow);
    
    public void generateLayout() ;
    
}
