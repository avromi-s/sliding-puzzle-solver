package Classes.SearchMethods;
import Classes.PuzzleSearchResult;
import Interfaces.INode;
import Classes.PuzzleSearch;

import java.util.HashSet;
import java.util.Stack;

/**
 * Facilitates search with depth-bounded DFS.
 * */
public class DDFS extends PuzzleSearch {

    /**
     * The maximum depth to search until. Once this depth is hit, the search terminates.
     * */
    private final int MAX_DEPTH = 10;

    /**
     * Search for a solution to the given node using DDFS
     *
     * @return the solution found, if applicable
     * */
    @Override
    public PuzzleSearchResult search(INode node) {
        return search(node, MAX_DEPTH);
    }


    /**
     * Search for a solution using the DDFS algorithm. DDFS performs DFS search up until the set maximum depth, terminating
     * with no solution if it is not found by the set depth.
     * @param node the node to find the solution for
     * @return the PuzzleSolution found
     * */
    public static PuzzleSearchResult search(INode node, int maxDepth) {
        INode solution = findSolution(node, maxDepth);

        if (solution != null) {
            return new PuzzleSearchResult(solution, true);
        } else {
            PuzzleSearchResult puzzleSearchResult = new PuzzleSearchResult();
            puzzleSearchResult.setSolutionWasFound(false);
            return puzzleSearchResult;
        }
    }

    /**
     * Find a solution from the given node, up until the provided max depth, and return the solution if found.
     *
     * @param node the node to search from
     * @param maxDepth the maximum depth to search until
     * @return the solution node if found; otherwise null
     * */
    public static INode findSolution(INode node, int maxDepth) {
        Stack<INode> stack = new Stack<>();
        int depth;
        stack.push(node);
        HashSet<Integer> colored = new HashSet<>();
        colored.add(node.hashCode());

        // Check if the given node is a solution before searching
        if (node.isSolution()) {
            return node;
        } else {
            while (!stack.isEmpty()) {
                // Otherwise, search for a solution
                INode curr = stack.pop();
                depth = curr.getLevel();
                if (depth < maxDepth) { // *** Check that we are not past the max depth before we evaluate the children.
                    for (INode child : curr.getNextNodes()) {
                        if (child.isSolution()) {
                            return child;
                        }
                        if (!colored.contains(child.hashCode())) {
                            stack.push(child);
                            colored.add(child.hashCode());
                        }
                    }
                }
            }
        }
        return null;
    }
}
