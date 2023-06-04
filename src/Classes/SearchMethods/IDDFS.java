// Avromi Schneierson - 6.4.2023
package Classes.SearchMethods;
import Interfaces.INode;
import Interfaces.IPuzzleSearch;
import Classes.PuzzleSolution;

/**
 * Facilitates Iterative deepening depth-first search on a puzzle.
 * */
public class IDDFS extends IPuzzleSearch {
    /**
     * The initial depth to start with
     * */
    public final int INITIAL_DEPTH = 20;

    /**
     * The amount to increment the depth by when a search fails to find a result
     * */
    public final int INCREMENT = 5;

    /**
     * Search for a solution using IDDFS. IDDFS performs depth bounded DFS search (DDFS), but repeatedly increments the
     * max depth if a solution isn't found.
     * @param node the node to find the solution for
     * @return the PuzzleSolution found
     * */
    @Override
    public PuzzleSolution search(INode node) {
        PuzzleSolution solution = new PuzzleSolution();
        solution.setFoundSolution(false);
        // Run until a solution is found. There is no clause to break out of the loop without a solution.
        for (int depth = INITIAL_DEPTH; !solution.getFoundSolution(); depth += INCREMENT) {
            solution = DDFS.search(node, depth);
        }
        return solution;
    }
}