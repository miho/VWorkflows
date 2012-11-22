/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.SkinBase;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class DefaultConnectionPaneSkin extends SkinBase<ConnectorPane, BehaviorBase<ConnectorPane>> {

//    private Pane root = new StackPane();
    public DefaultConnectionPaneSkin(final ConnectorPane c) {
        super(c, new BehaviorBase<ConnectorPane>(c));

        setCursor(Cursor.DEFAULT);
    }
}
