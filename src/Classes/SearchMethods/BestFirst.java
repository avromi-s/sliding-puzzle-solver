// Avromi Schneierson - 6.4.2023
package Classes.SearchMethods;
import Classes.PuzzleSearchResult;
import Interfaces.INode;
import Classes.IPuzzleSearch;
import java.util.Comparator;
import java.util.PriorityQueue;


/**
 * Facilitates search with the Best-First informed search algorithm.
 * */
public class BestFirst extends IPuzzleSearch {

    /**
     * Search for a solution using the Best-First algorithm. The Best-First algorithm uses informed search to find the
     * fastest solution it can find in terms of search speed, regardless of solution depth.
     * @param node the node to find the solution for
     * @return the PuzzleSolution found
     * */
    @Override
    public PuzzleSearchResult search(INode node) {
        // We construct a priority queue to sort the nodes based on their H values or heuristic value, i.e., the estimated
        // cost REMAINING from this node to get to any solution.
        PriorityQueue<INode> queue = new PriorityQueue<>(Comparator.comparingInt(INode::getHValue));
        return BFS.search(node, queue);  // aside from the queue, the algorithm is the same as BFS.
    }
}
