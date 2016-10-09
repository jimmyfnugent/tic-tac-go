package com.tictacgo.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewManager;

/**
 * The Board class represents a single instance of a game of TicTacGo. It contains information
 * pertaining to a specific game instance, such as a list of the Pieces in play and whose turn
 * it is.
 */
public class Board {

    public enum Player {
        X,
        O
    }

    /**
     * The number of spaces per side of the game board.
     */
    public static final int SIDE_LENGTH = 3;

    /**
     * The player who currently has their turn.
     */
    private Player turn;

    /**
     * A List of the Spaces currently on the board.
     */
    private List<List<Space>> spaces;

    /**
     * A List of the Pieces currently on the board.
     */
    private List<Piece> pieces;

    /**
     * The player who goes first.
     * Used to decide when to move the Pieces
     */
    private Player startTurn;

    /**
     * The X (Vertical) position of the next piece to be added
     */
    private int row;

    /**
     * The Y (Horizontal) position of the next piece to be added
     */
    private int column;

    /**
     * The height of the board, in pixels.
     */
    private int height;

    /**
     * The Context of the board. Used to create Pieces.
     */
    private Context context;

    /**
     * Constructor
     *
     * @param startingPlayer The player to start. If null, it is chosen randomly.
     *
     * @param height the height of the Board, in pixels.
     *
     * @param c the Context. Used when creating an ImageView.
     */
    public Board(Player startingPlayer, int height, Context c) {
        context = c;
        this.height = height;

        /**
         * Initializes the spaces ArrayList to all empty Spaces
         */
        spaces = new ArrayList<>(SIDE_LENGTH);
        pieces = new ArrayList<>(SIDE_LENGTH * SIDE_LENGTH * 2);

        for (int i = 0; i < SIDE_LENGTH; i++) {
            List<Space> row = new ArrayList<>(SIDE_LENGTH);

            for (int j = 0; j < SIDE_LENGTH; j++) {
                row.add(new Space());
            }

            for (int j = 0; j < SIDE_LENGTH; j++) {
                row.add(new Space());
            }

            spaces.add(row);
        }

        /**
         * Sets up turn
         */
        turn = startingPlayer;
        if (turn == null) { //Random first turn
            if (Math.random() < .5)
                turn = Player.O; //O goes first
            else
                turn = Player.X; //X goes first
        }
        startTurn = turn; //To decide when to move the Pieces
    }

    /**
     * Create a new Board object that is a clone of the Board b
     *
     * @param b The Board object to clone
     */
    public Board(Board b) {
        turn = b.getTurn();
        startTurn = b.getStartTurn();
        context = b.getContext();
        height = b.getHeight();

        // Clones the spaces and pieces lists
        spaces = new ArrayList<>(SIDE_LENGTH);
        pieces = new ArrayList<>(SIDE_LENGTH * SIDE_LENGTH * 2);
        for (int row = 0; row < SIDE_LENGTH; row++) {
            spaces.add(new ArrayList<Space>(SIDE_LENGTH));
            for (int column = 0; column < SIDE_LENGTH; column++) {
                spaces.get(row).add(b.getSpace(row, column).copy());
                pieces.addAll(spaces.get(row).get(column).getPieces());
            }
        }
    }

    /**
     * Creates a deep copy of the Board Object
     *
     * @return A deep copy of this Board object
     */
    public Board copy() {
        return new Board(this);
    }

