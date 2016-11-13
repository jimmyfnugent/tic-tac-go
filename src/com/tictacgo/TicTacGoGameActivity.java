package com.tictacgo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.tictacgo.DirectionPickerFragment.OnDirectionPickedListener;
import com.tictacgo.data.Board;
import com.tictacgo.data.Board.Player;
import com.tictacgo.data.Piece;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The TicTacGoGameActivity class represents the Activity for when the game board is visible, ie.
 * while the game is actually being played.
 */
public class TicTacGoGameActivity extends Activity implements OnDirectionPickedListener {
    private static final String BOARD_KEY = "board";
    private static final String TURN_KEY = "activityTurn";
    private static final String FINISHED_KEY = "finished";

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
     * The initial turn selection. Used for the New Game Button
     */
    private Player turn;

    private boolean finished;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_go_game);

        fragmentManager = getFragmentManager();

        Intent intent = getIntent();

        if (savedInstanceState == null) {
            turn = (Player) intent.getSerializableExtra(TicTacGoMenuActivity.PLAYER_KEY);
            board = new Board(turn, 0, getBaseContext());
            finished = false;
        }

        // Set up the screen
        ((TextView) findViewById(R.id.gamePlayerOneName))
                .setText(intent.getStringExtra(TicTacGoMenuActivity.P1_NAME_KEY));
        ((TextView) findViewById(R.id.gamePlayerTwoName))
                .setText(intent.getStringExtra(TicTacGoMenuActivity.P2_NAME_KEY));
        fl = (FrameLayout) findViewById(R.id.gameBoard);

        // What to do when an empty space is clicked
        onPieceClicked = new View.OnClickListener() {
            public void onClick(View v) {
                if (!finished) {
                    int height = fl.getHeight();

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

                } else {
                    getFragmentManager().popBackStackImmediate(GameEndFragment.class.getName(),
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        };

        // Remove and active Direction Pickers whenever we click anywhere
        findViewById(R.id.gameScreen).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate(DirectionPickerFragment.class.getName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getFragmentManager().popBackStackImmediate(GameEndFragment.class.getName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        /**
         * Sets the New Game Button to work
         */
        findViewById(R.id.newGameButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate(GameEndFragment.class.getName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                board = new Board(turn, fl.getHeight(), getBaseContext());
                finished = false;
                updateBoard();
                updateTurnIndicator();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putSerializable(TURN_KEY, turn);
        state.putBundle(BOARD_KEY, board.getBundle());
        state.putBoolean(FINISHED_KEY, finished);
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        turn = ((Player) state.getSerializable(TURN_KEY));
        board = new Board(fl.getHeight(), getBaseContext(), state.getBundle(BOARD_KEY));
        finished = state.getBoolean(FINISHED_KEY);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            int height = findViewById(R.id.background).getHeight();
            fl.getLayoutParams().height = findViewById(R.id.background).getHeight();
            fl.getLayoutParams().width = findViewById(R.id.background).getHeight();
            board.setHeight(height);
            updateBoard();
            updateTurnIndicator();
        }
    }

    @Override
    public void onDirectionPicked(int dirVertical, int dirHorizontal, int row, int column) {
        getFragmentManager().popBackStack();

        board.makePiece(row, column);
        fl.addView(board.newPiece(dirVertical, dirHorizontal));

        if (board.willMove()) {
            // Only move the pieces after both players have moved.
            board.updatePositionsNoCollisions();
            animateBoard();

        } else {
            board.nextTurn();
            updateTurnIndicator();
            updateClearPieces();
        }
    }

    /**
     * Animate the board, first halfway, and then the second half.
     */
    private void animateBoard() {
        final List<Piece> dummies = board.getDummyPieces();
        for (Piece dummy : dummies) {
            fl.addView(dummy);
        }
        Animator halfwayAnimator = board.getHalfwayAnimator();

        halfwayAnimator.addListener(new AnimatorListenerAdapter() {
            /**
             * Here, the Pieces have gone halfway. We want to resolve halfway collisions,
             * and then start a new halfway animator.
             * @param animator
             */
            @Override
            public void onAnimationEnd(Animator animator) {
                board.resolveHalfwayCollisions();

                Animator halfwayAnimatorTwo = board.getHalfwayAnimator();
                halfwayAnimatorTwo.addListener(new AnimatorListenerAdapter() {
                    /**
                     * Here, the animations are completely finished. We need to resolve any
                     * full collisions, and then move to the next turn.
                     * @param animator
                     */
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        board.resolveFullCollisions();
                        board.nextTurn();
                        notifyWinners(board.getWinners());
                        updateTurnIndicator();
                        updateBoard();
                    }
                });

                halfwayAnimatorTwo.start();
            }
        });

        halfwayAnimator.start();
    }

    /**
     * Updates the clear Pieces on the FrameLayout
     * We must do this after each time the Pieces move
     */
    private void updateClearPieces() {
        if (!finished && board.isFull()) {
            notifyWinners(null);
        }
        
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
                    board.getSpace(row, column).render(fl, getBaseContext(), fl.getHeight(),
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
        // Remove all views except the background
        for (int i = 0; i < fl.getChildCount(); i++) {
            if (fl.getChildAt(i).getId() != R.id.background) {
                fl.removeViewAt(i);
                i--;
            }
        }

        fillBoard();
    }

    /**
     * Animates the appropriate turn indicator
     */
    private void updateTurnIndicator() {
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setDuration(3000);

        if (board.getTurn() == Player.X) {
            findViewById(R.id.gamePlayerTwoPiece).clearAnimation();
            findViewById(R.id.gamePlayerTwoPiece).setRotation(0);
            if (!finished) {
                findViewById(R.id.gamePlayerOnePiece).startAnimation(rotation);
            }

        } else {
            findViewById(R.id.gamePlayerOnePiece).clearAnimation();
            findViewById(R.id.gamePlayerOnePiece).setRotation(0);
            if (!finished) {
                findViewById(R.id.gamePlayerTwoPiece).startAnimation(rotation);
            }
        }
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
                board.getSpace(row, column).render(fl, getBaseContext(), fl.getHeight(), row,
                        column, onPieceClicked);
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
            winners = new HashMap<>(2);
            winners.put(Player.O, 1);
            winners.put(Player.X, 1);
        }

        int winnersX = winners.get(Player.X);
        int winnersO = winners.get(Player.O);
        if (winnersX == 0 && winnersO == 0) { // No winners yet
            return;

        } else {
            finished = true;
            GameEndFragment fragment = null;
            if (winnersX == winnersO) { //Tie
                fragment = GameEndFragment.newInstance(null);
            }
            else if (winnersX < winnersO) { //O wins
                fragment = GameEndFragment.newInstance(Player.O);
            }
            else { //X wins
                fragment = GameEndFragment.newInstance(Player.X);
            }

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Add the new DirectionPicker
            fragmentTransaction.add(R.id.gameScreen, fragment);
            fragmentTransaction.addToBackStack(GameEndFragment.class.getName());
            fragmentTransaction.commit();
        }
    }
}
