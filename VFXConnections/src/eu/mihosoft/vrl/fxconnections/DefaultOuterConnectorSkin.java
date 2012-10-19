/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.SkinBase;
import javafx.scene.Cursor;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class DefaultOuterConnectorSkin extends SkinBase<OuterConnector, BehaviorBase<OuterConnector>> {

//    private Pane root = new StackPane();
    public DefaultOuterConnectorSkin(final OuterConnector c) {
        super(c, new BehaviorBase<>(c));

        setCursor(Cursor.DEFAULT);
    }
}
