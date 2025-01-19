package com.avromi.slidingpuzzlesolver.models.searchmethods;

import com.avromi.slidingpuzzlesolver.models.interfaces.Node;
import com.avromi.slidingpuzzlesolver.models.interfaces.SearchMethod;
import com.avromi.slidingpuzzlesolver.models.classes.SearchResult;

/**
 * Facilitates iterative-deepening depth-first search on a puzzle.
 */
public class IDDFS<T extends Node> implements SearchMethod<T> {
    private volatile boolean isSearchingForSolution = false;
    private volatile boolean terminate = false;
    private DDFS<T> ddfs;

    /**
     * The initial depth to start with
     */
    public final int DEFAULT_INITIAL_SEARCH_DEPTH = 20;

    /**
     * The amount to increment the depth by when a search fails to find a result
     */
    public final int DEFAULT_SEARCH_DEPTH_INCREMENT = 5;

    public final long DEFAULT_TIMEOUT_MS = 60000L;

    /**
     * Search for a solution using the default initial search depth, increment, and timeout
     * */
    @Override
    public SearchResult<T> search(T node) {
        return search(node, DEFAULT_INITIAL_SEARCH_DEPTH, DEFAULT_SEARCH_DEPTH_INCREMENT, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Search for a solution using IDDFS. IDDFS performs depth bounded DFS (DDFS), but
     * repeatedly increments the max depth if a solution isn't found.
     *
     * @param node the node to find the solution for
     * @param initialSearchDepth the search depth to start with
     * @param searchDepthIncrement the increment to increase the search depth by each time
     * @param timeoutMs length of time after which search is aborted if no solution is found
     * @return the PuzzleSolution found
     */
    public SearchResult<T> search(T node, int initialSearchDepth, int searchDepthIncrement, long timeoutMs) {
        long startTime = System.currentTimeMillis();
        boolean timeIsUp = System.currentTimeMillis() - startTime > timeoutMs;

        SearchResult<T> solution = new SearchResult<>(false);
        this.ddfs = new DDFS<>();

        this.isSearchingForSolution = true;

        // Run until a solution is found or we hit the timeout
        int depth = initialSearchDepth;
        while (!timeIsUp && !solution.getSolutionWasFound()) {
            if (this.terminate) {
                this.terminate = false;
                break;
            }
            solution = this.ddfs.search(node, depth);
            depth += searchDepthIncrement;
            timeIsUp = System.currentTimeMillis() - startTime >= timeoutMs;
        }

        this.isSearchingForSolution = false;

        return solution;
    }

    @Override
    public void terminate() {
        if (this.isSearchingForSolution) {
            // terminate the active DDFS if it's running and also terminate our search loop to ensure another DDFS isn't run
            if (this.ddfs != null) {
                this.ddfs.terminate();
            }
            this.terminate = true;
        }
    }
}
