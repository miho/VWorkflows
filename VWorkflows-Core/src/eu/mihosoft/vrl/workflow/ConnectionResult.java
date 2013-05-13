/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface ConnectionResult {

    /**
     * @return the connection
     */
    Connection getConnection();

    /**
     * @return the status
     */
    CompatibilityResult getStatus();
    
}
