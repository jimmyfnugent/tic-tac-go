package com.tictacgo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.tictacgo.data.Board;
import com.tictacgo.data.Board.Player;
import com.tictacgo.data.Piece;


public class TicTacGoMenu extends Activity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
      setContentView(R.layout.menu); //Display menu

      /**
       * Default = player vs. player
       */
      isCPU1 = false;
      isCPU2 = false;
      setUpButtons();
    }

	/**
     * Sets up all of the Buttons for the main screen
     */
    private void setUpButtons() {
    	/**
         * Play in local mode
         */
        findViewById(R.id.localButton).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
               	findViewById(R.id.onlineScreen).setVisibility(View.GONE);
               	findViewById(R.id.localScreen).setVisibility(View.VISIBLE);
           	}
        });
        
        /**
         * Play in online mode
         */
        findViewById(R.id.onlineButton).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		findViewById(R.id.localScreen).setVisibility(View.GONE);
               	findViewById(R.id.onlineScreen).setVisibility(View.VISIBLE);
               	// TODO EVERYTHING!!!
           	}
        });
        
        /**
         * Make player one a computer player
         * This involves switching the EditViews and changing isCPU1
         */
        findViewById(R.id.localComputerOneButton).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		findViewById(R.id.localPlayerOneName).setVisibility(View.GONE);
               	findViewById(R.id.localComputerOneName).setVisibility(View.VISIBLE);
               	isCPU1 = true;
           	}
        });
        
        /**
         * Make player one a human player
         * This involves switching the EditViews and changing isCPU1
         */
        findViewById(R.id.localPlayerOneButton).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		findViewById(R.id.localComputerOneName).setVisibility(View.GONE);
               	findViewById(R.id.localPlayerOneName).setVisibility(View.VISIBLE);
               	isCPU1 = false;
           	}
        });
        
        /**
         * Make player two a computer player
         * This involves switching the EditViews and changing isCPU2
         */
        findViewById(R.id.localComputerTwoButton).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		findViewById(R.id.localPlayerTwoName).setVisibility(View.GONE);
               	findViewById(R.id.localComputerTwoName).setVisibility(View.VISIBLE);
               	isCPU2 = true;
           	}
        });
        
        /**
         * Make player two a human player.
         * This involves switching the EditViews and changing isCPU2
         */
        findViewById(R.id.localPlayerTwoButton).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		findViewById(R.id.localComputerTwoName).setVisibility(View.GONE);
               	findViewById(R.id.localPlayerTwoName).setVisibility(View.VISIBLE);
               	isCPU2 = false;
           	}
        });
        
        /**
         * Show the options Menu
         */
        findViewById(R.id.optionsButton).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		openOptionsMenu();
           	}
        });
        
        /**
         * Show the help dialog
         */
        findViewById(R.id.helpButton).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		// TODO create the help dialog
           	}
        });
        
        /**
         * Play the game
         * 
         * Steps:
         * 1: Set up turn variable
         * 2: Set up player1 and player2
         * 3: Create the board object
         * 4: Set up the screen
         * 5: Enter the play loop
         */
        findViewById(R.id.playButton).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		/**
        		 * Step 1: Set up turn variable
        		 */
        		turn = null; //Default = random
           		int first = ((RadioGroup) findViewById(R.id.localTurnSelect)).getCheckedRadioButtonId();
           		if (first == R.id.localTurnSelectO)
           			turn = Player.O; //O first
           		else if (first == R.id.localTurnSelectX)
           			turn = Player.X; //X first
           		
           		/**
           		 * Step 2: Set up player1 and player2 names
           		 */
           		if (isCPU1) {
           			player1 = ((TextView) findViewById(R.id.localComputerOneName)).getText().toString();
           			if (player1.equals("")) //No name entered
           				player1 = ((TextView) findViewById(R.id.localComputerOneName)).getHint().toString();
           		} else {
           			player1 = ((TextView) findViewById(R.id.localPlayerOneName)).getText().toString();
           			if (player1.equals("")) //No name entered
           				player1 = ((TextView) findViewById(R.id.localPlayerOneName)).getHint().toString();
           		}
           		if (isCPU2) {
           			player2 = ((TextView) findViewById(R.id.localComputerTwoName)).getText().toString();
        			if (player2.equals("")) //No name entered
        				player2 = ((TextView) findViewById(R.id.localComputerTwoName)).getHint().toString();
        		} else {
        			player2 = ((TextView) findViewById(R.id.localPlayerTwoName)).getText().toString();
        			if (player2.equals("")) //No name entered
        				player2 = ((TextView) findViewById(R.id.localPlayerTwoName)).getHint().toString();
        		}
           		
           		/**
           		 * Step 3: Set up board
           		 */
           		height = findViewById(R.id.gameSelectScreen).getBottom();
           		board = new Board(turn, height, getBaseContext());
           		
           		/**
           		 * Step 4: Sets up the screen
           		 */
           		setContentView(R.layout.game); //Change layout
        		((TextView) findViewById(R.id.gamePlayerOneName)).setText(player1);
        		((TextView) findViewById(R.id.gamePlayerTwoName)).setText(player2);
        		fl = (FrameLayout) findViewById(R.id.gameBoard);
				
				/**
				 * What to do when an empty space is clicked
				 */
        		pieceClicked = new OnClickListener() {
        			public void onClick(View v) {
        				if (directions != null) //Popup already active
        					directions.dismiss(); //Dismiss old popup
        				board.makePiece(((LayoutParams) v.getLayoutParams()).gravity); //Set the location for the new Piece
        				LayoutInflater inflater = getLayoutInflater();
        				View grid = inflater.inflate(R.layout.direction, null); //Show popup
        				directions = new PopupWindow(grid, -2, -2); //Show  popup
        				/**
        				 * Offset is used so that the correct edge of the popup window will
        				 * line up with the edge of the Board
        				 */
        				int xOffset = 0, yOffset = 0; //No offset
        				switch (((LayoutParams) v.getLayoutParams()).gravity) {
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
        				if (board.getTurn() == Player.X) //X's turn
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
        				if (board.getTurn() == Player.X) //X's turn
        					id = R.drawable.piecex;
        				setDirectionButton((ImageView) grid.findViewById(R.id.directionClear), id, 0); //Set center button image
        			}
        		};
        		
        		/**
        		 * What to do when a direction is clicked
        		 */
        		directionClicked = new OnClickListener() {
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
                 * Sets Main Screen Button to go back to the main screen
                 */
                findViewById(R.id.mainScreenButton).setOnClickListener(new OnClickListener() {
                	public void onClick(View v) {
                		setContentView(R.layout.menu);
                		setUpButtons();
                   	}
                });
                
                /**
                 * Sets the New Game Button to work
                 */
                findViewById(R.id.newGameButton).setOnClickListener(new OnClickListener() {
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
                findViewById(R.id.undoButton).setOnClickListener(new OnClickListener() {
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
                findViewById(R.id.redoButton).setOnClickListener(new OnClickListener() {
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
        });
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