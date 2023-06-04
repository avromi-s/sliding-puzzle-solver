// Avromi Schneierson - 6.4.2023
package Classes;

/**
 * Represents a solution to a puzzle. This is returned from a search method.
 * */
public class PuzzleSolution {
    /**
     * Indicate if a solution was found
     * */
    private boolean foundSolution = false;

    /**
     * The path to the solution, condensed into just the move numbers.
     * */
    private String condensedSolutionPath = "";

    /**
     * The full path to the solution with each move displayed as a board.
     * */
    private String fullSolutionPath = "";


    public boolean getFoundSolution() {
        return foundSolution;
    }

    public void setFoundSolution(boolean foundSolution) {
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
