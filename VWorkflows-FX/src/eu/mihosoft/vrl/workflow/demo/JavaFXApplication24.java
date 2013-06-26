/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class JavaFXApplication24 extends Application {
    
    private double value;

    @Override
    public void start(Stage primaryStage) {
//        Button btn = new Button();
//        btn.setText("Say 'Hello World'");
//        btn.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                System.out.println("Hello World!");
//            }
//        });

        StackPane root = new StackPane();
//        root.getChildren().add(btn);


        final SwingNode node = new SwingNode();

        node.setVisible(true);

        final JPanel panel = new JPanel() {
            
            @Override
            public void paintComponent(Graphics g) {
                g.setColor(Color.red);
                g.drawLine(0, 0, (int)(value/2.0)/*(int)g.getClipBounds().getWidth()*/,
                        50);
            }
        };

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                node.setContent(panel);
                
                Timer t = new Timer();
                
                t.scheduleAtFixedRate(new TimerTask() {

                    @Override
                    public void run() {
                        value+=0.1;
                        panel.repaint();
                    }
                }, 0, 3);
            }
        });

        root.getChildren().add(node);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
