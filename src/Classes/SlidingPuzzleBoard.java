// Avromi Schneierson - 6.4.2023
package Classes;

import Interfaces.INode;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the state of a sliding-puzzle board. It implements the Interfaces.INode interface for use in searching for
 * solutions.
 */
public class SlidingPuzzleBoard implements INode {
    /**
     * The number of rows/columns in the board. For a 4x4 board this is 4.
     */
    private final int BOARD_SIZE;

    /**
     * The board pieces.
     */
    private final int[][] board;

    /**
     * The number used to represent the empty space on the board. This is represented by the next number on the board
     * internally. Meaning on a 4x4 board, 16 represents the empty space.
     */
    private final int emptyPieceNumber;

    /**
     * The position of the empty piece in the board. The first number is the vertical position, second is horizontal.
     * This is saved for when calculating the next move, as all next moves must swap with the empty piece.
     */
    private int[] emptyPieceIndices;

    /**
     * The level at which this node was encountered
     */
    private final int level;

    /**
     * The parent node of this node.
     */
    private final SlidingPuzzleBoard parent;

    /**
     * A string representing the state of the board, encoded as all pieces in order from top-left to bottom-right,
     * separated by dashes "-".
     */
    private final String puzzleId;

    /**
     * The piece the parent moved to get to this board
     */
    private final int movedPiece;

    /**
     * The position of the piece moved in the board. The first number is the vertical position, second is horizontal.
     */
    private int[] movedPieceIndices = new int[]{-1, -1};

    /**
     * Construct a new board.
     *
     * @param puzzleId   the string representation of the board
     * @param parent     the board that led to this board
     * @param level      the level that this board was encountered at
     * @param movedPiece the piece moved to get to this board
     */
    public SlidingPuzzleBoard(String puzzleId, SlidingPuzzleBoard parent, int level, int movedPiece) {
        // all fields based on BOARD_SIZE must be set BEFORE setPiecePositions() is called since that function uses those items.
        this.BOARD_SIZE = (int) Math.sqrt(puzzleId.split("-").length);
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        this.emptyPieceNumber = BOARD_SIZE * BOARD_SIZE;
        this.puzzleId = puzzleId;
        this.parent = parent;
        this.level = level;
        this.movedPiece = movedPiece;

        setPiecePositions(puzzleId);
    }

    /**
     * Set up the board based on the string representation of the board.
     * E.g., the string: "1-2-3-4-5-6-7-8-9-10-11-12-13-14-15-16" is a fully solved 4x4 board.
     *
     * @param numbersInOrder the board numbers, in order
     */
    private void setPiecePositions(String numbersInOrder) {
        String[] pieces = numbersInOrder.split("-");
        for (int i = 0; i < pieces.length; i++) {
            // Save the position of the blank or moved pieces for future use.
            if (pieces[i].equals(String.valueOf(emptyPieceNumber))) {
                emptyPieceIndices = new int[]{i / BOARD_SIZE, i % BOARD_SIZE};
            } else if (pieces[i].equals(String.valueOf(movedPiece))) {
                movedPieceIndices = new int[]{i / BOARD_SIZE, i % BOARD_SIZE};
            }
            board[i / BOARD_SIZE][i % BOARD_SIZE] = Integer.parseInt(pieces[i]);
        }
    }

    /**
     * @return a list of the next board nodes. Meaning, it returns all possible boards that can result from
     * 1 move forward from this position.
     */
    @Override
    public List<INode> getNextNodes() {
        List<INode> nodes = new ArrayList<>();  // nodes to return. Will have max size of 4, minimum size of 2.

        int blankPieceVerticalIndex = emptyPieceIndices[0];
        int blankPieceHorizontalIndex = emptyPieceIndices[1];

        // The directions of all possible moves from the given board by one unit, namely: up, right, down, and left.
        // The first item in the directions array is the vertical differential, and the second is the horizontal.
        int[][] directions = new int[][]{
                {-1, 0},
                {0, 1},
                {1, 0},
                {0, -1}
        };

        // For each direction, construct a new Classes.SlidingPuzzleBoard with the new position, and add it to the List of nodes.
        for (int[] direction : directions) {
            int pieceToSwapsVerticalIndex = blankPieceVerticalIndex + direction[0];
            int pieceToSwapsHorizontalIndex = blankPieceHorizontalIndex + direction[1];

            // The move is invalid if either of the resulting indices is negative, or if either index is greater than
            // the board length/width.
            boolean moveIsWithinBounds = !(pieceToSwapsVerticalIndex < 0 || pieceToSwapsHorizontalIndex < 0 ||
                    pieceToSwapsVerticalIndex >= BOARD_SIZE || pieceToSwapsHorizontalIndex >= BOARD_SIZE);
            if (moveIsWithinBounds) {
                // Copy the board with the pieces swapped
                int[][] boardCopy = new int[board.length][];
                for (int row = 0; row < board.length; row++) {
                    boardCopy[row] = board[row].clone();
                }
                int pieceToSwap = boardCopy[pieceToSwapsVerticalIndex][pieceToSwapsHorizontalIndex];
                boardCopy[pieceToSwapsVerticalIndex][pieceToSwapsHorizontalIndex] = emptyPieceNumber;
                boardCopy[blankPieceVerticalIndex][blankPieceHorizontalIndex] = pieceToSwap;

                // After we've swapped the pieces. Compile all the pieces in order (top-left to bottom-right), so that we
                // can create a new Node.
                String[] allPiecesInOrder = new String[boardCopy.length * BOARD_SIZE];
                for (int row = 0; row < boardCopy.length; row++) {
                    for (int piece = 0; piece < boardCopy[row].length; piece++) {
                        allPiecesInOrder[(row * BOARD_SIZE) + piece] = String.valueOf(boardCopy[row][piece]);
                    }
                }
                String stringRepresentation = String.join("-", allPiecesInOrder);
                nodes.add(new SlidingPuzzleBoard(stringRepresentation, this, level + 1, pieceToSwap));
            }
        }
        return nodes;
    }

