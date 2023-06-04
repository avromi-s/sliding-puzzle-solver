// Avromi Schneierson - 6.4.2023
package Interfaces;
import java.util.List;

/**
 * This interface represents a node for a puzzle search.
 * */
public interface INode {

    /**
     * @return all the nodes that can be (legally) reached within one move from this node.
     * */
    List<INode> getNextNodes();

    /**
     * @return the level that the node was encountered at.
     * */
    int getLevel();

    /**
     * @return the parent node, i.e., the node that after 1 move, led to this node.
     * */
    INode getParent();


    /**
     * @return the piece moved to get to this node
     * */
    String getPieceMoved();

    boolean isSolution();

    /**
     * Returns the minimum number of moves REMAINING to get from this node to a solution.
     *
     * @return the minimum number of moves REMAINING to get from this node to a solution.
     */
    int getHValue();

    /**
     * @return the actual cost (number of moves) used to get to this node.
     * */
    int getGValue();

    /**
     * Returns the total estimated cost of using this node to get to the solution, which is the level (G-value) + the
     * estimated cost of getting to the solution from this node (H-value).
     *
     * @return the total estimated cost of using this node to get to the solution.
     */
    int getFValue();
}
