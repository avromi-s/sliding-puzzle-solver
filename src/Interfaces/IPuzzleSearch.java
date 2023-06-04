// Avromi Schneierson - 6.4.2023
package Interfaces;
import Classes.PuzzleSolution;
import java.util.ArrayList;

/**
 * Abstracts a puzzle search type, which all must offer a search method.
 * */
public abstract class IPuzzleSearch {

    /**
     * Search for a solution to the puzzle with the given node.
     *
     * @param node the node to find a solution for
     * @return the solution, if found
     * */
    public abstract PuzzleSolution search(INode node);


    /**
     * Packs and returns a PuzzleSolution object with the condensed and full paths of the given node solution.
     *
     * @param solution the solution node
     * @return the PuzzleSolution object
     * */
    public static PuzzleSolution getSolutionResults(INode solution) {
        ArrayList<INode> solutionPath = new ArrayList<>(solution.getLevel());
        INode curr = solution;
        while (curr != null) {
            solutionPath.add(0, curr);
            curr = curr.getParent();
        }

        StringBuilder condensedPath = new StringBuilder();
        for (INode board : solutionPath) {
            condensedPath.append(board.getPieceMoved()).append(", ");
        }

        StringBuilder fullSolutionPath = new StringBuilder();
        for (INode board : solutionPath) {
            fullSolutionPath.append("Step #").append(board.getLevel() + 1).append(":\n").append(board);
        }

        PuzzleSolution puzzleSolution = new PuzzleSolution();
        puzzleSolution.setFoundSolution(true);
        puzzleSolution.setCondensedSolutionPath(condensedPath.toString());
        puzzleSolution.setFullSolutionPath(fullSolutionPath.toString());
        return puzzleSolution;
    }

    /**
     * @return the class name of the search type
     * */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}