// Avromi Schneierson - 6.4.2023
package Classes.SearchMethods;

import Classes.PuzzleSearchResult;
import Interfaces.INode;
import Classes.PuzzleSearch;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Facilitates search with the A* informed search algorithm.
 */
public class AStar extends PuzzleSearch {

    /**
     * Search for a solution using the A* algorithm. The A* algorithm uses informed search to find the fastest solution
     * (in number of moves), regardless of search speed.
     *
     * @param node the node to find the solution for
     * @return the PuzzleSolution found
     */
    public PuzzleSearchResult search(INode node) {
        // We construct a priority queue to sort the nodes based on their F values, which is the level + h value, or total
        // estimated cost of using this node to get to a solution.
        PriorityQueue<INode> queue = new PriorityQueue<>(Comparator.comparingInt(INode::getFValue));
        INode solution = findSolution(node, queue);
        if (solution != null) {
            return new PuzzleSearchResult(solution, true);
        } else {
            PuzzleSearchResult puzzleSearchResult = new PuzzleSearchResult();
            puzzleSearchResult.setSolutionWasFound(false);
            return puzzleSearchResult;
        }
    }

    /**
     * Find a solution from the given node, using the given queue, and return the solution if found. The algorithm used
     * for A* here is the same as with BFS except that the nodes are checked for a solution when removed from the queue
     * instead of when being put on.
     *
     * @param node the node to search from
     * @param queue the queue to use when searching. The queue's priority will determine the order of which nodes are evaluated first.
     *
     * @return the solution node if found; otherwise null
     * */
    public INode findSolution(INode node, Queue<INode> queue) {
        queue.offer(node);
        HashSet<Integer> colored = new HashSet<>();
        colored.add(node.hashCode());
        // Search for a solution
        while (!queue.isEmpty()) {
            INode curr = queue.poll();
            // Check if the node is the solution when removing from the queue, so that nodes are evaluated in our queue's order
            if (curr.isSolution()) {
                return curr;
            }
            for (INode child : curr.getNextNodes()) {
                if (!colored.contains(child.hashCode())) {
                    queue.offer(child);
                    colored.add(child.hashCode());
                }
            }
        }
        return null;
    }
}

