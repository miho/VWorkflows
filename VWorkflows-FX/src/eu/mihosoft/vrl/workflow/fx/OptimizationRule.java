/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import javafx.scene.transform.Transform;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public interface OptimizationRule {
    public boolean visible(OptimizableContentPane p, Transform t);
    public boolean attached(OptimizableContentPane p, Transform t);
}
