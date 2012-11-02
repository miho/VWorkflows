/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import eu.mihosoft.vrl.fxwindows.Window;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Playground {

    public void play() {

        Flow<Window> connections1 = new ControlFlow<>();

        Window w1 = new Window();

        FlowNode<Window> n1 = connections1.newNode(w1);

        Window w2 = new Window();

        FlowNode<Window> n2 = connections1.newNode(w2);

        connections1.connect(n1, n2);

        Flow<Window> connections2 = new DataFlow<>();
        
        

    }
}
