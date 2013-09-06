/*
 * IntegerFlowNodeSkin.java
 * 
 * Copyright 2012-2013 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 * 
 * Please cite the following publication(s):
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
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
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
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
