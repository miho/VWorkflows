/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Model;
import eu.mihosoft.vrl.workflow.Skin;
import javafx.scene.Node;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface FXSkin<T extends Model, V extends Node> extends Skin<T>{
    public V getNode();
}
