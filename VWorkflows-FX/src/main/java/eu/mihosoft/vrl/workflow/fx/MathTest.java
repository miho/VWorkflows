/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Segment;
import org.apache.commons.math3.geometry.euclidean.twod.SubLine;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class MathTest {
    public static void main(String[] args) {
        Vector2D p1 = new Vector2D(-1, 0);
        Vector2D p2 = new Vector2D(1, 0);
        
        Vector2D p3 = new Vector2D(0,-1);
        Vector2D p4 = new Vector2D(0,1);
        
        Line l1 = new Line(p1, p2, 1e-5);
        
        Line l2 = new Line(p3, p4, 1e-5);
        
        Segment s1 = new Segment(p2, p1, l1);
        Segment s2 = new Segment(p4, p3, l2);
        
        Vector2D lIntersection = l1.intersection(l2);
        Vector2D sIntersection = new SubLine(s1).intersection(new SubLine(s2), true);
        
        System.out.println("l-intersection: " + lIntersection);
        System.out.println("s-intersection: " + sIntersection);
        
    }
}
