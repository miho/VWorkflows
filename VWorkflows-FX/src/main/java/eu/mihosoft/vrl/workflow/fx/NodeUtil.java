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
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.MouseButton;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class that provides methods to simplify node handling. Possible use
 * cases are searching for nodes at specific locations, adding/removing nodes
 * to/from parents (Parent interface does not give write access to children).
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class NodeUtil {

    // no instanciation allowed
    private NodeUtil() {
        throw new AssertionError(); // not in this class either!
    }

    /**
     *
     * @param node
     * @return The X screen coordinate of the node.
     */
    static public double screenX(Node node) {
        return node.localToScene(node.getBoundsInLocal()).getMinX() + node.getScene().getX() + node.getScene().getWindow().getX();

    }

    /**
     *
     * @param node
     * @return The Y screen coordinate of the node.
     */
    static public double screenY(Node node) {
        return node.localToScene(node.getBoundsInLocal()).getMinY() + node.getScene().getY() + node.getScene().getWindow().getY();
    }

    static public Point2D transformCoordinates(double x, double y, Node from, Node to) {

        if (from == to || from == null || to == null) {
            return new Point2D(x, y);
        }

        // from -> scene
        Point2D fromInSceneCoordinates = new Point2D(
                (x) * from.localToSceneTransformProperty().get().getMxx(),
                (y) * from.localToSceneTransformProperty().get().getMyy());

        // scene -> to
        return new Point2D(
                (fromInSceneCoordinates.getX() + from.localToSceneTransformProperty().get().getTx())
                / to.getLocalToSceneTransform().getMxx(),
                (fromInSceneCoordinates.getY() + from.localToSceneTransformProperty().get().getTy())
                / to.getLocalToSceneTransform().getMyy());

    }

    /**
     * Returns all common ancestors of the specified nodes.
     *
     * @param n1 first scene graph node
     * @param n2 second scene graph node
     * @return a list that contains all common ancestors of the specified nodes
     */
    static public List<Parent> getCommonAncestors(Node n1, Node n2) {
        Parent p = null;

        List<Parent> n1Parents = getAncestors(n1);
        List<Parent> n2Parents = getAncestors(n2);

        n1Parents.retainAll(n2Parents);

        return n1Parents;
    }

    /**
     * Returns all ancestors of the specified node.
     *
     * @param n scene graph node
     * @return a list that contains all ancestors of the specified node
     */
    public static List<Parent> getAncestors(Node n) {

        return getAncestors(n, null);
    }

    /**
     * Returns the stylesheets of the node and all ancestors of the specified
     * node.
     *
     * @param n scene graph node
     * @return a list that contains the stylesheets of the node and all
     * ancestors of the specified node
     */
    public static List<String> getStylesheetsOfAncestors(Node n) {

//        System.out.println(">> searching stylesheets of ancestors of: " + n);
        List<String> result = new ArrayList<>();

        if (n instanceof Parent) {

//            for (String css : ((Parent) n).getStylesheets()) {
//                System.out.println(" --> n-css: " + css);
//            }
            result.addAll(((Parent) n).getStylesheets());
        }

        Scene scene = null;

        for (Parent p : getAncestors(n)) {

//            System.out.println(" --> parent: " + p);
//
//            for (String css : p.getStylesheets()) {
//                System.out.println("  --> p-css: " + p + ": " + css);
//            }
            result.addAll(p.getStylesheets());

            if (scene == null) {
                scene = p.getScene();
            }
        }

        if (scene != null) {

//            System.out.println(" --> scene: " + scene);
            result.addAll(scene.getStylesheets());

//            for (String css : scene.getStylesheets()) {
//                System.out.println("  --> scene-css: " + css);
//            }
//
//            System.out.println(" --> done");
        }

        return result;
    }

    /**
     * Returns all ancestors of the specified node till the specified one is
     * reached.
     *
     * @param n scene graph node
     * @param parent scene graph parent
     * @return a list that contains all ancestors of the specified node
     */
    public static List<Parent> getAncestors(Node n, Parent parent) {
        List<Parent> nParents = new ArrayList<>();

        Parent p = n.getParent();

        while (p != null && p != parent) {
            nParents.add(p);
            p = p.getParent();
        }

        return nParents;
    }

    /**
     * Removes the specified node from its parent.
     *
     * @param n the node to remove
     *
     * @throws IllegalArgumentException if an unsupported parent class has been
     * specified or the parent is <code>null</code>
     */
    public static void removeFromParent(Node n) {
        if (n.getParent() instanceof Group) {
            ((Group) n.getParent()).getChildren().remove(n);
        } else if (n.getParent() instanceof Pane) {
            ((Pane) n.getParent()).getChildren().remove(n);
        } else {
            throw new IllegalArgumentException("Unsupported parent: " + n.getParent());
        }
    }

    /**
     * Adds the given node to the specified parent.
     *
     * @param p parent
     * @param n node
     *
     * @throws IllegalArgumentException if an unsupported parent class has been
     * specified or the parent is <code>null</code>
     */
    public static void addToParent(Parent p, Node n) {
        if (p instanceof Group) {
            ((Group) p).getChildren().add(n);
        } else if (p instanceof Pane) {
            ((Pane) p).getChildren().add(n);
        } else if (p instanceof ScalableContentPane) {
            ((Pane)((ScalableContentPane) p).getContent()).getChildren().add(n);
        } else {
            throw new IllegalArgumentException("Unsupported parent: " + p);
        }
    }

    /**
     * Adds the given node to the specified parent.
     *
     * @param p parent
     * @param n node
     * @param index child index (z buffer)
     * @throws IllegalArgumentException if an unsupported parent class has been
     * specified or the parent is <code>null</code>
     */
    public static void addToParent(Parent p, Node n, int index) {
        if (p instanceof Group) {
            ((Group) p).getChildren().add(index, n);
        } else if (p instanceof Pane) {
            ((Pane) p).getChildren().add(index, n);
        } else {
            throw new IllegalArgumentException("Unsupported parent: " + p);
        }
    }

    /**
     * Returns the first node at the given location that is an instance of the
     * specified class object. The search is performed recursively until either
     * a node has been found or a leaf node is reached.
     *
     * @param p parent node
     * @param sceneX x coordinate
     * @param sceneY y coordinate
     * @param nodeClass node class to search for
     * @return a node that contains the specified screen coordinates and is an
     * instance of the specified class or <code>null</code> if no such node
     * exist
     */
    public static Node getNode(Parent p, double sceneX, double sceneY, Class<?> nodeClass) {

        // dammit! javafx uses "wrong" children order.
        List<Node> rightOrder = new ArrayList<>();
        rightOrder.addAll(p.getChildrenUnmodifiable());
        Collections.reverse(rightOrder);

        for (Node n : rightOrder) {
            boolean contains = n.contains(n.sceneToLocal(sceneX, sceneY));

            if (contains) {

                if (nodeClass.isAssignableFrom(n.getClass())) {
                    return n;
                }

                if (n instanceof Parent) {
                    return getNode((Parent) n, sceneX, sceneY, nodeClass);
                }
            }
        }

        return null;
    }

    /**
     * Returns the deepest node at the given location that is an instance of the
     * specified class object. The search is performed recursively until either
     * a node has been found or a leaf node is reached.
     *
     * @param p parent node
     * @param sceneX x coordinate
     * @param sceneY y coordinate
     * @param nodeClasses node classes to search for
     * @return a node that contains the specified screen coordinates and is an
     * instance of the specified class or <code>null</code> if no such node
     * exist
     */
    /*
    public static Node getDeepestNode(Parent p, double sceneX, double sceneY, Class<?>... nodeClasses) {

        // dammit! javafx uses "wrong" children order.
        List<Node> rightOrder = new ArrayList<>();
        rightOrder.addAll(p.getChildrenUnmodifiable());
        Collections.reverse(rightOrder);

        for (Node n : rightOrder) {
            boolean contains = n.contains(n.sceneToLocal(sceneX, sceneY));

            if (contains) {

                Node result = null;

                if (n instanceof Parent) {
                    result = getDeepestNode((Parent) n, sceneX, sceneY, nodeClasses);
                }

                if (result == null) {
                    result = n;
                }

                for (Class<?> nodeClass : nodeClasses) {

                    if (nodeClass.isAssignableFrom(result.getClass())) {

                        return result;

                    }
                }
            }
        }

        return null;
    }
    */

    /**
     * Returns the first node at the given location that is an instance of the
     * specified class object. The search is performed recursively until either
     * a node has been found or a leaf node is reached.
     *
     * @param p parent node
     * @param sceneX x coordinate
     * @param sceneY y coordinate
     * @param nodeClasses node classes to search for
     * @return a node that contains the specified screen coordinates and is an
     * instance of the specified class or <code>null</code> if no such node
     * exist
     */
    public static Node getNode(Parent p, double sceneX, double sceneY, Class<?>... nodeClasses) {

        // dammit! javafx uses "wrong" children order.
        List<Node> rightOrder = new ArrayList<>();
        rightOrder.addAll(p.getChildrenUnmodifiable());
        Collections.reverse(rightOrder);

        for (Node n : rightOrder) {
            boolean contains = n.contains(n.sceneToLocal(sceneX, sceneY));

            if (contains) {

                Node result = null;

                if (n instanceof Parent) {
                    result = getNode((Parent) n, sceneX, sceneY, nodeClasses);
                }

                if (result == null) {
                    result = n;
                }

                for (Class<?> nodeClass : nodeClasses) {
                    // may not implement the interface. We check if the Node has a property marking the link
                    // a custom node may have established a link to the ConnectorShape type but the Node itself
                    Class<?> connectorShapeClass = (Class<?>) n.getProperties().get(ConnectorShape.CONNECTOR_SHAPE_CLASS);
                    if (nodeClass.isAssignableFrom(result.getClass())) {
                        return result;
                    } else if (connectorShapeClass != null && connectorShapeClass.isAssignableFrom(result.getClass())) {
                        return result;
                    }
                }
            }
        }

        return null;
    }

    public static List<Node> nodesWithParent(Parent p, List<Node> nodes) {
        List<Node> result = new ArrayList<>();

        for (Node n : nodes) {
            if (p.equals(n.getParent())) {
                result.add(n);
            }
        }

        return result;
    }

    public static List<Node> nodesThatImplement(List<Node> nodes, Class<?> cls) {
        List<Node> result = new ArrayList<>();

        for (Node n : nodes) {
            if (cls.isAssignableFrom(n.getClass())) {
                result.add(n);
            }
        }

        return result;
    }

    public static MouseButton mouseBtnFromEvent(MouseEvent t) {
        MouseButton btn = MouseButton.NONE;

        switch (t.getButton()) {
            case PRIMARY:
                btn = MouseButton.PRIMARY;
                break;
            case MIDDLE:
                btn = MouseButton.MIDDLE;
                break;
            case SECONDARY:
                btn = MouseButton.SECONDARY;
                break;
        }

        return btn;
    }
}
