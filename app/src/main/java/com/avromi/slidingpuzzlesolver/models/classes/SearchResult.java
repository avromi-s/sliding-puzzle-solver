package com.avromi.slidingpuzzlesolver.models.classes;

import com.avromi.slidingpuzzlesolver.models.interfaces.Node;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a search result to be returned from a search method.
 * @param <T> the puzzle type (an implementation of Node)
 */
public class SearchResult<T extends Node> {
    /**
     * Indicate if a solution was found
     */
    private boolean solutionWasFound = false;

    private T[] solutionPath;

    /**
     * No-args constructor
     */
    public SearchResult(boolean solutionWasFound) {
        this.solutionWasFound = solutionWasFound;
    }

    /**
     * Constructs a search result object
     *
     * @param solutionWasFound whether a solution was found
     * @param solution         the solution node
     */
    public SearchResult(boolean solutionWasFound, T solution) {
        this.solutionWasFound = solutionWasFound;
        if (solutionWasFound) {
            this.solutionPath = (T[]) new Node[solution.getLevel() + 1];
            Node curr = solution;
            for (int i = solution.getLevel(); i >= 0; i--) {
                this.solutionPath[i] = (T) curr;
                curr = curr.getParent();
            }
        }
    }

    public boolean getSolutionWasFound() {
        return solutionWasFound;
    }

    public List<T> getSolutionPath() {
        return solutionWasFound ? Arrays.asList(this.solutionPath) : List.of();
    }
}
