package com.tictacgo;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tictacgo.data.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TicTacGoGame extends Activity implements DirectionPicker.OnDirectionPickedListener {
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
  private View.OnClickListener pieceClicked;

  /**
   * The height of the usable screen area
   */
  private int height;

  /**
   * An ArrayList of the Boards for undo and redo
   */
  private List<Board> undoHistory;

  /**
   * The index of the current undo/redo history
   */
  private int historyIndex;

  /**
   * The initial turn selection. Used for the New Game Button
   */
  private Board.Player turn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.game);

    Intent intent = getIntent();

    int first = intent.getIntExtra("first", R.id.localTurnSelectX);
    if (first == R.id.localTurnSelectX) {
      turn = Board.Player.X;
    } else if (first == R.id.localTurnSelectO) {
      turn = Board.Player.O;
    } else {
      turn = null;
    }

    // Undo/redo initialization
    undoHistory = new ArrayList<>();
    historyIndex = 0;

    height = intent.getIntExtra("height", 300);
    board = new Board(turn, height, getBaseContext());

    // Set up the screen
    ((TextView) findViewById(R.id.gamePlayerOneName)).setText(intent.getStringExtra("p1Name"));
    ((TextView) findViewById(R.id.gamePlayerTwoName)).setText(intent.getStringExtra("p2Name"));
    fl = (FrameLayout) findViewById(R.id.gameBoard);

    // What to do when an empty space is clicked
    pieceClicked = new View.OnClickListener() {
      public void onClick(View v) {
        int gravity = ((FrameLayout.LayoutParams) v.getLayoutParams()).gravity;
        board.makePiece(gravity); // Preset Piece location in board

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        DirectionPicker directionPicker = new DirectionPicker();
        Bundle arguments = new Bundle();
        arguments.putString("player", board.getTurn() == Board.Player.X ? "X" : "O");
        arguments.putInt("gravity", gravity);
        arguments.putInt("height", height);
        directionPicker.setArguments(arguments);

        fragmentTransaction.add(R.id.gameBoard, directionPicker);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
      }
    };

    /**
     * Sets the New Game Button to work
     */
    findViewById(R.id.newGameButton).setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        board = new Board(turn, height, getBaseContext());
        updateBoard();
        updateTurnIndicator();
        undoHistory = new ArrayList<>();
        historyIndex = 0;
        play();
      }
    });

    /**
     * Sets up the Undo Button
     */
    findViewById(R.id.undoButton).setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (historyIndex == 0) //First turn already
          return;
        historyIndex--; //Go back one index
        board = undoHistory.get(historyIndex).copy(); //Go back one Board
        updateBoard();
        updateTurnIndicator();
      }
    });

    /**
     * Sets up the Redo Button
     */
    findViewById(R.id.redoButton).setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (historyIndex == undoHistory.size() - 1) //Last turn already
          return;
        historyIndex++; //Go forward one index
        board = undoHistory.get(historyIndex).copy(); //Go forward one Board
        updateBoard();
        updateTurnIndicator();
      }
    });

    /**
     * Step 5: Play the game
     */
    play();
    updateBoard();
    updateTurnIndicator();
  }

  @Override
  public void onDirectionPicked(int dirx, int diry) {
    board.newPiece(dirx, diry);
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
    for (int i = 0; i < Board.SIDE_LENGTH; i++) { //Each row
      for (int j = 0; j < Board.SIDE_LENGTH; j++) { //Each column
        board.getSpace(i, j).updateImageResources();
        if (board.getSpace(i, j).isEmpty()) {// We need a clear piece here
          board.getSpace(i, j).render(fl, getBaseContext(), height, Board.getGravity(i, j),
              pieceClicked);
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
    if (board.getTurn() == Board.Player.X)
      ((ImageView) findViewById(R.id.turnIndicator)).setImageResource(R.drawable.piecex);
    else
      ((ImageView) findViewById(R.id.turnIndicator)).setImageResource(R.drawable.pieceo);
  }

  /**
   * Fills the FrameLayout with ImageViews, some invisible and clickable, others
   * visible and not clickable, based on the values in board.
   *
   * Needed in case of undo, redo, or new game
   */
  private void fillBoard() {
    for (int i = 0; i < Board.SIDE_LENGTH; i++) {
      for (int j = 0; j < Board.SIDE_LENGTH; j++) {
        board.getSpace(i, j).updateImageResources();
        board.getSpace(i, j).render(fl, getBaseContext(), height, Board.getGravity(i, j),
            pieceClicked);
      }
    }
  }

  /**
   * Run at the beginning of the game and the end of each turn
   */
  private void play(){
    while (undoHistory.size() > historyIndex + 1) { //Remove all unwanted redo Boards
      undoHistory.remove(historyIndex + 1);
    }
    undoHistory.add(board.copy()); //Add our board to the undo history
    historyIndex++;
  }

  /**
   * The CPU's turn
   */
  private void CPUMove() {
    // TODO Make a move

    // TODO Draw the piece
    if (board.willMove()) {
      notifyWinners(board.getWinners());
    } else {
      board.nextTurn();
      notifyWinners(board.getWinners());
      updateTurnIndicator();
      updateClearPieces();
    }
  }

  /**
   * Notifies the winners of the game
   *
   * @param winners The ArrayList<Piece> returned from getWinners
   */
  private void notifyWinners(Map<Board.Player, Integer> winners) {
    if (winners == null) { //Cat's Game

    }
    int winnersX = winners.get(Board.Player.X);
    int winnersO = winners.get(Board.Player.O);
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
