// Avromi Schneierson - 6.4.2023
package Classes.SearchMethods;
import Classes.PuzzleSolution;
import Interfaces.INode;
import Interfaces.IPuzzleSearch;
import java.util.Comparator;
import java.util.PriorityQueue;


/**
 * Facilitates search with the A* informed search algorithm.
 * */
public class AStar extends IPuzzleSearch {

    /**
     * Search for a solution using the A* algorithm. The A* algorithm uses informed search to find the fastest solution
     * (in number of moves), regardless of search speed.
     * @param node the node to find the solution for
     * @return the PuzzleSolution found
     * */
    @Override
    public PuzzleSolution search(INode node) {
        // We construct a priority queue to sort the nodes based on their F values, which is the level + h value, or total
        // estimated cost of using this node to get to a solution.
        PriorityQueue<INode> queue = new PriorityQueue<>(Comparator.comparingInt(INode::getFValue));
        return BFS.search(node, queue);  // aside from the queue, the algorithm is the same as BFS.
    }
}

