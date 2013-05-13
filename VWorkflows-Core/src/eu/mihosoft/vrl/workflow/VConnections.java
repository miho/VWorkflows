/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VConnections {
    
    public static Connections newConnections(String type) {
        return new ConnectionsImpl(type);
    }
    
    public static void printConnections(Connections connections) {
        for (Connection c : connections.getConnections()) {
            System.out.println(c.toString());
        }
    }
}