    /**
     * Tests if the board is full
     *
     * @return True if the board is full, false otherwise
     */
    public boolean isFull() {
        for (int i = 0; i < SIDE_LENGTH; i++) {
            for (int j = 0; j < SIDE_LENGTH; j++) {
                if (spaces.get(i).get(j).isEmpty()) { //There is an empty slot
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Figures out if there is a winner of the game
     *
     * @return a count of how many winning combinations each player has.
     */
    public Map<Player, Integer> getWinners() {
        /**
         * A map of Players to the count of their winning combinations.
         */
        Map<Player, Integer> winners = new HashMap<>();
        winners.put(Player.X, 0);
        winners.put(Player.O, 0);

        // Sum all rows, columns, and diagonals, looking for all Xs or all Os.
        // Rows
        for (List<Space> row : spaces) {
            mergeWinners(getWinners(row), winners);
        }

        // Columns
        for (int col = 0; col < SIDE_LENGTH; col++) {
            ArrayList<Space> columnSpaces = new ArrayList<>(SIDE_LENGTH);

            for (List<Space> row : spaces) {
              columnSpaces.add(row.get(col));
            }

            mergeWinners(getWinners(columnSpaces), winners);
        }

        // Top left to bottom right diagonal
        int row = 0;
        int col = 0;
        ArrayList<Space> diagSpaces = new ArrayList<>(SIDE_LENGTH);
        while (row < SIDE_LENGTH && col < SIDE_LENGTH) {
            diagSpaces.add(spaces.get(row).get(col));
            row++;
            col++;
        }

        mergeWinners(getWinners(diagSpaces), winners);

        // Top right to bottom left diagonal
        diagSpaces.clear();
        row = 0;
        col = SIDE_LENGTH - 1;
        while (row < SIDE_LENGTH && col >= 0) {
            diagSpaces.add(spaces.get(row).get(col));
            row++;
            col--;
        }

        mergeWinners(getWinners(diagSpaces), winners);

        return winners;
    }

    /**
     * Figures out if there is a winner in the given Spaces. A winner must be in every single Space
     * to win.
     *
     * @param toCheck A List of the Spaces to check for a winner. This should be an entire row,
     *                column, or diagonal of Spaces.
     * @return A Set of the Players who won in this List of Spaces.
     */
    private Set<Player> getWinners(List<Space> toCheck) {
        // TODO: This allows spaces with multiple Pieces in them to still contribute to a win.
        // We don't need to map to a count here because a Player may only win once per row, column,
        // or diagonal.
        Set<Player> winners = new HashSet<>(2);

        boolean xWins = true;
        boolean oWins = true;

        for (Space space : toCheck) {
          if (!space.hasX()) {
            xWins = false;
          }

          if (!space.hasO()) {
            oWins = false;
          }

          if (!xWins && !oWins) {
            return winners;
          }
        }

        if (xWins) {
          winners.add(Player.X);
        }

        if (oWins) {
          winners.add(Player.O);
        }

        return winners;
    }

    /**
     * Merges the winners from an individual row, column, or diagonal with the running sum of
     * overall winners.
     *
     * This method modifies its input parameter sum.
     *
     * @param winners The set of winners from an individual row, column, or diagonal.
     * @param sum The running total of wins for each Player.
     */
    private void mergeWinners(Set<Player> winners, Map<Player, Integer> sum) {
        if (winners.contains(Player.X)) {
            sum.put(Player.X, sum.get(Player.X) + 1);
        }

        if (winners.contains(Player.O)) {
            sum.put(Player.O, sum.get(Player.O) + 1);
        }
    }

    /**
     * Method newPiece Adds a new Piece to the Board
     *
     * @param dirVertical The X direction of the Piece.
     * @param dirHorizontal The Y direction of the Piece.
     * @return The new Piece
     */
    public View newPiece(int dirVertical, int dirHorizontal) {
        Piece p = new Piece(row, column, dirVertical, dirHorizontal, turn, height / 3, context);
        spaces.get(row).get(column).addPiece(p);
        pieces.add(p);
        return p;
    }

    /**
     * Returns whether or not the Pieces will move at the end of the turn
     *
     * @return A boolean value of whether the Pieces will move after the current turn
     */
    public boolean willMove() {
        return (turn != startTurn); //Move whenever the next turn is the startTurn
    }

    /**
     * Goes to the next turn by flipping the value of turn.
     */
    public void nextTurn() {
        if (turn == Player.O) {
            turn = Player.X;
        } else {
            turn = Player.O;
        }
    }


    /**
     * Updates the positions of the Pieces and wraps around out of bounds Pieces.
     *
     * This method handles collisions as well.
     */
    public void updatePositions() {
        for (Piece piece : pieces) {
            spaces.get(piece.getRow()).get(piece.getColumn()).removePiece(piece);
            piece.updatePositionNoCollision();
            spaces.get(piece.getRow()).get(piece.getColumn()).addPiece(piece);
        }

        // halfway collisions
        resolveHalfwayCollisions();

        // Full collisions
        for (List<Space> row : spaces) {
            for (Space space : row) {
                if (space.collisionOccurred()) {
                    resolveCollision(space.getPieces());
                }
            }
        }
    }

    /**
     * Resolves all halfway collisions, ie. when two or more Pieces meet in between squares.
     *
     * There are three cases:
     *  Their Y's cross
     *  Their X's cross
     *  Both cross
     */
    private void resolveHalfwayCollisions() {
        for (int i = 0; i < pieces.size() - 1; i++) {
            List<Piece> collision = new ArrayList<>(4);
            collision.add(pieces.get(i));
            for (int j = i + 1; j < pieces.size(); j++) { //For every Piece after the current one.
                /**
                 * X values are the same.
                 * Y values cross.
                 */
                if ((pieces.get(i).getLastRow() == pieces.get(j).getLastRow() &&
                     pieces.get(i).getRow() == pieces.get(j).getRow() &&
                     pieces.get(i).getLastColumn() == pieces.get(j).getColumn() &&
                     pieces.get(i).getColumn() == pieces.get(j).getLastColumn()) ||

                /**
                 * X values cross.
                 * Y values are the same.
                 */
                    (pieces.get(i).getLastRow() == pieces.get(j).getRow() &&
                     pieces.get(i).getRow() == pieces.get(j).getLastRow() &&
                     pieces.get(i).getLastColumn() == pieces.get(j).getLastColumn() &&
                     pieces.get(i).getColumn() == pieces.get(j).getColumn()) ||

                /**
                 * X values cross.
                 * Y values cross.
                 */
                    (pieces.get(i).getLastRow() == pieces.get(j).getRow() &&
                     pieces.get(i).getRow() == pieces.get(j).getLastRow() &&
                     pieces.get(i).getLastColumn() == pieces.get(j).getColumn() &&
                     pieces.get(i).getColumn() == pieces.get(j).getLastColumn())) {
                        collision.add(pieces.get(j)); //A collision occurred
                }
            }

            if (collision.size() > 1) { // Collision occurred
                if (resolveCollision(collision)) {
                    i--;
                }
            }
        }
    }

    /**
     * Resolve the given collision. If 2 Pieces collide, this requires swapping their Player value.
     * If 3 or more Pieces collide, they should explode and be removed.
     *
     * @param collision A List of the Pieces which caused a single collision.
     *
     * @return True if we had to remove any Pieces. False otherwise.
     */
    private boolean resolveCollision(List<Piece> collision) {
        if (collision.size() == 2) {
            Player temp = collision.get(0).getPlayer();
            collision.get(0).setPlayer(collision.get(1).getPlayer());
            collision.get(1).setPlayer(temp);

        } else if (collision.size() > 2) {
            // Foreach here creates concurrent mod exception.
            for (int i = collision.size() - 1; i >= 0; i--) {
                removePiece(collision.get(i));
            }
            return true;
        }

        return false;
    }

    /**
     * Removes a Piece from the pieces ArrayList.
     * Called from the collision resolution in TicTacGoMenu.java
     *
     * @param piece The Piece to be removed
     */
    public void removePiece(Piece piece) {
        spaces.get(piece.getRow()).get(piece.getColumn()).removePiece(piece);
        pieces.remove(piece);

        ((ViewManager)piece.getParent()).removeView(piece);
    }

    public void updateUiPositions() {
        // Iterate through the board and update each piece's UI position.
        for (List<Space> row : spaces) {
            for (Space space : row) {
                space.updateUiPosition();
            }
        }
    }

    /**
     * Sets the next Piece Location as the space row, column
     * row is the top to bottom index.
     * column is the right to left index.
     *
     * @param gravity The Gravity of the Space clicked
     */
    public void makePiece(int gravity) {
        switch (gravity) {
            case Gravity.TOP | Gravity.LEFT:
                row = 0;
                column = 0;
                break;
            case Gravity.TOP | Gravity.CENTER_HORIZONTAL:
                row = 0;
                column = 1;
                break;
            case Gravity.TOP | Gravity.RIGHT:
                row = 0;
                column = 2;
                break;
            case Gravity.CENTER_VERTICAL | Gravity.LEFT:
                row = 1;
                column = 0;
                break;
            case Gravity.CENTER_VERTICAL | Gravity.RIGHT:
                row = 1;
                column = 2;
                break;
            case Gravity.BOTTOM | Gravity.LEFT:
                row = 2;
                column = 0;
                break;
            case Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL:
                row = 2;
                column = 1;
                break;
            case Gravity.BOTTOM | Gravity.RIGHT:
                row = 2;
                column = 2;
                break;
            default: //Center
                row = 1;
                column = 1;
        }
    }

    /**
     * Returns the Space at index row, column
     *
     * @param row the row
     * @param column the column
     * @return The Space at index row, column
     */
    public Space getSpace(int row, int column) {
        return spaces.get(row).get(column);
    }

    public Player getStartTurn() {
        return startTurn;
    }

    public Player getTurn() {
        return turn;
    }

    public int getHeight() {
        return height;
    }

    public Context getContext() {
        return context;
    }
}
