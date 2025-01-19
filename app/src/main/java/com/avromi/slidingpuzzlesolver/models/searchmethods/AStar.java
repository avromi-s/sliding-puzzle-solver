package com.avromi.slidingpuzzlesolver.models.searchmethods;

import com.avromi.slidingpuzzlesolver.models.interfaces.Node;
import com.avromi.slidingpuzzlesolver.models.interfaces.SearchMethod;
import com.avromi.slidingpuzzlesolver.models.classes.SearchResult;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Facilitates search with the A* informed search algorithm.
 */
public class AStar<T extends Node> implements SearchMethod<T> {
    private volatile boolean terminate = false;
    private volatile boolean isSearchingForSolution = false;

    /**
     * Search for a solution using the A* algorithm. The A* algorithm uses informed search to find the fastest solution
     * (in number of moves), regardless of search speed.
     *
     * @param node the node to find the solution for
     * @return the PuzzleSolution found
     */
    public SearchResult<T> search(T node) {
        this.isSearchingForSolution = true;

        // We construct a priority queue to sort the nodes based on their F values, which is the level + h value, or total
        // estimated cost of using this node to get to a solution.
        PriorityQueue<T> queue = new PriorityQueue<>(Comparator.comparingInt(T::getFValue));
        T solution = findSolution(node, queue);

        this.isSearchingForSolution = false;

        if (solution != null) {
            return new SearchResult<>(true, solution);
        } else {
            return new SearchResult<>(false);
        }
    }

    /**
     * Find a solution from the given node, using the given queue, and return the solution if found. The algorithm used
     * for A* here is the same as with BFS except that the nodes are checked for a solution when removed from the queue
     * instead of when being put on.
     *
     * @param node  the node to search from
     * @param queue the queue to use when searching. The queue's priority will determine the order of which nodes are evaluated first.
     * @return the solution node if found; otherwise null
     */
    private T findSolution(T node, Queue<T> queue) {
        queue.offer(node);
        HashSet<Integer> colored = new HashSet<>();
        colored.add(node.hashCode());
        // Search for a solution
        while (!queue.isEmpty()) {
            if (this.terminate) {
                this.terminate = false;
                break;
            }

            T curr = queue.poll();
            // Check if the node is the solution when removing from the queue, so that nodes are evaluated in our queue's order
            if (curr.isSolution()) {
                return curr;
            }
            for (Node child : curr.getNextNodes()) {
                if (!colored.contains(child.hashCode())) {
                    queue.offer((T) child);
                    colored.add(child.hashCode());
                }
            }
        }
        return null;
    }

    @Override
    public void terminate() {
        if (this.isSearchingForSolution) {
            this.terminate = true;
        }
    }
}

