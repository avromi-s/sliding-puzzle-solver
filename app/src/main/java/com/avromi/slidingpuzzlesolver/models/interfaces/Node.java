package com.avromi.slidingpuzzlesolver.models.interfaces;

import java.util.List;

/**
 * This interface represents a node for a puzzle search.
 */
public interface Node {

    /**
     * @return all the nodes that can be (legally) reached within one move from this node.
     */
    List<Node> getNextNodes();

    /**
     * @return the level that the node was encountered at.
     */
    int getLevel();

    /**
     * @return the parent node, i.e., the node that after 1 move, led to this node.
     */
    Node getParent();


    /**
     * @return the piece moved to get to this node
     */
    int getMovedPiece();

    boolean isSolution();

    /**
     * Returns the minimum number of moves REMAINING to get from this node to a solution.
     *
     * @return the minimum number of moves REMAINING to get from this node to a solution.
     */
    int getHValue();

    /**
     * @return the actual cost (number of moves) used to get to this node.
     */
    int getGValue();

    /**
     * Returns the total estimated cost of using this node to get to the solution, which is the level (G-value) + the
     * estimated cost of getting to the solution from this node (H-value).
     *
     * @return the total estimated cost of using this node to get to the solution.
     */
    int getFValue();
}
