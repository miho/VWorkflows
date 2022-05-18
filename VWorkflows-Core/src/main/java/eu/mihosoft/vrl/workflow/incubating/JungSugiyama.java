/*
 * Copyright (c) 2012 Stefan Wolf
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

// package hudson.plugins.depgraph_view.model.layout;
/* 
 * from https://github.com/jenkinsci/depgraph-view-plugin
 * bjeffrie 2016-10-01 Add JungSugiyama as layout in VWorkflows package hierarchy.
 * bjeffrie 2016-12-07 Small modifications and formating to pass vworkflow's style checker
 */

package eu.mihosoft.vrl.workflow.incubating;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Arranges the nodes with the Sugiyama Layout Algorithm.<br>
 * * <a href="http://plg.uwaterloo.ca/~itbowman/CS746G/Notes/Sugiyama1981_MVU/"> Link to the algorithm</a><br>
 *
 * Originally, source was posted to the Jung2 forum, for Jung 1.x. Not sure where the original code came from, but ti didn;t work for Jung2, but it was not that complicated, so I
 * pounded it into shape for Jung2, complete with generics and such. Lays out either top-down to left-right.
 *
 * Seems to work. Paramterize with spacing and orientation.
 *
 * C. Schanck (chris at schanck dot net)
 */

public class JungSugiyama<V, E> extends AbstractLayout<V, E> {

    private static final Orientation DEFAULT_ORIENTATION = Orientation.TOP;

    private static final int DEFAULT_HORIZONTAL_SPACING = 200;

    private static final int DEFAULT_VERTICAL_SPACING = 100;

    public static enum Orientation {
        TOP, LEFT
    };

    private boolean executed = false;

    /**
     * represents the size of the grid in horizontal grid elements
     *
     */
    private int gridAreaSize = Integer.MIN_VALUE;

    private int horzSpacing;

    private int vertSpacing;

    private Map<V, CellWrapper<V>> vertToWrapper = new HashMap<V, CellWrapper<V>>();

    private Orientation orientation;

    public JungSugiyama(Graph<V, E> g) {
        this(g, DEFAULT_ORIENTATION, DEFAULT_HORIZONTAL_SPACING, DEFAULT_VERTICAL_SPACING);
    }

    public JungSugiyama(Graph<V, E> g, Orientation orientation, int horzSpacing, int vertSpacing) {
        super(copyGraph(g));
        this.orientation = orientation;
        this.horzSpacing = horzSpacing;
        this.vertSpacing = vertSpacing;
    }

