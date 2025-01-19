package com.avromi.slidingpuzzlesolver.models.searchmethods;

import com.avromi.slidingpuzzlesolver.models.interfaces.Node;
import com.avromi.slidingpuzzlesolver.models.interfaces.SearchMethod;
import com.avromi.slidingpuzzlesolver.models.classes.SearchResult;

import java.util.*;

/**
 * Facilitates breadth-first search.
 */
public class BFS<T extends Node> implements SearchMethod<T> {
    private volatile boolean terminate = false;
    private volatile boolean isSearchingForSolution = false;

    /**
     * Search for a solution to the given node using BFS
     *
     * @return the solution found, if applicable
     */
    @Override
    public SearchResult<T> search(T node) {
        LinkedList<T> queue = new LinkedList<>();
        return search(node, queue);
    }

    /**
     * Search for a solution to the puzzle using the given queue. If a LinkedList queue is provided,
     * this runs regular BFS.
     * Otherwise, a priority queue with any ordering (like heuristics) can be used.
     *
     * @param node  the node to start the search from
     * @param queue the queue to store and pop the nodes in
     * @return the solution found, if applicable
     */
    public SearchResult<T> search(T node, Queue<T> queue) {
        this.isSearchingForSolution = true;
        T solution = findSolution(node, queue);
        this.isSearchingForSolution = false;

        if (solution != null) {
            return new SearchResult<>(true, solution);
        } else {
            return new SearchResult<>(false);
        }
    }


    /**
     * Find a solution from the given node, using the given queue, and return the solution if found.
     *
     * @param node  the node to search from
     * @param queue the queue to use when searching. The queue's priority will determine the order of which nodes are evaluated first.
     * @return the solution node if found; otherwise null
     */
    private T findSolution(T node, Queue<T> queue) {
        queue.offer(node);
        HashSet<Integer> colored = new HashSet<>();
        colored.add(node.hashCode());

        // Check if the given node is a solution before searching
        if (node.isSolution()) {
            return node;
        } else {
            // Search for a solution
            while (!queue.isEmpty()) {
                if (this.terminate) {
                    this.terminate = false;
                    break;
                }

                T curr = queue.poll();
                for (Node child : curr.getNextNodes()) {
                    if (child.isSolution()) {
                        return (T) child;
                    }
                    if (!colored.contains(child.hashCode())) {
                        queue.offer((T) child);
                        colored.add(child.hashCode());
                    }
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
