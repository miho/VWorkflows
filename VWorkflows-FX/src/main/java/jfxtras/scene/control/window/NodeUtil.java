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
package jfxtras.scene.control.window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableNumberValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;


/**
 * Utility class that provides methods to simplify node handling. Possible use
 * cases are searching for nodes at specific locations, adding/removing nodes
 * to/from parents (Parent interface does not give write access to children).
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 * @author Tom Eugelink &lt;tbee@tbee.org&gt;
 */
public class NodeUtil {

    // no instantiation allowed
    private NodeUtil() {
        throw new AssertionError(); // not in this class either!
    }

    /**
     *
     * @param node
     * @return The X screen coordinate of the node.
     */
    static public double screenX(Node node) {
        return node.localToScene(node.getBoundsInLocal()).getMinX()
                + node.getScene().getX() + node.getScene().getWindow().getX();
    }

    /**
     *
     * @param node
     * @return The Y screen coordinate of the node.
     */
    static public double screenY(Node node) {
        return node.localToScene(node.getBoundsInLocal()).getMinY()
                + node.getScene().getY() + node.getScene().getWindow().getY();
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
            throw new IllegalArgumentException(
                    "Unsupported parent: " + n.getParent());
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
     * instance of the specified class or {@code null} if no such node exist
     */
    public static Node getNode(Parent p, double sceneX,
            double sceneY, Class<?> nodeClass) {

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
     * This method prevents blurry horizontal or vertical lines, use snapXY(x)
     * instead of x.
     *
     * @param position (x or y)
     * @return
     */
    public static double snapXY(double position) {
        return ((int) position) + .5;
    }

    /**
     * This is the snapXY method for using in a binding, for example: p1.bind(
     * snapXY( p2.multiply(0.1) ));
     *
     * @param position (x or y)
     * @return
     */
    public static DoubleBinding snapXY(final ObservableNumberValue position) {
        return new DoubleBinding() {
            {
                super.bind(position);
            }

            @Override
            public void dispose() {
                super.unbind(position);
            }

            @Override
            protected double computeValue() {
                return NodeUtil.snapXY(position.doubleValue());
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(position);
            }
        };
    }

    /**
     * This method prevents blurry horizontal or vertical lines, use snapWH(x,
     * w) instead of w.
     *
     * @param position (x or y)
     * @param offset (width or height)
     * @return
     */
    public static double snapWH(double position, double offset) {
        return snapXY(position + offset) - snapXY(position);
    }

    /**
     * This is the snapXY method for using in a binding, for example: p1.bind(
     * snapXY( p2.multiply(0.1) ));
     *
     * @param position (x or y)
     * @param offset (width or height)
     * @param dependencies
     * @return
     */
    public static DoubleBinding snapWH(final ObservableNumberValue position,
            final ObservableNumberValue offset,
            final Observable... dependencies) {
        return new DoubleBinding() {
            {
                super.bind(dependencies);
            }

            @Override
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override
            protected double computeValue() {
                return NodeUtil.snapWH(position.doubleValue(),
                        offset.doubleValue());
            }

            @Override
            public ObservableList<?> getDependencies() {
                return (dependencies.length == 1)
                        ? FXCollections.singletonObservableList(dependencies[0])
                        : FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(dependencies));
            }
        };
    }
}
