package com.tictacgo.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

public class Board {

    public enum Player {
        X,
        O
    }

    /**
     * The number of spaces per side of the game board.
     */
    private static final int SIDE_LENGTH = 3;

	/**
     * The player who currently has their turn.
	 */
	private Player turn;

	/**
	 * An ArrayList of the Pieces currently on the board.
	 */
	private ArrayList<ArrayList<ArrayList<Piece>>> pieces;

	/**
	 * The player who goes first.
	 * Used to decide when to move the Pieces
	 */
	private Player startTurn;
	
	/**
	 * The X (Vertical) position of the next piece to be added
	 */
	private int posx;
	
	/**
	 * The Y (Horizontal) position of the next piece to be added
	 */
	private int posy;
	
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
		 * Initializes the pieces ArrayList to all zeros
		 */
		pieces = new ArrayList<>(SIDE_LENGTH);
		pieces.add(new ArrayList<ArrayList<Piece>>(SIDE_LENGTH));
		pieces.add(new ArrayList<ArrayList<Piece>>(SIDE_LENGTH));
		pieces.add(new ArrayList<ArrayList<Piece>>(SIDE_LENGTH));
        for (int i = 0; i < SIDE_LENGTH; i++) {
            // Each space can have up to 2 overlapping pieces.
            pieces.get(i).add(new ArrayList<Piece>(2));
            pieces.get(i).add(new ArrayList<Piece>(2));
            pieces.get(i).add(new ArrayList<Piece>(2));
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
		
		/**
		 * Clones the pieces ArrayList
		 */
		pieces = new ArrayList<>(SIDE_LENGTH);
		for (int i = 0; i < SIDE_LENGTH; i++) {
			pieces.add(new ArrayList<ArrayList<Piece>>(SIDE_LENGTH));
			for (int j = 0; j < SIDE_LENGTH; j++) {
				pieces.get(i).add(new ArrayList<Piece>(2));
				for (int k = 0; k < b.getPieces(i, j).size(); k++) {
					pieces.get(i).get(j).add(b.getPieces(i, j).get(k).clone()); //Clones the Piece in slot i, j, k
				}
			}
		}
	}

	/**
	 * Creates a clone of the Board Object
	 * 
	 * @return A clone of this Board object
	 */
    @Override
	public Board clone() {
		return new Board(this);
	}

