// Avromi Schneierson - 6.4.2023
package Classes.SearchMethods;
import Interfaces.INode;
import Interfaces.IPuzzleSearch;
import Classes.PuzzleSolution;
import java.util.*;

/**
 * Facilitates breadth-first search.
 * */
public class BFS extends IPuzzleSearch {

    /**
     * Search for a solution to the given node using BFS
     *
     * @return the solution found, if applicable
     * */
    @Override
    public PuzzleSolution search(INode node) {
        LinkedList<INode> queue = new LinkedList<>();
        return search(node, queue);
    }

    /**
     * Search for a solution to the puzzle using the given queue. If a LinkedList queue is provided, this runs regular BFS.
     * Otherwise, a priority queue with any ordering (like heuristics) can be used.
     *
     * @param node  the node to start the search from
     * @param queue the queue to store and pop the nodes in
     * @return the solution found, if applicable
     */
    public static PuzzleSolution search(INode node, Queue<INode> queue) {
        INode solution = findSolution(node, queue);

        if (solution != null) {
            return getSolutionResults(solution);
        } else {
            PuzzleSolution puzzleSolution = new PuzzleSolution();
            puzzleSolution.setFoundSolution(false);
            return puzzleSolution;
        }
    }


    /**
     * Find a solution from the given node, using the given queue, and return the solution if found.
     *
     * @param node the node to search from
     * @param queue thq queue to use when searching. The queue's priority will determine the order of which nodes are evaluated first.
     *
     * @return the solution node if found; otherwise null
     * */
    public static INode findSolution(INode node, Queue<INode> queue) {
        queue.offer(node);
        HashSet<Integer> colored = new HashSet<>();
        colored.add(node.hashCode());

        // Check if the given node is a solution before searching
        if (node.isSolution()) {
            return node;
        } else {
            // Search for a solution
            while (!queue.isEmpty()) {
                INode curr = queue.poll();
                for (INode child : curr.getNextNodes()) {
                    if (child.isSolution()) {
                        return child;
                    }
                    if (!colored.contains(child.hashCode())) {
                        queue.offer(child);
                        colored.add(child.hashCode());
                    }
                }
            }
        }
        return null;
    }
}
