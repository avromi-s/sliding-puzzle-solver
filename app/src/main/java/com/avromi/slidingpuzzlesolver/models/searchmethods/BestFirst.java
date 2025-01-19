package com.avromi.slidingpuzzlesolver.models.searchmethods;

import com.avromi.slidingpuzzlesolver.models.interfaces.Node;
import com.avromi.slidingpuzzlesolver.models.interfaces.SearchMethod;
import com.avromi.slidingpuzzlesolver.models.classes.SearchResult;

import java.util.Comparator;
import java.util.PriorityQueue;


/**
 * Facilitates search with the Best-First informed search algorithm.
 */
public class BestFirst<T extends Node> implements SearchMethod<T> {
    private volatile boolean isSearchingForSolution = false;
    private BFS<T> bfs;

    /**
     * Search for a solution using the Best-First algorithm. The Best-First algorithm uses informed search to find the
     * fastest solution it can find in terms of search speed, regardless of solution depth.
     *
     * @param node the node to find the solution for
     * @return the PuzzleSolution found
     */
    @Override
    public SearchResult<T> search(T node) {
        this.isSearchingForSolution = true;

        // We construct a priority queue to sort the nodes based on their H values or heuristic value, i.e., the estimated
        // cost REMAINING from this node to get to any solution.
        this.bfs = new BFS<>();
        PriorityQueue<T> queue = new PriorityQueue<>(Comparator.comparingInt(T::getHValue));
        SearchResult<T> solution = bfs.search(node, queue);  // aside from the queue, the algorithm is the same as BFS.

        this.isSearchingForSolution = false;

        return solution;
    }

    @Override
    public void terminate() {
        if (this.isSearchingForSolution && this.bfs != null) {
            this.bfs.terminate();  // terminate the running bfs search
        }
    }
}