	/**
	 * Tests if the game is a Cats Game
	 *
	 * @return True if the board is full, false otherwise
	 */
	public boolean isCatsGame() {
		for (int i = 0; i < SIDE_LENGTH; i++) {
			for (int j = 0; j < SIDE_LENGTH; j++) {
				if (pieces.get(i).get(j).size() == 0) { //There is an empty slot
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
        // Assign values to the players to calculate winners.
        Map<Player, Integer> playerValues = new HashMap<>();
        playerValues.put(Player.X, 1);
        playerValues.put(Player.O, -1);

        int winningSumX = playerValues.get(Player.X) * SIDE_LENGTH;
        int winningSumY = playerValues.get(Player.O) * SIDE_LENGTH;

		int sum; //Used to add the rows/columns/diagonals
		
		/**
		 * A map of Players to the count of their winning combinations.
		 */
		Map<Player, Integer> winners = new HashMap<>();
        winners.put(Player.X, 0);
        winners.put(Player.O, 0);

        // Sum all rows, columns, and diagonals, looking for all Xs or all Os.
        // Rows
        for (ArrayList<ArrayList<Piece>> row : pieces) {
            sum = 0;
            for (ArrayList<Piece> space : row) {
                // Spaces with multiple pieces block winning.
                // TODO: figure out if this is more fun than multi-piece squares being able to win.
                if (space.size() == 1) {
                    sum += playerValues.get(space.get(0).getPlayer());
                }
            }
            if (sum == winningSumX) {
                winners.put(Player.X, winners.get(Player.X) + 1);
            } else if (sum == winningSumY) {
                winners.put(Player.O, winners.get(Player.O) + 1);
            }
        }

        // Columns
        for (int col = 0; col < SIDE_LENGTH; col++) {
            sum = 0;
            for (ArrayList<ArrayList<Piece>> row : pieces) {
                ArrayList<Piece> space = row.get(col);
                // Spaces with multiple pieces block winning.
                if (space.size() == 1) {
                    sum += playerValues.get(space.get(0).getPlayer());
                }
            }
            if (sum == winningSumX) {
                winners.put(Player.X, winners.get(Player.X) + 1);
            } else if (sum == winningSumY) {
                winners.put(Player.O, winners.get(Player.O) + 1);
            }
        }

        // Top left to bottom right diagonal
        sum = 0;
        int row = 0;
        int col = 0;
        while (row < SIDE_LENGTH && col < SIDE_LENGTH) {
            ArrayList<Piece> space = pieces.get(row).get(col);
            // Spaces with multiple pieces block winning.
            if (space.size() == 1) {
                sum += playerValues.get(space.get(0).getPlayer());
            }
            row++;
            col++;
        }
        if (sum == winningSumX) {
            winners.put(Player.X, winners.get(Player.X) + 1);
        } else if (sum == winningSumY) {
            winners.put(Player.O, winners.get(Player.O) + 1);
        }

        // Top right to bottom left diagonal
        sum = 0;
        row = SIDE_LENGTH - 1;
        col = SIDE_LENGTH - 1;
        while (row >= 0 && col >= 0) {
            ArrayList<Piece> space = pieces.get(row).get(col);
            // Spaces with multiple pieces block winning.
            if (space.size() == 1) {
                sum += playerValues.get(space.get(0).getPlayer());
            }
            row--;
            col--;
        }
        if (sum == winningSumX) {
            winners.put(Player.X, winners.get(Player.X) + 1);
        } else if (sum == winningSumY) {
            winners.put(Player.O, winners.get(Player.O) + 1);
        }

		return winners;
	}

	/**
	 * Method newPiece Adds a new Piece to the Board
	 *
	 * @param dirx The X direction of the Piece.
	 * @param diry The Y direction of the Piece.
	 * @return The new Piece
	 */
	public View newPiece(int dirx, int diry) {
		Piece p = new Piece(posx, posy, dirx, diry, turn, height / 3, context);
		pieces.get(posx + 1).get(posy + 1).add(p); //Adds the Piece to the pieces ArrayList
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
	 * @return An ArrayList of Pieces to be changed during Animation. (Collisions)
	 */
	public ArrayList<ArrayList<ArrayList<Piece>>> updatePositions() {
		ArrayList<Piece> temp = new ArrayList<>(9); //ArrayList of the Pieces
		for (int i = 0; i < 3; i++) { //Each Row
			for (int j = 0; j < 3; j++) { //Each Column
				for (int k = 0; k < pieces.get(i).get(j).size();) { //Each Piece 
					temp.add(pieces.get(i).get(j).remove(k)); //Adds the Piece to our new ArrayList of Pieces
															  //and removes it from pieces since it will be changing positions
					temp.get(temp.size() - 1).updatePosition(); //Changes the position of the Piece
				}
			}
		}
		/**
		 * ArrayList of the collisions of the Board.
		 * collisions(0) will be the halfway collisions
		 * collisions(1) will be the full collisions
		 * Each of those contains an ArrayList of collisions
		 * Each of which are ArrayLists of the Pieces that collide
		 */
		ArrayList<ArrayList<ArrayList<Piece>>> collisions = new ArrayList<>();
		/**
		 * Sets up the collisions ArrayList.
		 * Note that we don't deal with the collisions here, we just list them.
		 */
		collisions.add(getHalfwayCollisions(temp));
		collisions.add(getCollisions());
		for (int i = 0; i < temp.size(); i++) { //Each Piece
			/**
			 * We need the + 1 because pos ranges from -1 to 1
			 * And the Array goes from 0 to 2
			 */
			pieces.get(temp.get(i).getXPosition() + 1).get(temp.get(i).getYPosition() + 1).add(temp.get(i));
		}
		return collisions;
	}
	
	/**
	 * Method getHalfwayCollisions Gets all halfway collisions, ie. when two Pieces meet in between squares.
	 *
	 * There are three cases:
	 *  Their Y's cross
	 *  Their X's cross
	 *  Both cross
	 *
	 * The Pieces should bounce off each other, which is essentially the same as simply switching their isX values or their directions.
	 *
	 * If three or more Pieces bounce off of each other, they should explode. (Remove them).
	 * 
	 * @param temp An ArrayList of the Pieces in the Board
	 * 
	 * @return An ArrayList of the collisions.
	 * 	For each collision:
	 * 		If its length is 2, we should switch the isX values.
	 * 		If its length is greater than 2, we should remove the Pieces.
	 */
	public ArrayList<ArrayList<Piece>> getHalfwayCollisions(ArrayList<Piece> temp) {
		ArrayList<ArrayList<Piece>> collisions = new ArrayList<>();
		for (int i = 0; i < temp.size() - 1; i++) { //For each Piece
			collisions.add(new ArrayList<Piece>()); //Will be the Pieces that collide with temp(i)
			collisions.get(collisions.size() - 1).add(temp.get(i)); //Add Piece temp(i)
			for (int j = i + 1; j < temp.size(); j++) { //For every Piece after i in temp.
														//This eliminates having two ArrayLists for one collisions
				/**
				 * X values are the same.
				 * Y values cross.
				 */
				if ((temp.get(i).getLastXPosition() == temp.get(j).getLastXPosition() &&
					 temp.get(i).getXPosition() == temp.get(j).getXPosition() &&
					 temp.get(i).getLastYPosition() == temp.get(j).getYPosition() &&
					 temp.get(i).getYPosition() == temp.get(j).getLastYPosition()) ||
				
				/**
				 * X values cross.
				 * Y values are the same.
				 */
					(temp.get(i).getLastXPosition() == temp.get(j).getXPosition() &&
					 temp.get(i).getXPosition() == temp.get(j).getLastXPosition() &&
					 temp.get(i).getLastYPosition() == temp.get(j).getLastYPosition() &&
					 temp.get(i).getYPosition() == temp.get(j).getYPosition()) ||

				/**
				 * X values cross.
				 * Y values cross.
				 */
					(temp.get(i).getLastXPosition() == temp.get(j).getXPosition() &&
					 temp.get(i).getXPosition() == temp.get(j).getLastXPosition() &&
					 temp.get(i).getLastYPosition() == temp.get(j).getYPosition() &&
					 temp.get(i).getYPosition() == temp.get(j).getLastYPosition())) {

						collisions.get(collisions.size() - 1).add(temp.get(j)); //A collision occurred
				}
			}
			if (collisions.get(collisions.size() - 1).size() == 1) //No collision occurred
				collisions.remove(collisions.size() - 1); //Don't want it in the collisions ArrayList
		}
		return collisions;
	}

	/**
	 * Gets the collisions within a space
	 *
	 * If there are two Pieces in a square, we should simply switch their isX values.
	 * If there are more than two Pieces in a square, we should remove them all from the Board.
	 * 
	 * @return An ArrayList of the collisions
	 */
	public ArrayList<ArrayList<Piece>> getCollisions() {
		ArrayList<ArrayList<Piece>> collisions = new ArrayList<>();
		for (int i = 0; i < 3; i++) { //Vertical coordinate
			for (int j = 0; j < 3; j++) { //horizontal coordinate
				if (pieces.get(i).get(j).size() > 1) { //More than one piece in the same space
					collisions.add(pieces.get(i).get(j));
				}
			}
		}
		return collisions;
	}
	
	/**
	 * Removes a Piece from the pieces ArrayList.
	 * Called from the collision resolution in TicTacGo.java
	 * 
	 * @param piece The Piece to be romoved
	 */
	public void removePiece(Piece piece) { //We need the + 1 because pos ranges from -1 to 1
										   //And the Array goes from 0 to 2
		pieces.get(piece.getXPosition() + 1).get(piece.getYPosition() + 1).remove(piece);
	}

	public void updateUiPositions() {
        // Iterate through the board and update each piece's UI position.
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < pieces.get(i).get(j).size(); k++) {
					pieces.get(i).get(j).get(k).updateUiPosition();
				}
			}
		}
	}

	/**
	 * Sets the next Piece Location as teh space posx, posy
	 * posx is the top to bottom index.
	 * posy is the right to left index.
	 * 
	 * @param gravity The Gravity of the Space clicked
	 */
	public void makePiece(int gravity) {
		switch (gravity) {
		case Gravity.TOP | Gravity.LEFT:
			posx = -1;
			posy = -1;
			break;
		case Gravity.TOP | Gravity.CENTER_HORIZONTAL:
			posx = -1;
			posy = 0;
			break;
		case Gravity.TOP | Gravity.RIGHT:
			posx = -1;
			posy = 1;
			break;
		case Gravity.CENTER_VERTICAL | Gravity.LEFT:
			posx = 0;
			posy = -1;
			break;
		case Gravity.CENTER_VERTICAL | Gravity.RIGHT:
			posx = 0;
			posy = 1;
			break;
		case Gravity.BOTTOM | Gravity.LEFT:
			posx = 1;
			posy = -1;
			break;
		case Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL:
			posx = 1;
			posy = 0;
			break;
		case Gravity.BOTTOM | Gravity.RIGHT:
			posx = 1;
			posy = 1;
			break;
		default: //Center
			posx = 0;
			posy = 0;
		}
	}
	
	/**
	 * Gets the Gravity value of the Piece at coordinates i, j
	 * 
	 * @param i The X coordinate (top to bottom)
	 * @param j The Y coordinate (right to left)
	 * @return The Gravity value
	 */
	public static int getGravity(int i, int j) {
		int gravity = 0;
		if (i == 0) //Top row
			gravity = Gravity.TOP;
		else if (i == 1) //Middle row
			gravity = Gravity.CENTER_VERTICAL;
		else //Bottom row
			gravity = Gravity.BOTTOM;
		if (j == 0) //Left column
			gravity = gravity | Gravity.LEFT;
		else if (j == 1) //Middle column
			gravity = gravity | Gravity.CENTER_HORIZONTAL;
		else //Right column
			gravity = gravity | Gravity.RIGHT;
		return gravity;
	}

	/**
	 * Gets the Piece ArrayList
	 * 
	 * @return pieces
	 */
	public ArrayList<ArrayList<ArrayList<Piece>>> getPieces() {
		return pieces;
	}
	/**
	 * Returns the value of pieces.get(i)
	 * 
	 * @param i the index
	 * @return The ArrayList<ArrayList<Piece>> at index i
	 */
	public ArrayList<ArrayList<Piece>> getPieces(int i) {
		return pieces.get(i);
	}
	
	/**
	 * Returns the value of pieces.get(i).get(j)
	 * 
	 * @param i the first index
	 * @param j the second index
	 * @return The ArrayList<Piece> at index i, j
	 */
	public ArrayList<Piece> getPieces(int i, int j) {
		return pieces.get(i).get(j);
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
