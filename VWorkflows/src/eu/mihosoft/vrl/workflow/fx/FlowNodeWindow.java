/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.FlowFlowNode;
import eu.mihosoft.vrl.workflow.FlowNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import jfxtras.labs.scene.control.window.CloseIcon;
import jfxtras.labs.scene.control.window.MinimizeIcon;
import jfxtras.labs.scene.control.window.Window;
import jfxtras.labs.scene.control.window.WindowIcon;
import jfxtras.labs.util.event.MouseControlUtil;

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


        OptimizableContentPane parentContent = new OptimizableContentPane();

        content = new ScalableContentPane();

        parentContent.getChildren().add(content);

        Pane root = new Pane();
        content.setContentPane(root);

        super.setContentPane(parentContent);
        addCollapseIcon(skin);
        
//        addSelectionRectangle(skin, root);
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

    private void addCollapseIcon(FXFlowNodeSkin skin) {

        if (skin == null) {
            return;
        }

        if (!(skin.getModel() instanceof FlowFlowNode)) {
            return;
        }

        final WindowIcon collapseIcon = new WindowIcon();

        collapseIcon.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                FXFlowNodeSkin skin = nodeSkinProperty.get();

                if (skin != null) {
                    FlowFlowNode model = (FlowFlowNode) skin.getModel();
                    model.setVisible(!model.isVisible());
                }
            }
        });

        getRightIcons().add(collapseIcon);

        if (skin.modelProperty() != null) {
            skin.modelProperty().addListener(new ChangeListener<FlowNode>() {
                @Override
                public void changed(ObservableValue<? extends FlowNode> ov,
                        FlowNode t, FlowNode t1) {
                    if (t1 instanceof FlowFlowNode) {
                        getRightIcons().add(collapseIcon);
                    } else {
                        getRightIcons().remove(collapseIcon);
                    }
                }
            });
        }

    }

    private void addSelectionRectangle(FXFlowNodeSkin skin, Pane root) {
        if (skin == null) {
            return;
        }
        if (!(skin.getModel() instanceof FlowFlowNode)) {
            return;
        }
        Rectangle rect = new Rectangle();
        rect.setStroke(new Color(1, 1, 1, 1));
        rect.setFill(new Color(0, 0, 0, 0.5));
        MouseControlUtil.
                addSelectionRectangleGesture(root, rect);
    }
}
