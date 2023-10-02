// Avromi Schneierson - 6.4.2023

import Classes.PuzzleSearchResult;
import Classes.SlidingPuzzleBoard;
import Classes.PuzzleSearch;
import Classes.SearchMethods.*;

import java.util.*;

/**
 * Runs the board solver. Gets the board as input and displays the times and results for each algorithm.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the board pieces in order separated by dashes in order from top-left to bottom-right (put a space \" \" for the blank piece): ");
        String boardInput = scanner.nextLine();
        // Replace the space character with the number for the empty piece (the number of pieces on the board).
        String boardStr = boardInput.replaceFirst(" ", String.valueOf(boardInput.split("-").length));
        SlidingPuzzleBoard board = new SlidingPuzzleBoard(boardStr, null, 0, -1);

        ArrayList<PuzzleSearch> searchTypes = new ArrayList<>();
        System.out.print("Enter the algorithm to run: A - All, AS - A* (A star), B - Best First, BFS - BFS, D - DFS, DD - DDFS, I - IDDFS: ");
        String algorithmsToRun = scanner.nextLine().toUpperCase();
        switch (algorithmsToRun) {
            case "A" ->
                    searchTypes.addAll(Arrays.asList(new BestFirst(), new AStar(), new BFS(), new IDDFS(), new DDFS(), new DFS()));
            case "AS" -> searchTypes.add(new AStar());
            case "B" -> searchTypes.add(new BestFirst());
            case "BFS" -> searchTypes.add(new BFS());
            case "D" -> searchTypes.add(new DFS());
            case "DD" -> searchTypes.add(new DDFS());
            case "I" -> searchTypes.add(new IDDFS());
            default -> System.out.println("Invalid input. Please try again.");
        }

        LinkedHashMap<String, Long> runTimes = new LinkedHashMap<>();
        LinkedHashMap<String, PuzzleSearchResult> solutions = new LinkedHashMap<>();
        for (PuzzleSearch searchType : searchTypes) {
            long startTime = System.currentTimeMillis();
            PuzzleSearchResult solution;
            System.out.print(searchType + " search:\n");
            solution = searchType.search(board);

            System.out.println("\t" + searchType + ":\n\t\t" + (solution.solutionWasFound() ? solution.getFullSolutionPath() : "no solution found"));
            long runTime = System.currentTimeMillis() - startTime;
            System.out.println("\n" + searchType + " search took " + runTime + "ms to run.\n");
            System.out.println("Solution:\n\t" + solution.getCondensedSolutionPath() + "\n");

            runTimes.put(searchType.toString(), runTime);
            solutions.put(searchType.toString(), solution);
        }

        System.out.println("Solutions:");
        for (String searchType : solutions.keySet()) {
            System.out.println("\t" + searchType + ":\n\t\t" + (solutions.get(searchType).solutionWasFound() ?
                    solutions.get(searchType).getCondensedSolutionPath() : "no solution found"));
        }

        System.out.println("\n---------------------------\n");
        System.out.println("Execution times:");
        for (String searchType : runTimes.keySet()) {
            System.out.println("\t" + searchType + ": " + runTimes.get(searchType) + "ms");
        }
    }
}