    private static <V, E> Graph<V, E> copyGraph(Graph<V, E> src) {
        try {
            @SuppressWarnings("unchecked")
            Graph<V, E> dest = (Graph<V, E>) src.getClass().newInstance();
            for (V v: src.getVertices())
                dest.addVertex(v);

            for (E e: src.getEdges())
                dest.addEdge(e, src.getIncidentVertices(e));
            return dest;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
        if (!executed) {
            List<List<CellWrapper<V>>> graphLevels = runSugiyama();
            for (List<CellWrapper<V>> level: graphLevels) {
                for (CellWrapper<V> wrapper: level) {
                    V vertex = wrapper.getVertexView();

                    if (orientation.equals(Orientation.TOP)) {
                        double xCoordinate = 10.0 + (wrapper.gridPosition * horzSpacing);
                        double yCoordinate = 10.0 + (wrapper.level * vertSpacing);
                        setLocation(vertex, xCoordinate, yCoordinate);
                    } else {
                        double yCoordinate = 10.0 + (wrapper.gridPosition * vertSpacing);
                        double xCoordinate = 10.0 + (wrapper.level * horzSpacing);
                        setLocation(vertex, xCoordinate, yCoordinate);
                    }
                }
            }
        }

    }

    public String toString() {
        return "Jung Sugiyama";
    }

    /**
     * Implementation.
     *
     * First of all, the Algorithm searches the roots from the Graph. Starting from this roots the Algorithm creates levels and stores them in the member <code>levels</code>. The
     * Member levels contains LinkedList Objects and the LinkedList per level contains Cell Wrapper Objects. After that the Algorithm tries to solve the edge crosses from level to
     * level and goes top down and bottom up. After minimization of the edge crosses the algorithm moves each node to its bary center.
     *
     */
    private List<List<CellWrapper<V>>> runSugiyama() {
        executed = true;
        Set<V> vertexSet = new HashSet<V>(graph.getVertices());

        makeGraphAcyclic();
        List<List<CellWrapper<V>>> levels = fillLevels();
        levels = balanceLevels(levels);
        solveEdgeCrosses(levels);
        moveToBarycenter(levels, vertexSet);
        return levels;
    }

    private void makeGraphAcyclic() {
        new AcyclicCalculator().run();
    }

    private List<List<CellWrapper<V>>> fillLevels() {
        return fillLevels(0, copyGraph(graph), new LinkedList<List<CellWrapper<V>>>());
    }

    /**
     * Method fills the levels and stores them in the member levels.
     * 
     * Each level was represended by a LinkedList with Cell Wrapper objects. These LinkedLists are the elements in the <code>levels</code> LinkedList.
     *
     */
    private List<List<CellWrapper<V>>> fillLevels(int currentLevel, Graph<V, E> graph, List<List<CellWrapper<V>>> levels) {
        if (graph.getVertices().isEmpty()) {
            return levels;
        }
        List<V> roots = searchRoots(graph);
        if (levels.size() == currentLevel) levels.add(currentLevel, new LinkedList<CellWrapper<V>>());
        List<CellWrapper<V>> vecForTheCurrentLevel = levels.get(currentLevel);
        for (V rootNode: roots) {
            // Create a wrapper for the node
            int numberForTheEntry = vecForTheCurrentLevel.size();
            CellWrapper<V> wrapper = new CellWrapper<V>(currentLevel, numberForTheEntry, rootNode);

            // put the Wrapper in the LevelLinkedList
            vecForTheCurrentLevel.add(wrapper);
            // concat the wrapper to the cell for an easy access
            vertToWrapper.put(rootNode, wrapper);

            graph.removeVertex(rootNode);
        } // i.e root level
        if (vecForTheCurrentLevel.size() > gridAreaSize) {
            gridAreaSize = vecForTheCurrentLevel.size();
        }
        fillLevels(currentLevel + 1, graph, levels); // 0 indicates level 0
        return levels;
    }

    /**
     * Searches all Roots for the current Graph First the method marks any Node as not visited. Than calls searchRoots(MyGraphCell) for each not visited Cell. The Roots are stored
     * in the LinkedList named roots
     *
     * @return returns a LinkedList with the roots
     */
    private List<V> searchRoots(Graph<V, E> graph) {
        List<V> roots = new LinkedList<V>();
        // first: mark all as not visited
        // it is assumed that vertex are not visited
        for (V vert: graph.getVertices()) {
            int in_degree = graph.inDegree(vert);
            if (in_degree == 0) {
                roots.add(vert);
            }
        }
        return roots;
    }

    private List<List<CellWrapper<V>>> balanceLevels(List<List<CellWrapper<V>>> levels) {
        Map<V, Integer> maxLevels = new HashMap<V, Integer>();
        Map<V, Integer> minLevels = new HashMap<V, Integer>();
        List<CellWrapper<V>> verticesToAdd = new LinkedList<CellWrapper<V>>();
        List<Integer> sizes = new ArrayList<Integer>(levels.size());
        for (List<CellWrapper<V>> levelList: levels) {
            for (CellWrapper<V> node: levelList) {
                int minLevel = findMinPossibleLevel(node);
                int maxLevel = findMaxPossibleLevel(node, levels.size());
                if (minLevel != maxLevel) {
                    maxLevels.put(node.getVertexView(), maxLevel);
                    minLevels.put(node.getVertexView(), minLevel);
                    verticesToAdd.add(node);
                }
            }
            levelList.removeAll(verticesToAdd);
            sizes.add(levelList.size());
        }
        for (CellWrapper<V> vertex: verticesToAdd) {
            int minSize = Integer.MAX_VALUE;
            int minIndex = -1;
            for (int i = minLevels.get(vertex.getVertexView()); i <= maxLevels.get(vertex.getVertexView()); i++) {
                if (sizes.get(i) < minSize) {
                    minSize = sizes.get(i);
                    minIndex = i;
                }
            }
            levels.get(minIndex).add(vertex);
            sizes.set(minIndex, sizes.get(minIndex) + 1);
        }
        List<List<CellWrapper<V>>> newLevels = new LinkedList<List<CellWrapper<V>>>();
        for (List<CellWrapper<V>> level: levels) {
            LinkedList<CellWrapper<V>> newLevel = new LinkedList<CellWrapper<V>>();
            for (CellWrapper<V> cellWrapper: level) {
                newLevel.add(new CellWrapper<V>(newLevels.size(), newLevel.size(), cellWrapper.getVertexView()));
            }
            newLevels.add(newLevel);
        }
        return newLevels;
    }

    private int findMaxPossibleLevel(CellWrapper<V> node, int numLevels) {
        V vertex = node.getVertexView();
        int maxLevel = numLevels - 1;
        for (V successor: graph.getSuccessors(vertex)) {
            int level = vertToWrapper.get(successor).getLevel();
            maxLevel = Math.min(level - 1, maxLevel);
        }
        return maxLevel;
    }

    private int findMinPossibleLevel(CellWrapper<V> node) {
        V vertex = node.getVertexView();
        Collection<V> predecessors = graph.getPredecessors(vertex);
        int minLevel = 0;
        for (V predecessor: predecessors) {
            int level = vertToWrapper.get(predecessor).getLevel();
            minLevel = Math.max(level + 1, minLevel);
        }
        return minLevel;
    }

    private void solveEdgeCrosses(List<List<CellWrapper<V>>> levels) {
        int movementsCurrentLoop = -1;

        while (movementsCurrentLoop != 0) {
            // reset the movements per loop count
            movementsCurrentLoop = 0;

            // top down
            for (int i = 0; i < levels.size() - 1; i++) {
                movementsCurrentLoop += solveEdgeCrosses(true, levels, i);
            }

            // bottom up
            for (int i = levels.size() - 1; i >= 1; i--) {
                movementsCurrentLoop += solveEdgeCrosses(false, levels, i);
            }
        }
    }

    /**
     * @return movements
     */
    private int solveEdgeCrosses(boolean down, List<List<CellWrapper<V>>> levels, int levelIndex) {
        // Get the current level
        List<CellWrapper<V>> currentLevel = levels.get(levelIndex);
        int movements = 0;

        // restore the old sort
        CellWrapper<?>[] levelSortBefore = currentLevel.toArray(new CellWrapper[] {});

        // new sort
        Collections.sort(currentLevel);

        // test for movements
        for (int j = 0; j < levelSortBefore.length; j++) {
            if (levelSortBefore[ j ].getEdgeCrossesIndicator() != currentLevel.get(j).getEdgeCrossesIndicator()) {
                movements++;
            }
        }
        // Collections Sort sorts the highest value to the first value
        for (int j = currentLevel.size() - 1; j >= 0; j--) {
            CellWrapper<V> sourceWrapper = currentLevel.get(j);

            V sourceView = sourceWrapper.getVertexView();

            Collection<E> edgeList = getNeighborEdges(sourceView);

            for (E edge: edgeList) {
                // if it is a forward edge follow it
                V targetView = null;
                if (down && sourceView == graph.getSource(edge)) {
                    targetView = graph.getDest(edge);
                }
                if (!down && sourceView == graph.getDest(edge)) {
                    targetView = graph.getSource(edge);
                }
                if (targetView != null) {
                    CellWrapper<V> targetWrapper = vertToWrapper.get(targetView);

                    // do it only if the edge is a forward edge to a deeper level
                    if (down && targetWrapper != null && targetWrapper.getLevel() > levelIndex) {
                        targetWrapper.addToEdgeCrossesIndicator(sourceWrapper.getEdgeCrossesIndicator());
                    }
                    if (!down && targetWrapper != null && targetWrapper.getLevel() < levelIndex) {
                        targetWrapper.addToEdgeCrossesIndicator(sourceWrapper.getEdgeCrossesIndicator());
                    }
                }
            }
        }
        return movements;
    }

    private void moveToBarycenter(List<List<CellWrapper<V>>> levels, Set<V> vertexSet) {
        for (V v: vertexSet) {

            CellWrapper<V> currentwrapper = vertToWrapper.get(v);

            Collection<E> edgeList = getNeighborEdges(v);

            for (E edge: edgeList) {
                // i have to find neigbhor vertex
                V neighborVertex = null;

                if (v == graph.getSource(edge)) {
                    neighborVertex = graph.getDest(edge);
                } else {
                    if (v == graph.getDest(edge)) {
                        neighborVertex = graph.getSource(edge);
                    }
                }

                if ((neighborVertex != null) && (neighborVertex != v)) {

                    CellWrapper<V> neighborWrapper = vertToWrapper.get(neighborVertex);

                    if (!(currentwrapper == null || neighborWrapper == null || currentwrapper.level == neighborWrapper.level)) {
                        currentwrapper.priority++;
                    }
                }
            }
        }
        for (List<CellWrapper<V>> level: levels) {
            int pos = 0;
            for (CellWrapper<V> wrapper: level) {
                // calculate the initial Grid Positions 1, 2, 3, .... per Level
                wrapper.setGridPosition(pos++);
            }
        }

        int movementsCurrentLoop = -1;

        while (movementsCurrentLoop != 0) {
            // reset movements
            movementsCurrentLoop = 0;

            // top down
            for (int i = 1; i < levels.size(); i++) {
                movementsCurrentLoop += moveToBarycenter(levels, i);
            }
            // bottom up
            for (int i = levels.size() - 1; i >= 0; i--) {
                movementsCurrentLoop += moveToBarycenter(levels, i);
            }
        }
    }

    private Collection<E> getNeighborEdges(V v) {
        Collection<E> outEdges = graph.getOutEdges(v);
        Collection<E> inEdges = graph.getInEdges(v);
        LinkedList<E> edgeList = new LinkedList<E>();
        edgeList.addAll(outEdges);
        edgeList.addAll(inEdges);
        return edgeList;
    }

    private int moveToBarycenter(List<List<CellWrapper<V>>> levels, int levelIndex) {
        // Counter for the movements
        int movements = 0;

        // Get the current level
        List<CellWrapper<V>> currentLevel = levels.get(levelIndex);

        for (int currentIndexInTheLevel = 0; currentIndexInTheLevel < currentLevel.size(); currentIndexInTheLevel++) {
            CellWrapper<V> sourceWrapper = currentLevel.get(currentIndexInTheLevel);

            float gridPositionsSum = 0;
            float countNodes = 0;

            V vertexView = sourceWrapper.getVertexView();

            Collection<E> edgeList = getNeighborEdges(vertexView);

            for (E edge: edgeList) {
                // if it is a forward edge follow it
                // Object neighborPort = null;
                V neighborVertex = null;
                if (vertexView == graph.getSource(edge)) {
                    neighborVertex = graph.getDest(edge);
                } else {
                    if (vertexView == graph.getSource(edge)) {
                        neighborVertex = graph.getDest(edge);
                    }
                }

                if (neighborVertex != null) {

                    CellWrapper<V> targetWrapper = vertToWrapper.get(neighborVertex);

                    if (!(targetWrapper == sourceWrapper) || targetWrapper.getLevel() == levelIndex) {
                        gridPositionsSum += targetWrapper.getGridPosition();
                        countNodes++;
                    }
                }
            }

            if (countNodes > 0) {
                float tmp = (gridPositionsSum / countNodes);
                int newGridPosition = Math.round(tmp);
                boolean toRight = (newGridPosition > sourceWrapper.getGridPosition());

                boolean moved = true;

                while (newGridPosition != sourceWrapper.getGridPosition() && moved) {
                    moved = move(toRight, currentLevel, currentIndexInTheLevel, sourceWrapper.getPriority());
                    if (moved) {
                        movements++;
                    }
                }
            }
        }
        return movements;
    }

    /**
     * @param toRight
     *            <tt>true</tt> = try to move the currentWrapper to right; <tt>false</tt> = try to move the currentWrapper to left;
     * @param currentLevel
     *            LinkedList which contains the CellWrappers for the current level
     *
     * @return The free GridPosition or -1 is position is not free.
     */
    private boolean move(boolean toRight, List<CellWrapper<V>> currentLevel, int currentIndexInTheLevel, int currentPriority) {
        CellWrapper<V> currentWrapper = currentLevel.get(currentIndexInTheLevel);

        boolean moved = false;
        int neighborIndexInTheLevel = currentIndexInTheLevel + (toRight ? 1 : -1);
        int newGridPosition = currentWrapper.getGridPosition() + (toRight ? 1 : -1);

        if (0 > newGridPosition || newGridPosition >= gridAreaSize) {
            return false;
        }

        // if the node is the first or the last we can move
        if (toRight && currentIndexInTheLevel == currentLevel.size() - 1 || !toRight && currentIndexInTheLevel == 0) {
            moved = true;
        } else {
            // else get the neighbor and ask his gridposition
            // if he has the requested new grid position
            // check the priority

            CellWrapper<V> neighborWrapper = (CellWrapper<V>) currentLevel.get(neighborIndexInTheLevel);

            int neighborPriority = neighborWrapper.getPriority();

            if (neighborWrapper.getGridPosition() == newGridPosition) {
                if (neighborPriority >= currentPriority) {
                    return false;
                } else {
                    moved = move(toRight, currentLevel, neighborIndexInTheLevel, currentPriority);
                }
            } else {
                moved = true;
            }
        }

        if (moved) {
            currentWrapper.setGridPosition(newGridPosition);
        }
        return moved;
    }

    // ---------------cell wrapper-----------------
    /**
     * cell wrapper contains all values for one node
     */
    static class CellWrapper<VV> implements Comparable<CellWrapper<VV>> {
        /**
         * sum value for edge Crosses
         */
        private double edgeCrossesIndicator = 0;

        /**
         * counter for additions to the edgeCrossesIndicator
         */
        private int additions = 0;

        /**
         * the vertical level where the cell wrapper is inserted
         */
        private int level = 0;

        /**
         * current position in the grid
         */
        private int gridPosition = 0;

        /**
         * priority for movements to the barycenter
         */
        private int priority = 0;

        /**
         * reference to the wrapped cell
         */
        private VV wrappedVertex = null;

        private String vertexName = "";

        // CellWrapper constructor
        CellWrapper(int level, double edgeCrossesIndicator, VV vertex) {
            this.level = level;
            this.edgeCrossesIndicator = edgeCrossesIndicator;
            this.wrappedVertex = vertex;
            vertexName = vertex.toString();
            additions++;
        }

        public String toString() {
            return vertexName + "," + level + "," + gridPosition + "," + priority + "," + edgeCrossesIndicator + "," + additions;
        }

        /**
         * returns the wrapped Vertex
         */
        VV getVertexView() {
            return wrappedVertex;
        }

        /**
         * retruns the average value for the edge crosses indicator
         *
         * for the wrapped cell
         *
         */

        double getEdgeCrossesIndicator() {
            if (additions == 0) return 0;
            return edgeCrossesIndicator / additions;
        }

        /**
         * Addes a value to the edge crosses indicator for the wrapped cell
         *
         */
        void addToEdgeCrossesIndicator(double addValue) {
            edgeCrossesIndicator += addValue;
            additions++;
        }

        /**
         * gets the level of the wrapped cell
         */
        int getLevel() {
            return level;
        }

        /**
         * gets the grid position for the wrapped cell
         */
        int getGridPosition() {
            return gridPosition;
        }

        /**
         * Sets the grid position for the wrapped cell
         */
        void setGridPosition(int pos) {
            this.gridPosition = pos;
        }

        /**
         * returns the priority of this cell wrapper.
         *
         * The priority was used by moving the cell to its barycenter.
         */
        int getPriority() {
            return priority;
        }

        /**
         * @see java.lang.Comparable#compareTo(Object)
         */
        public int compareTo(CellWrapper<VV> compare) {
            if (compare.getEdgeCrossesIndicator() == this.getEdgeCrossesIndicator()) return 0;

            double compareValue = compare.getEdgeCrossesIndicator() - this.getEdgeCrossesIndicator();

            return (int) (compareValue * 1000);

        }
    }

    // --------------------------------------------
    public void reset() {
        vertToWrapper.clear();
        executed = false;
    }

    private class AcyclicCalculator {
        private Set<V> onStack = new HashSet<V>();
        private Set<V> visited = new HashSet<V>();

        public AcyclicCalculator() {
        }

        private void dfs(V vertex) {
            if (visited.contains(vertex)) {
                return;
            }

            visited.add(vertex);
            onStack.add(vertex);

            for (E edge: graph.getOutEdges(vertex)) {
                V target = graph.getDest(edge);
                if (onStack.contains(target)) {
                    graph.removeEdge(edge);
                    graph.addEdge(edge, target, vertex);
                } else {
                    dfs(target);
                }
            }
            onStack.remove(vertex);
        }

        public void run() {
            for (V vertex: graph.getVertices()) {
                dfs(vertex);
            }
        }
    }
}
