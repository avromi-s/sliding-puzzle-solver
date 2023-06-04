// Avromi Schneierson - 6.4.2023
package Classes.SearchMethods;
import Interfaces.INode;
import Interfaces.IPuzzleSearch;
import Classes.PuzzleSolution;
import java.util.*;

/**
 * Facilitates depth-first search on a puzzle.
 * */
public class DFS extends IPuzzleSearch {

    /**
     * Search for a solution to the given node using DFS
     *
     * @return the solution found, if applicable
     * */
    @Override
    public PuzzleSolution search(INode node) {
        Stack<INode> stack = new Stack<>();
        return search(node, stack);
    }

    /**
     * Search for a solution using the DFS algorithm.
     * @param node the node to find the solution for
     * @param stack the stack to use in the search.
     * @return the PuzzleSolution found
     * */
    public static PuzzleSolution search(INode node, Stack<INode> stack) {
        INode solution = findSolution(node, stack);
        if (solution != null) {
            return getSolutionResults(solution);
        } else {
            PuzzleSolution puzzleSolution = new PuzzleSolution();
            puzzleSolution.setFoundSolution(false);
            return puzzleSolution;
        }
    }

    /**
     * Find a solution from the given node, using the given stack, and return the solution if found.
     *
     * @param node the node to search from
     * @param stack the stack to use in the search
     * @return the solution node if found; otherwise null
     * */
    public static INode findSolution(INode node, Stack<INode> stack) {
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
        return null;
    }
}