    /**
     * Calculate and return the manhattan distance of a given piece, which is its distance from where it should be if
     * the board were fully solved.
     * E.g., for a board where everything is solved but 1 is switched with 2's position, 1 and 2 will each have a
     * manhattanDistance of 1 and the total manhattan distance of the board would be 2.
     *
     * @param verticalIndex   the row index, starting from the top, of the piece to process. 0 indexed.
     * @param horizontalIndex the column index, starting from the left, of the piece to process. 0 indexed.
     * @return The 'manhattanDistance' of a piece.
     */
    private int manhattanDistance(int verticalIndex, int horizontalIndex) {
        int pieceNumber = board[verticalIndex][horizontalIndex];
        // Remove 1 because we are working with 0-indexed arrays, whereas the piece numbers start from 1
        pieceNumber--;
        int correctVerticalIndex = pieceNumber / BOARD_SIZE;
        int correctHorizontalIndex = pieceNumber % BOARD_SIZE;

        // Get the vertical and horizontal distance for the piece from its desired location. Get the positive distance if
        // its distance is negative because it is ahead of where it should be.
        int distanceVertical = (verticalIndex - correctVerticalIndex) < 0 ? -(verticalIndex - correctVerticalIndex) :
                (verticalIndex - correctVerticalIndex);
        int distanceHorizontal = (horizontalIndex - correctHorizontalIndex) < 0 ? -(horizontalIndex - correctHorizontalIndex) :
                (horizontalIndex - correctHorizontalIndex);
        return distanceVertical + distanceHorizontal;
    }

    @Override
    public String toString() {
        final int NUM_CHARS_BETWEEN_PIECES = 3;
        final int NUM_CHARS_PER_PIECE = String.valueOf(emptyPieceNumber).length(); // The max size of any piece. Smaller pieces will be padded to keep everything aligned.
        final int NUM_CHARS_PER_ROW = (NUM_CHARS_PER_PIECE + NUM_CHARS_BETWEEN_PIECES) * BOARD_SIZE + 1;

        StringBuilder sb = new StringBuilder();
        sb.append("-".repeat(NUM_CHARS_PER_ROW)).append("\n");
        for (int[] row : board) {
            for (int piece : row) {
                // The blank piece is represented by the last number internally, so convert it to a blank space for viewing.
                String pieceStr = piece == BOARD_SIZE * BOARD_SIZE ? " " : String.valueOf(piece);
                int numCharsPadding = NUM_CHARS_PER_PIECE - pieceStr.length();

                // Add the padding with half on either side. Adding 1 for the minimum spacing.
                // All pieces have a minimum of 1 space on either side and the "|" character for a total min of 3 characters.
                int numCharsPaddingLeft = (numCharsPadding / 2);
                int numCharsPaddingRight = numCharsPadding - numCharsPaddingLeft;  // any remaining padding

                sb.append("|").append(" ".repeat(numCharsPaddingLeft + 1)).append(pieceStr).
                        append(" ".repeat(numCharsPaddingRight + 1));

            }
            sb.append("|\n");
            sb.append("-".repeat(NUM_CHARS_PER_ROW)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Boards are equal if they have the same hashcode, which is if all pieces are in the same order.
     */
    @Override
    public boolean equals(Object puzzle) {
        if (puzzle.getClass() == SlidingPuzzleBoard.class) {
            return puzzle.hashCode() == this.hashCode();
        }
        return false;
    }

    /**
     * @return the level that this board was encountered at.
     */
    @Override
    public int getLevel() {
        return level;
    }


    /**
     * @return the parent board, i.e., the board that after 1 move, led to this board.
     */
    @Override
    public INode getParent() {
        // Parent is a SlidingPuzzleMove but that implements Interfaces.INode, so it can be returned polymorphically.
        return parent;
    }


    /**
     * @return <code>true</code> if this node is a solved board; otherwise <code>false</code>
     */
    @Override
    public boolean isSolution() {
        return getHValue() == 0;
    }


    /**
     * Returns the total estimated cost of using this node to get to the solution, which is the level (G-value) + the
     * estimated cost of getting to the solution (H-value).
     *
     * @return the total estimated cost of using this node to get to the solution.
     */
    public int getFValue() {
        return getGValue() + getHValue();
    }


    /**
     * Returns the minimum number of moves REMAINING to get from this node to a solution. This is the sum of the
     * manhattan distance for all pieces on the board.
     *
     * @return the minimum number of moves REMAINING to get from this node to a solution.
     */
    @Override
    public int getHValue() {
        int sum = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                sum += manhattanDistance(i, j);
            }
        }
        return sum;
    }


    /**
     * @return the actual cost (number of moves) used to get to this node, which is the level here.
     */
    @Override
    public int getGValue() {
        return getLevel();
    }


    /**
     * The hashcode is solely based on the layout of the pieces on the board, as boards are only considered unique by
     * the layout of their pieces so that search can ignore duplicate positions already evaluated.
     */
    @Override
    public int hashCode() {
        return puzzleId.hashCode();
    }

    /**
     * @return the last piece moved to get to this board.
     */
    public String getMovedPiece() {
        return String.valueOf(movedPiece);
    }

}
