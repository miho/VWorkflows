/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VFXConnections {
    
    public static Connections newConnections() {
        return new ConnectionsImpl();
    }
    
    public static void printConnections(Connections connections) {
        for (Connection c : connections.getConnections()) {
            System.out.println(c.toString());
        }
    }
}
