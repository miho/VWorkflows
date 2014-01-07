/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.test;

import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXValueSkinFactory;
import eu.mihosoft.vrl.workflow.fx.VCanvas;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import org.loadui.testfx.Assertions;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.categories.TestFX;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@Category( TestFX.class )
public class CanvasTest extends GuiTest {

    private Connector n1OutputControl;
    private Connector n2InputControl;
    private Connector n2InputData;
    private VFlow flow;
    private VCanvas canvas;

    public CanvasTest() {

        

        
    }

    @Override
    protected Parent getRootNode() {
        System.out.println("test2");
        
        // setup model
        flow = FlowFactory.newFlow();
        flow.setVisible(true);

        VNode n1 = flow.newNode();
        n1OutputControl = n1.addOutput("control");
        VNode n2 = flow.newNode();
        n2InputControl = n2.addInput("control");
        n2InputData = n2.addInput("data");
        
        
        // setup fx
        canvas = new VCanvas();
        canvas.setPrefSize(1024, 768);
        FXValueSkinFactory skinFactory = new FXValueSkinFactory(canvas);
        flow.setSkinFactories(skinFactory);
        System.out.println("test1");
        
        return canvas;
    }

    @Test
    public void dragConnectionTest() {
        System.out.println("test3");
        drag("#" + n1OutputControl.getId(), MouseButton.PRIMARY).
                to("#" + n2InputControl.getId());

        assertTrue("Flow must contain exactly one connection", flow.getAllConnections().size() == 1);
    }

}
