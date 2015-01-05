package com.tictacgo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;

import com.tictacgo.data.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TicTacGoGame extends Activity {
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
    super.onCreate(savedInstanceState);
    setContentView(R.layout.game);

    Intent intent = getIntent();

    int first = intent.getIntExtra("first", R.id.localTurnSelectX);
    if (first == R.id.localTurnSelectX) {
      turn = Board.Player.X;
    } else if (first == R.id.localTurnSelectO) {
      turn = Board.Player.X;
    } else {
      Random rand = new Random();
      turn = rand.nextBoolean() ? Board.Player.X : Board.Player.O;
    }

    // Undo/redo initialization
    undoHistory = new ArrayList<>();
    historyIndex = 0;

    /**
     * Step 3: Set up board
     */
    height = findViewById(R.id.gameSelectScreen).getBottom();
    board = new Board(turn, height, getBaseContext());

    /**
     * Step 4: Sets up the screen
     */
    setContentView(R.layout.game); //Change layout
    ((TextView) findViewById(R.id.gamePlayerOneName)).setText(intent.getStringExtra("p1Name"));
    ((TextView) findViewById(R.id.gamePlayerTwoName)).setText(intent.getStringExtra("p2Name"));
    fl = (FrameLayout) findViewById(R.id.gameBoard);

    /**
     * What to do when an empty space is clicked
     */
    pieceClicked = new View.OnClickListener() {
      public void onClick(View v) {
        if (directions != null) //Popup already active
          directions.dismiss(); //Dismiss old popup
        board.makePiece(((FrameLayout.LayoutParams) v.getLayoutParams()).gravity); //Set the location for the new Piece
        LayoutInflater inflater = getLayoutInflater();
        View grid = inflater.inflate(R.layout.direction, null); //Show popup
        directions = new PopupWindow(grid, -2, -2); //Show  popup
        /**
         * Offset is used so that the correct edge of the popup window will
         * line up with the edge of the Board
         */
        int xOffset = 0, yOffset = 0; //No offset
        switch (((FrameLayout.LayoutParams) v.getLayoutParams()).gravity) {
          case Gravity.TOP | Gravity.LEFT:
            break;
          case Gravity.TOP | Gravity.CENTER_HORIZONTAL:
            xOffset = height / 4;
            break;
          case Gravity.TOP | Gravity.RIGHT:
            xOffset = height / 2;
            break;
          case Gravity.CENTER_VERTICAL | Gravity.LEFT:
            yOffset = height / 4;
            break;
          case Gravity.CENTER_VERTICAL | Gravity.RIGHT:
            xOffset = height / 2;
            yOffset = height / 4;
            break;
          case Gravity.BOTTOM | Gravity.LEFT:
            yOffset = height / 2;
            break;
          case Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL:
            xOffset = height / 4;
            yOffset = height / 2;
            break;
          case Gravity.BOTTOM | Gravity.RIGHT:
            xOffset = height / 2;
            yOffset = height / 2;
            break;
          default:
            xOffset = height / 4;
            yOffset = height / 4;
        }
        directions.showAtLocation(fl, Gravity.NO_GRAVITY, xOffset, yOffset); //Make popup visible
        int id = R.drawable.pieceodirection; //Which picture to use for directions (O)
        if (board.getTurn() == Board.Player.X) //X's turn
          id = R.drawable.piecexdirection;
        /**
         * Set the Button images for the Popup Window
         */
        setDirectionButton((ImageView) grid.findViewById(R.id.directionTopRight), id, 315);
        setDirectionButton((ImageView) grid.findViewById(R.id.directionTopMiddle), id, 270);
        setDirectionButton((ImageView) grid.findViewById(R.id.directionTopLeft), id, 225);
        setDirectionButton((ImageView) grid.findViewById(R.id.directionMiddleLeft), id, 180);
        setDirectionButton((ImageView) grid.findViewById(R.id.directionBottomLeft), id, 135);
        setDirectionButton((ImageView) grid.findViewById(R.id.directionBottomMiddle), id, 90);
        setDirectionButton((ImageView) grid.findViewById(R.id.directionBottomRight), id, 45);
        setDirectionButton((ImageView) grid.findViewById(R.id.directionMiddleRight), id, 0);
        id = R.drawable.pieceo; //What picture to use for center (O)
        if (board.getTurn() == Board.Player.X) //X's turn
          id = R.drawable.piecex;
        setDirectionButton((ImageView) grid.findViewById(R.id.directionClear), id, 0); //Set center button image
      }
    };

    /**
     * What to do when a direction is clicked
     */
    directionClicked = new View.OnClickListener() {
      public void onClick(View v) {
        switch (v.getId()) { //Which direction was picked
          case R.id.directionTopLeft:
            fl.addView(board.newPiece(-1, -1));
            break;
          case R.id.directionTopMiddle:
            fl.addView(board.newPiece(-1, 0));
            break;
          case R.id.directionTopRight:
            fl.addView(board.newPiece(-1, 1));
            break;
          case R.id.directionMiddleLeft:
            fl.addView(board.newPiece(0, -1));
            break;
          case R.id.directionMiddleRight:
            fl.addView(board.newPiece(0, 1));
            break;
          case R.id.directionBottomLeft:
            fl.addView(board.newPiece(1, -1));
            break;
          case R.id.directionBottomMiddle:
            fl.addView(board.newPiece(1, 0));
            break;
          case R.id.directionBottomRight:
            fl.addView(board.newPiece(1, 1));
            break;
          default: //Center Button
            directions.dismiss();
            return; //Only reason we need this case
        }
        directions.dismiss();

        /**
         * Game loop
         */
        notifyWinners(board.getWinners());
        if (board.willMove()) {
          // Only move the pieces after both players have moved.
          board.updatePositions();
          board.updateUiPositions();
        }
        board.nextTurn();
        updateTurnIndicator();
        updateClearPieces();
        play();
      }
    };
    updateBoard();
    updateTurnIndicator();

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

  /**
   * Helper method for creating the direction picker PopupWindow
   *
   * @param imageView the ImageView to edit
   * @param id the image to use
   * @param rotation the rotation of the image
   */
  private void setDirectionButton(ImageView imageView, int id, int rotation) {
    TableRow.LayoutParams pieceLayout = new TableRow.LayoutParams(height / 6, height / 6);
    imageView.setLayoutParams(pieceLayout);
    imageView.setOnClickListener(directionClicked);
    imageView.setImageResource(id);
    imageView.setPivotX(height / 12);
    imageView.setPivotY(height / 12);
    imageView.setRotation(rotation);
  }
}
