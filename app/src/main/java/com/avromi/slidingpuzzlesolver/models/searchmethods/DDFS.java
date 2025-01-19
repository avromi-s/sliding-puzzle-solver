package com.avromi.slidingpuzzlesolver.models.searchmethods;

import com.avromi.slidingpuzzlesolver.models.interfaces.Node;
import com.avromi.slidingpuzzlesolver.models.interfaces.SearchMethod;
import com.avromi.slidingpuzzlesolver.models.classes.SearchResult;

import java.util.HashSet;
import java.util.Stack;

/**
 * Facilitates search with depth-bounded DFS.
 */
public class DDFS<T extends Node> implements SearchMethod<T> {
    private volatile boolean terminate = false;
    private volatile boolean isSearchingForSolution = false;

    /**
     * The maximum depth to search until. Once this depth is hit, the search terminates.
     */
    private final int DEFAULT_MAX_DEPTH = 10;

    /**
     * Search for a solution to the given node using DDFS
     *
     * @return the solution found, if applicable
     */
    @Override
    public SearchResult<T> search(T node) {
        return search(node, DEFAULT_MAX_DEPTH);
    }

    /**
     * Search for a solution using the DDFS algorithm. DDFS performs DFS search up until the set
     * maximum depth, terminating with no solution if it is not found by the set depth.
     *
     * @param node the node to find the solution for
     * @return the PuzzleSolution found
     */
    public SearchResult<T> search(T node, int maxDepth) {
        this.isSearchingForSolution = true;
        T solution = findSolution(node, maxDepth);
        this.isSearchingForSolution = false;

        if (solution != null) {
            return new SearchResult<>(true, solution);
        } else {
            return new SearchResult<>(false);
        }
    }

    /**
     * Find a solution from the given node, up until the provided max depth, and return the solution if found.
     *
     * @param node     the node to search from
     * @param maxDepth the maximum depth to search until
     * @return the solution node if found; otherwise null
     */
    private T findSolution(T node, int maxDepth) {
        Stack<T> stack = new Stack<>();
        int depth;
        stack.push(node);
        HashSet<Integer> colored = new HashSet<>();
        colored.add(node.hashCode());

        // Check if the given node is a solution before searching
        if (node.isSolution()) {
            return node;
        } else {
            while (!stack.isEmpty()) {
                if (this.terminate) {
                    this.terminate = false;
                    break;
                }

                // Otherwise, search for a solution
                T curr = stack.pop();
                depth = curr.getLevel();
                if (depth < maxDepth) { // *** Check that we are not past the max depth before we evaluate the children.
                    for (Node child : curr.getNextNodes()) {
                        if (child.isSolution()) {
                            return (T) child;
                        }
                        if (!colored.contains(child.hashCode())) {
                            stack.push((T) child);
                            colored.add(child.hashCode());
                        }
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
