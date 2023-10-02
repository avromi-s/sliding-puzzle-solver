// Avromi Schneierson - 6.4.2023
package Classes;

import Interfaces.INode;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Represents a puzzle search results to be returned from a search method.
 */
public class PuzzleSearchResult {
    /**
     * Indicate if a solution was found
     */
    private boolean foundSolution = false;

    /**
     * The path to the solution, condensed into just the move numbers.
     */
    private String condensedSolutionPath = "";

    /**
     * The full path to the solution with each move displayed as a board.
     */
    private String fullSolutionPath = "";


    /**
     * No-args constructor
     */
    public PuzzleSearchResult() {
    }

    /**
     * Construct a search result object. Store the condensed and full paths of the given node solution if a solution was
     * found.
     *
     * @param solution      the solution node
     * @param foundSolution whether a solution was found
     */
    public PuzzleSearchResult(INode solution, boolean foundSolution) {
        this.foundSolution = foundSolution;
        if (foundSolution) {
            ArrayList<INode> solutionPath = new ArrayList<>(solution.getLevel());
            INode curr = solution;
            while (curr != null) {
                solutionPath.add(0, curr);
                curr = curr.getParent();
            }

            StringBuilder fullSolutionPath = new StringBuilder();
            for (INode board : solutionPath) {
                fullSolutionPath.append("\nStep #").append(board.getLevel() + 1).append(":\n").append(board);
            }

            this.fullSolutionPath = fullSolutionPath.toString();
            this.condensedSolutionPath = solutionPath.stream()
                    .map(board -> board.getMovedPiece())
                    .filter(movedPiece -> !movedPiece.equals("-1"))
                    .collect(Collectors.joining(", "));
        }
    }

    public boolean solutionWasFound() {
        return foundSolution;
    }

    public void setSolutionWasFound(boolean foundSolution) {
        this.foundSolution = foundSolution;
    }

    public String getFullSolutionPath() {
        return fullSolutionPath;
    }

    public void setFullSolutionPath(String fullSolutionPath) {
        this.fullSolutionPath = fullSolutionPath;
    }

    public String getCondensedSolutionPath() {
        return condensedSolutionPath;
    }

    public void setCondensedSolutionPath(String condensedSolutionPath) {
        this.condensedSolutionPath = condensedSolutionPath;
    }
}
