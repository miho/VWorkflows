/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.demo5;

import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import eu.mihosoft.vrl.workflow.fx.FXFlowNodeSkinBase;
import eu.mihosoft.vrl.workflow.fx.ScalableContentPane;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import jfxtras.labs.scene.control.gauge.Gauge;
import jfxtras.labs.scene.control.gauge.Lcd;
import jfxtras.labs.scene.control.gauge.LcdBuilder;
import jfxtras.labs.scene.control.gauge.LcdDesign;
import jfxtras.labs.scene.control.gauge.StyleModel;
import jfxtras.labs.scene.control.gauge.StyleModelBuilder;

/**
 * Custom flownode skin. In addition to the basic node visualization from
 * VWorkflows this skin adds custom visualization of value objects.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class IntegerFlowNodeSkin extends FXFlowNodeSkinBase {

    public IntegerFlowNodeSkin(FXSkinFactory skinFactory,
            VNode model, VFlow controller) {
        super(skinFactory, model, controller);
    }

    @Override
    public void updateView() {

        // we don't create custom view for flows
        if (getModel() instanceof VFlowModel) {
            return;
        }

        // we don't create a custom view if no value has been defined
        if (getModel().getValueObject().getValue() == null) {
            return;
        }
        
        ScalableContentPane scalableContentPane = new ScalableContentPane();
        
        GridPane nodePane = new GridPane();
        nodePane.setAlignment(Pos.CENTER);
        scalableContentPane.setContentPane(nodePane);

        StyleModel style =
                StyleModelBuilder.create()
                .lcdDesign(LcdDesign.GREEN_BLACK)
                .lcdValueFont(Gauge.LcdFont.LCD)
                .lcdUnitStringVisible(true)
                .lcdThresholdVisible(true)
                .build();
        
        Lcd lcd1 = LcdBuilder.create()
                         .styleModel(style)
                         .threshold(40)
                         .bargraphVisible(true)
                         .minMeasuredValueVisible(true)
                         .minMeasuredValueDecimals(3)
                         .maxMeasuredValueVisible(true)
                         .maxMeasuredValueDecimals(3)
                         .formerValueVisible(true)
                         .title("VWorkflows")
                         .unit("°C")
                         .value((Integer)getModel().getValueObject().getValue())
                         .build();
        
        lcd1.setPrefSize(250, 70);
        
        scalableContentPane.getContentPane().getChildren().add(lcd1);
        getNode().setContentPane(scalableContentPane);
    }
}
