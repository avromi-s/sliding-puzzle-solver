package com.avromi.slidingpuzzlesolver.models.interfaces;

import com.avromi.slidingpuzzlesolver.models.classes.SearchResult;

/**
 * Abstracts a puzzle search type, which must offer a search method.
 * @param <T> the puzzle type (an implementation of Node)
 * */
public interface SearchMethod<T extends Node> {

    /**
     * Search for a solution to the puzzle with the given node.
     *
     * @param node the node to find a solution for
     * @return the solution, if found
     */
    SearchResult<T> search(T node);

    /**
     * Terminate the search method, if running
     * */
    void terminate();
}