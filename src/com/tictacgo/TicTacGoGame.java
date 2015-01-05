package com.tictacgo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.tictacgo.data.Board;
import com.tictacgo.data.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TicTacGoGame extends Activity {
  /**
   * The Board of the game
   */
  private Board board;

  /**
   * The name of Player One
   */
  private String player1;

  /**
   * The name of Player Two
   */
  private String player2;

  /**
   * The FrameLayout in which the game is to be drawn
   */
  private FrameLayout fl;

  /**
   * OnClickListener to use for each piece
   */
  private View.OnClickListener pieceClicked;

  /**
   * OnClickListener to use to pick direction
   */
  private View.OnClickListener directionClicked;

  /**
   * PopupWindow with direction Buttons
   */
  private PopupWindow directions;

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
   * The turn selection. Used for the New Game Button
   */
  private Board.Player turn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    // Undo/redo initialization
    undoHistory = new ArrayList<>();
    historyIndex = 0;
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

    if ((board.getTurn() == Board.Player.X && isCPU1) || (board.getTurn() == Board.Player.O && isCPU2)) { //CPU Move
      CPUMove();
      play();
    }
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
