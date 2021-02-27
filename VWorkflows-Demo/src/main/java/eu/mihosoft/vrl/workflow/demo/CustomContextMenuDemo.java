/*
 * Copyright 2012-2021 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.demo;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.DefaultFXConnectionSkin;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import eu.mihosoft.vrl.workflow.fx.VCanvas;
import eu.mihosoft.vrl.workflow.skin.ConnectionSkin;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author Andres Almiray
 */
public class CustomContextMenuDemo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane contentPane = new StackPane();
        VCanvas canvas = new VCanvas();
        contentPane.getChildren().add(canvas);
        createFlow((Pane)canvas.getContent());

        Scene scene = new Scene(contentPane, 1024, 768);

        primaryStage.setTitle("Custom ContextMenu Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void createFlow(Pane pane) {
        VFlow flow = FlowFactory.newFlow();

        VNode n1 = flow.newNode();
        n1.addOutput("data");
        VNode n2 = flow.newNode();
        n2.addInput("data");

        flow.setVisible(true);
        flow.setSkinFactories(new CustomFXSkiFactory(pane));
        // flow.setSkinFactories(new FXValueSkinFactory(canvas));
    }

    private static class CustomFXSkiFactory extends FXSkinFactory {
        public CustomFXSkiFactory(Parent parent) {
            super(parent);
        }

        protected CustomFXSkiFactory(Parent parent, FXSkinFactory parentFactory) {
            super(parent, parentFactory);
        }

        @Override
        public ConnectionSkin createSkin(Connection c, VFlow flow, String type) {
            return new CustomFXConnectionSkin(this, getFxParent(), c, flow, type).init();
        }
    }

    private static class CustomFXConnectionSkin extends DefaultFXConnectionSkin {
        public CustomFXConnectionSkin(FXSkinFactory skinFactory, Parent parent, Connection connection, VFlow flow, String type) {
            super(skinFactory, parent, connection, flow, type);
        }

        @Override
        protected ContextMenu createContextMenu() {
            ContextMenu contextMenu = super.createContextMenu();
            contextMenu.getItems().addAll(createMenuItem("Foo"));
            contextMenu.getItems().addAll(createMenuItem("Bar"));
            return contextMenu;
        }

        private MenuItem createMenuItem(String title) {
            MenuItem item = new MenuItem(title);
            item.setOnAction(event -> System.out.println(title));
            return item;
        }

        @Override
        protected EventHandler<MouseEvent> createContextMenuHandler(ContextMenu contextMenu) {
            return new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    contextMenu.show(getConnectionPath(), event.getScreenX(), event.getScreenY());
                }
            };
        }
    }
}
