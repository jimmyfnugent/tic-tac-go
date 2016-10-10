package com.tictacgo;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.tictacgo.DirectionPickerFragment.OnDirectionPickedListener;
import com.tictacgo.data.Board;
import com.tictacgo.data.Board.Player;

import java.util.Map;

/**
 * The TicTacGoGameActivity class represents the Activity for when the game board is visible, ie.
 * while the game is actually being played.
 */
public class TicTacGoGameActivity extends Activity implements OnDirectionPickedListener {
    /**
     * The Board of the game
     */
    private Board board;

    /**
     * The FrameLayout in which the game is to be drawn
     */
    private FrameLayout fl;

    /**
     * OnClickListener to use for each piece
     */
    private View.OnClickListener onPieceClicked;

    /**
     * The height of the usable screen area
     */
    private int height;

    /**
     * The initial turn selection. Used for the New Game Button
     */
    private Player turn;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_go_game);

        fragmentManager = getFragmentManager();

        Intent intent = getIntent();

        turn = (Player) intent.getSerializableExtra(TicTacGoMenuActivity.PLAYER_KEY);

        height = intent.getIntExtra(TicTacGoMenuActivity.HEIGHT_KEY, 300);
        board = new Board(turn, height, getBaseContext());

        // Set up the screen
        ((TextView) findViewById(R.id.gamePlayerOneName))
                .setText(intent.getStringExtra(TicTacGoMenuActivity.P1_NAME_KEY));
        ((TextView) findViewById(R.id.gamePlayerTwoName))
                .setText(intent.getStringExtra(TicTacGoMenuActivity.P2_NAME_KEY));
        fl = (FrameLayout) findViewById(R.id.gameBoard);

        // What to do when an empty space is clicked
        onPieceClicked = new View.OnClickListener() {
            public void onClick(View v) {
                // Create new Fragment Transaction and remove all previous DirectionPickers
                fragmentManager.popBackStackImmediate(DirectionPickerFragment.class.getName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Make the new DirectionPicker
                LayoutParams params = (LayoutParams) v.getLayoutParams();
                DirectionPickerFragment directionPicker = DirectionPickerFragment.newInstance(
                        board.getTurn(), params.topMargin * 3 / height,
                        params.leftMargin * 3 / height, height);

                // Add the new DirectionPicker
                fragmentTransaction.add(R.id.gameBoard, directionPicker);
                fragmentTransaction.addToBackStack(DirectionPickerFragment.class.getName());
                fragmentTransaction.commit();
            }
        };

        // Remove and active Direction Pickers whenever we click anywhere
        findViewById(R.id.gameScreen).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate(DirectionPickerFragment.class.getName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        /**
         * Sets the New Game Button to work
         */
        findViewById(R.id.newGameButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                board = new Board(turn, height, getBaseContext());
                updateBoard();
                updateTurnIndicator();
            }
        });

        /**
         * Step 5: Play the game
         */
        updateBoard();
        updateTurnIndicator();
    }

    @Override
    public void onDirectionPicked(int dirVertical, int dirHorizontal, int row, int column) {
        getFragmentManager().popBackStack();

        board.makePiece(row, column);
        fl.addView(board.newPiece(dirVertical, dirHorizontal));

        notifyWinners(board.getWinners());
        if (board.willMove()) {
            // Only move the pieces after both players have moved.
            board.updateUiPositions();
            board.updatePositions();
            updateBoard();
        }
        board.nextTurn();
        updateTurnIndicator();
        updateClearPieces();
    }

    /**
     * Updates the clear Pieces on the FrameLayout
     * We must do this after each time the Pieces move
     */
    private void updateClearPieces() {
        for (int i = 0; i < fl.getChildCount(); i++) {
            if (fl.getChildAt(i).isClickable()) { //Only clear Pieces are clickable
                fl.removeViewAt(i);
                i--; //When we remove a View, every other one goes up one index
            }
        }
        for (int row = 0; row < Board.SIDE_LENGTH; row++) { //Each row
            for (int column = 0; column < Board.SIDE_LENGTH; column++) { //Each column
                board.getSpace(row, column).updateImageResources();
                if (board.getSpace(row, column).isEmpty()) {// We need a clear piece here
                    board.getSpace(row, column).render(fl, getBaseContext(), height,
                            row, column, onPieceClicked);
                }
            }
        }
    }


    /**
     * Resets and redraws the FrameLayout
     *
     * Needed in case of undo, redo, or new game
     */
    private void updateBoard() {
        fl.removeAllViews();
        fl.addView(new Background(getBaseContext()));
        fillBoard();
    }

    /**
     * Redraws the turn indicator
     *
     * Should be called in between every turn
     */
    private void updateTurnIndicator() {
        if (board.getTurn() == Player.X)
            ((ImageView) findViewById(R.id.turnIndicator)).setImageResource(R.drawable.piece_x);
        else
            ((ImageView) findViewById(R.id.turnIndicator)).setImageResource(R.drawable.piece_o);
    }

    /**
     * Fills the FrameLayout with ImageViews, some invisible and clickable, others
     * visible and not clickable, based on the values in board.
     *
     * Needed in case of undo, redo, or new game
     */
    private void fillBoard() {
        for (int row = 0; row < Board.SIDE_LENGTH; row++) {
            for (int column = 0; column < Board.SIDE_LENGTH; column++) {
                board.getSpace(row, column).updateImageResources();
                board.getSpace(row, column).render(fl, getBaseContext(), height, row, column,
                        onPieceClicked);
            }
        }
    }

    /**
     * Notifies the winners of the game
     *
     * @param winners The ArrayList<Piece> returned from getWinners
     */
    private void notifyWinners(Map<Player, Integer> winners) {
        if (winners == null) { //Cat's Game

        }
        int winnersX = winners.get(Player.X);
        int winnersO = winners.get(Player.O);
        if (winnersX == 0 && winnersO == 0) { // No winners yet
            return;
        }
        else {
            if (winnersX == winnersO) { //Tie

            }
            else if (winnersX < winnersO) { //O wins

            }
            else { //X wins

            }
        }
        for (int i = 0; i < fl.getChildCount(); i++) {
            fl.getChildAt(i).setClickable(false); //Make sure you can't click on the board anymore
        }
    }
}
