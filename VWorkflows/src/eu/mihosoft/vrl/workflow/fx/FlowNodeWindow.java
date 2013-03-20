/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import jfxtras.labs.scene.control.window.CloseIcon;
import jfxtras.labs.scene.control.window.MinimizeIcon;
import jfxtras.labs.scene.control.window.Window;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FlowNodeWindow extends Window {

    private ObjectProperty<FXFlowNodeSkin> nodeSkinProperty = new SimpleObjectProperty<>();
    private ScalableContentPane content;

    public FlowNodeWindow(FXFlowNodeSkin skin) {

        nodeSkinProperty().set(skin);

        getLeftIcons().add(new CloseIcon(this));
        getLeftIcons().add(new MinimizeIcon(this));

        setStyle("-fx-background-color: rgba(120,140,255,0.2);-fx-border-color: rgba(120,140,255,0.42);-fx-border-width: 2;");

        content = new ScalableContentPane();

        Pane root = new Pane();
        content.setContentPane(root);

        super.setContentPane(content);
    }

    public Pane getWorkflowContentPane() {
        return content.getContentPane();
    }

    /**
     * @return the nodeSkinProperty
     */
    public final ObjectProperty<FXFlowNodeSkin> nodeSkinProperty() {
        return nodeSkinProperty;
    }
}
