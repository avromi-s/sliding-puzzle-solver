package com.avromi.slidingpuzzlesolver.models.searchmethods;

import com.avromi.slidingpuzzlesolver.models.interfaces.Node;
import com.avromi.slidingpuzzlesolver.models.interfaces.SearchMethod;
import com.avromi.slidingpuzzlesolver.models.classes.SearchResult;

import java.util.*;

/**
 * Facilitates depth-first search on a puzzle.
 */
public class DFS<T extends Node> implements SearchMethod<T> {
    private volatile boolean terminate = false;
    private volatile boolean isSearchingForSolution = false;

    /**
     * Search for a solution to the given node using DFS
     *
     * @return the solution found, if applicable
     */
    @Override
    public SearchResult<T> search(T node) {
        Stack<T> stack = new Stack<>();
        return search(node, stack);
    }

    /**
     * Search for a solution using the DFS algorithm.
     *
     * @param node  the node to find the solution for
     * @param stack the stack to use in the search.
     * @return the PuzzleSolution found
     */
    public SearchResult<T> search(T node, Stack<T> stack) {
        this.isSearchingForSolution = true;
        T solution = findSolution(node, stack);
        this.isSearchingForSolution = false;

        if (solution != null) {
            return new SearchResult<>(true, solution);
        } else {
            return new SearchResult<>(false);
        }
    }

    /**
     * Find a solution from the given node, using the given stack, and return the solution if found.
     *
     * @param node  the node to search from
     * @param stack the stack to use in the search
     * @return the solution node if found; otherwise null
     */
    private T findSolution(T node, Stack<T> stack) {
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
        return null;
    }

    @Override
    public void terminate() {
        if (this.isSearchingForSolution) {
            this.terminate = true;
        }
    }
}
