package com.tictacgo.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewManager;
import android.view.animation.LinearInterpolator;

/**
 * The Board class represents a single instance of a game of TicTacGo. It contains information
 * pertaining to a specific game instance, such as a list of the Pieces in play and whose turn
 * it is.
 */
public class Board {

    private static final String TURN_KEY = "boardTurn";
    private static final String START_TURN_KEY = "boardStartTurn";
    private static final String PIECES_ROW_KEY = "piecesRow";
    private static final String PIECES_COLUMN_KEY = "piecesColumn";
    private static final String PIECES_DIR_HORIZ_KEY = "piecesDirHorizontal";
    private static final String PIECES_DIR_VERT_KEY = "piecesDirVertical";
    private static final String PIECES_PLAYER_KEY = "piecesPlayer";

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
     * Load this Board from the given Bundle.
     *
     * @param height The height of the Board.
     * @param context The context of the Board.
     * @param state The Bundle to load state from, created in {@link Board#getBundle()}.
     */
    public Board(int height, Context context, Bundle state) {
        this.context = context;
        this.height = height;

        /**
         * Initializes the spaces ArrayList to all empty Spaces
         */
        spaces = new ArrayList<>(SIDE_LENGTH);

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
        turn = ((Player) state.getSerializable(TURN_KEY));
        startTurn = ((Player) state.getSerializable(START_TURN_KEY)); //To decide when to move the Pieces

        // Load the Pieces
        pieces = new ArrayList<>(SIDE_LENGTH * SIDE_LENGTH * 2);

        int[] columns = state.getIntArray(PIECES_COLUMN_KEY);
        int[] rows = state.getIntArray(PIECES_ROW_KEY);
        int[] dirHoriz = state.getIntArray(PIECES_DIR_HORIZ_KEY);
        int[] dirVert = state.getIntArray(PIECES_DIR_VERT_KEY);
        String[] players = state.getStringArray(PIECES_PLAYER_KEY);

        for (int i = 0; i < columns.length; i++) {
            Piece piece = new Piece(rows[i], columns[i], dirVert[i], dirHoriz[i],
                    players[i].equals("X") ? Player.X : Player.O, height / 3, context);

            pieces.add(piece);
            getSpace(rows[i], columns[i]).addPiece(piece);
        }
    }

    /**
     * Create and return a Bundle which can be used to restore state after app reload.
     *
     * @return A Bundle which can be used to restore state, with the
     * {@link Board#Board(int, Context, Bundle)} constructor.
     */
    public Bundle getBundle() {
        Bundle state = new Bundle();

        state.putSerializable(TURN_KEY, turn);
        state.putSerializable(START_TURN_KEY, startTurn);

        int[] columns = new int[pieces.size()];
        int[] rows = new int[pieces.size()];
        int[] dirHoriz = new int[pieces.size()];
        int[] dirVert = new int[pieces.size()];
        String[] players = new String[pieces.size()];

        for (int i = 0; i < pieces.size(); i++) {
            Piece piece = pieces.get(i);

            columns[i] = piece.getColumn();
            rows[i] = piece.getRow();
            dirHoriz[i] = piece.getHorizontalDirection();
            dirVert[i] = piece.getVerticalDirection();
            players[i] = piece.getPlayer() == Player.X ? "X" : "O";
        }

        state.putIntArray(PIECES_COLUMN_KEY, columns);
        state.putIntArray(PIECES_ROW_KEY, rows);
        state.putIntArray(PIECES_DIR_HORIZ_KEY, dirHoriz);
        state.putIntArray(PIECES_DIR_VERT_KEY, dirVert);
        state.putStringArray(PIECES_PLAYER_KEY, players);

        return state;
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
     */
    public void updatePositionsNoCollisions() {
        for (Piece piece : pieces) {
            spaces.get(piece.getRow()).get(piece.getColumn()).removePiece(piece);
            piece.updatePositionNoCollision();
            spaces.get(piece.getRow()).get(piece.getColumn()).addPiece(piece);
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
    public void resolveHalfwayCollisions() {
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

    public void resolveFullCollisions() {
        for (List<Space> row : spaces) {
            for (Space space : row) {
                if (space.collisionOccurred()) {
                    resolveCollision(space.getPieces());
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

        piece.setVisibility(View.GONE);
        for (Piece dummy : piece.getDummies()) {
            dummy.setVisibility(View.GONE);
        }
    }

    /**
     * Get a List of the dummy Pieces for each active Piece. A Piece's dummy
     * Pieces are initially placed outside of the screen, but are animated
     * identically to the normal Pieces. Thus, wrap-around animations work.
     *
     * @return A List of the dummy Pieces for each active Piece. If no Pieces
     * will wrap around, the returned List will be empty.
     */
    public List<Piece> getDummyPieces() {
        List<Piece> dummies = new ArrayList<>();

        for (Piece piece : pieces) {
            piece.updateDummyPieces();
            dummies.addAll(piece.getDummies());
        }

        return dummies;
    }

    /**
     * Update and get the halfway animator for the active Pieces.
     *
     * @return The halfway animator for the active Pieces.
     */
    public Animator getHalfwayAnimator() {
        AnimatorSet animator = new AnimatorSet();

        List<Animator> pieceAnimators = new ArrayList<>(pieces.size());
        for (Piece piece : pieces) {
            piece.updateHalfwayAnimator();
            piece.updateImageResourceFullPiece();
            pieceAnimators.add(piece.getHalfwayAnimator());
        }

        animator.playTogether(pieceAnimators);
        animator.setDuration(500);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }

    /**
     * Sets the next Piece Location as the space row, column
     *
     * @param row is the top to bottom index.
     * @param column is the right to left index.
     */
    public void makePiece(int row, int column) {
        this.row = row;
        this.column = column;
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

    /**
     * Set the height of this board to the given value. Also pass this through to the Pieces.
     *
     * @param height The new height of this board.
     */
    public void setHeight(int height) {
        this.height = height;

        for (Piece piece : pieces) {
            piece.setBoardHeight(height);
        }
    }

    public Context getContext() {
        return context;
    }
}
