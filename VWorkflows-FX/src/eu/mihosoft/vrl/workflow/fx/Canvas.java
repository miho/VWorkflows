/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Canvas extends ScalableContentPane {

    public Canvas() {
        setContentPane(new InnerCanvas());
        getStyleClass().add("vflow-background");
    }
}
