// Avromi Schneierson - 6.4.2023
package Classes;
import Classes.PuzzleSearchResult;
import Interfaces.INode;

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
    public abstract PuzzleSearchResult search(INode node);

    /**
     * @return the class name of the search type
     * */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}