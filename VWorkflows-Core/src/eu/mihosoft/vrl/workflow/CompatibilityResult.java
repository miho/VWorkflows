/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public interface CompatibilityResult {
    boolean isCompatible();
    String getMessage();
    String getStatus();
}
