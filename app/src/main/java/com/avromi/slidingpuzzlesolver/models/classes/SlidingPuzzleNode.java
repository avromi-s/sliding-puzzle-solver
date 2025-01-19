package com.avromi.slidingpuzzlesolver.models.classes;

import com.avromi.slidingpuzzlesolver.models.interfaces.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * This class represents the state of a sliding-puzzle board. It implements the Interfaces.INode interface for use in searching for
 * solutions.
 */
public class SlidingPuzzleNode implements Node {
    /**
     * The number of rows/columns in the board. For a 4x4 board this is 4.
     */
    private final int BOARD_SIZE;

    /**
     * The board pieces.
     */
    private final int[][] boardPieces;

    /**
     * The number used to represent the empty space on the board. This is represented by the next
     * number on the board internally. For example, on a 4x4 board, 16 represents the empty space.
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
    private final SlidingPuzzleNode parent;

    /**
     * The piece the parent moved to get to this board
     */
    private final int movedPiece;

    /**
     * The position of the piece moved in the board. The first number is the vertical position, second is horizontal.
     */
    private int[] movedPieceIndices = new int[]{-1, -1};

    /**
     * Construct a new initial board
     */
    public SlidingPuzzleNode(int[][] pieces) {
        this(pieces, null, 0, -1);
    }

    /**
     * Construct a new board.
     *
     * @param pieces     the pieces on the board, in order from top-left to bottom-right
     * @param parent     the board that led to this board
     * @param level      the level that this board was encountered at
     * @param movedPiece the piece moved to get to this board
     */
    public SlidingPuzzleNode(int[][] pieces, SlidingPuzzleNode parent, int level, int movedPiece) {
        // all fields based on BOARD_SIZE must be set BEFORE setPiecePositions() is called since that function uses those items.
        this.BOARD_SIZE = pieces.length;
        this.boardPieces = new int[pieces.length][pieces.length];
        this.emptyPieceNumber = this.BOARD_SIZE * this.BOARD_SIZE;
        this.movedPiece = movedPiece;
        this.parent = parent;
        this.level = level;
        setBoardPieces(pieces);
    }

    /**
     * Sets this board's pieces from the given array
     *
     * @throws IllegalArgumentException if the given array does not contain all valid numbers
     * */
    private void setBoardPieces(int[][] pieces) {
        int maxBoardNumber = pieces.length * pieces.length;
        HashSet<Integer> piecesAdded = new HashSet<>(maxBoardNumber);

        for (int i = 0; i < pieces.length; i++) {
            if (pieces[i].length != pieces.length) {  // only allowing square boards for now
                throw new IllegalArgumentException();
            }

            for (int j = 0; j < pieces[i].length; j++) {
                int pieceNumber = pieces[i][j];
                boolean pieceWasAlreadyAdded = !piecesAdded.add(pieces[i][j]);
                if (pieceWasAlreadyAdded || pieceNumber > maxBoardNumber) {
                    throw new IllegalArgumentException();
                }

                // Save the position of the blank or moved pieces for future use.
                if (pieces[i][j] == this.emptyPieceNumber) {
                    this.emptyPieceIndices = new int[]{i, j};
                } else if (pieces[i][j] == movedPiece) {
                    this.movedPieceIndices = new int[]{i, j};
                }
                this.boardPieces[i][j] = pieceNumber;
            }
        }
    }

    /**
     * @return the pieces on this board
     */
    public int[][] getBoardPieces() {
        int[][] result = new int[this.boardPieces.length][this.boardPieces.length];
        for (int i = 0; i < this.boardPieces.length; i++) {
            result[i] = Arrays.copyOf(this.boardPieces[i], this.boardPieces[i].length);
        }
        return result;
    }

    /**
     * @return a list of the next board nodes. Meaning, it returns all possible boards that can result from
     * 1 move forward from this position.
     */
    @Override
    public List<Node> getNextNodes() {
        List<Node> nodes = new ArrayList<>();  // nodes to return. Will have max size of 4, minimum size of 2.

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
                int[][] boardCopy = new int[boardPieces.length][];
                for (int row = 0; row < boardPieces.length; row++) {
                    boardCopy[row] = boardPieces[row].clone();
                }
                int pieceToSwap = boardCopy[pieceToSwapsVerticalIndex][pieceToSwapsHorizontalIndex];
                boardCopy[pieceToSwapsVerticalIndex][pieceToSwapsHorizontalIndex] = emptyPieceNumber;
                boardCopy[blankPieceVerticalIndex][blankPieceHorizontalIndex] = pieceToSwap;

                nodes.add(new SlidingPuzzleNode(boardCopy, this, level + 1, pieceToSwap));
                // todo might be doing double-copy above because constructor copies also
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
        int pieceNumber = boardPieces[verticalIndex][horizontalIndex];
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
    public Node getParent() {
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
        for (int i = 0; i < boardPieces.length; i++) {
            for (int j = 0; j < boardPieces[i].length; j++) {
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
     * @return the last piece moved to get to this board.
     */
    public int getMovedPiece() {
        return movedPiece;
    }

    /**
     * @return the value that represents the empty pieces on this board
     */
    public int getEmptyPieceNumber() {
        return this.emptyPieceNumber;
    }

    public static int getEmptyPieceNumber(int boardSize) {
        return boardSize * boardSize;
    }

    /**
     * Boards are equal if all pieces are in the same order.
     */
    @Override
    public boolean equals(Object board) {
        if (board.getClass() == SlidingPuzzleNode.class) {
            return board.hashCode() == this.hashCode();
        }
        return false;
    }

    /**
     * The hashcode is solely based on the layout of the pieces on the board, as boards are only considered unique by
     * the layout of their pieces so that search can ignore duplicate positions already evaluated.
     */
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        // todo update method to use string.join
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < boardPieces.length; i++) {
            for (int j = 0; j < boardPieces[i].length; j++) {
                result.append(boardPieces[i][j]);

                // Add '-' if it's not the last element
                if (i != boardPieces.length - 1 || j != boardPieces[i].length - 1) {
                    result.append("-");
                }
            }
        }

        return result.toString();
    }
}
