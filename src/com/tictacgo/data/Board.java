package com.tictacgo.data;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

public class Board {

	/**
	 * 1 if it is X's turn. -1 if it is O's turn.
	 */
	private int turn;

	/**
	 * An ArrayList of the Pieces currently on the board.
	 */
	private ArrayList<ArrayList<ArrayList<Piece>>> pieces;

	/**
	 * The player who goes first.
	 * Used to decide when to move the Pieces
	 */
	private int startTurn;
	
	/**
	 * The X (Vertical) position of the next piece to be added
	 */
	private int posx;
	
	/**
	 * The Y (Horizontal) position of the next piece to be added
	 */
	private int posy;
	
	/**
	 * The height of the board
	 */
	private int height;
	
	/**
	 * The Context of the board. Used to create Pieces.
	 */
	private Context context;
	
	/**
	 * Constructor
	 * 
	 * @param t The turn select
	 *     -1 = O
	 *      0 = Random
	 *      1 = X
	 *      
	 * @param h the hight of the Board.
	 * 
	 * @param c the Context. Used when creating an ImageView
	 */	
	public Board (int t, int h, Context c) {
		context = c;
		height = h;
		
		/**
		 * Initializes the pieces ArrayList to all zeros
		 */
		pieces = new ArrayList<ArrayList<ArrayList<Piece>>>(3);
		pieces.add(new ArrayList<ArrayList<Piece>>(3));
		pieces.add(new ArrayList<ArrayList<Piece>>(3));
		pieces.add(new ArrayList<ArrayList<Piece>>(3));
		pieces.get(0).add(new ArrayList<Piece>(2));
		pieces.get(0).add(new ArrayList<Piece>(2));
		pieces.get(0).add(new ArrayList<Piece>(2));
		pieces.get(1).add(new ArrayList<Piece>(2));
		pieces.get(1).add(new ArrayList<Piece>(2));
		pieces.get(1).add(new ArrayList<Piece>(2));
		pieces.get(2).add(new ArrayList<Piece>(2));
		pieces.get(2).add(new ArrayList<Piece>(2));
		pieces.get(2).add(new ArrayList<Piece>(2));
		
		/**
		 * Sets up turn
		 */
		turn = t;
		if (turn == 0) { //Random first turn
			if (Math.random() < .5)
				turn = -1; //O goes first
			else
				turn = 1; //X goes first
		}
		startTurn = turn; //To decide when to move the Pieces
	}
	
	/**
	 * Create a new Board object that is a clone of the Object b
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
		pieces = new ArrayList<ArrayList<ArrayList<Piece>>>(3);
		for (int i = 0; i < 3; i++) {
			pieces.add(new ArrayList<ArrayList<Piece>>(3));
			for (int j = 0; j < 3; j++) {
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
	public Board clone() {
		return new Board(this);
	}

	/**
	 * Tests if the game is a Cats Game
	 *
	 * @return True if the board is full, false otherwise
	 */
	public boolean isCatsGame() {
		boolean catGame = true;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (pieces.get(i).get(j).size() == 0) { //There is an empty slot
					catGame = false; //Not a cat's game
					break;
				}
			}
			if (!catGame)
				break;
		}
		return catGame;
	}

	/**
	 * Figures out if there is a winner of the game
	 *
	 * @return an ArrayList of Pieces representing the winning Pieces. Each Piece's position representing the starting point.
	 * The direction represents the direction of the win, and the isX represents who won.
	 * Size is 0 if there are no winners.
	 */
	public ArrayList<Piece> getWinners() {
		int sum; //Used to add the rows/columns/diagonals
		
		/**
		 * Splits the board.
		 * split[0] is the x Pieces.
		 * split[2] is the o Pieces.
		 */
		int[][][] split = splitBoard();
		
		/**
		 * An ArrayList of Pieces representing the winners.
		 * Both a Piece and a winning combo has both a position and a direction.
		 */
		ArrayList<Piece> winners = new ArrayList<Piece>();
		
		/**
		 * j = the Piece we are currently looking for.
		 * It is isX + 1
		 */
		for (int j = 0; j < 3; j+= 2) { //Goes by 2 to go from split[0] to split[2]
			for (int i = 0; i < 3; i++) { //Goes by 3
				sum = split[j][i][0] + split[j][i][1] + split[j][i][2]; //Checks row i 
				if (sum == 3)
					winners.add(new Piece(i, 0, 0, 1, j - 1, height / 3, context)); //Piece moving through row i
				sum = split[j][0][i] + split[j][1][i] + split[j][2][i]; //Checks column i
				if (sum == 3)
					winners.add(new Piece(0, i, 1, 0, j - 1, height / 3, context)); //Piece moving down column i
			}
			sum = split[j][0][0] + split[j][1][1] + split[j][2][2]; //Checks the top-left to bottom-right diagonal
			if (sum == 3)
				winners.add(new Piece(0, 0, 1, 1, j - 1, height / 3, context)); //Piece going down the top-left to bottom-right diagonal
			sum = split[j][0][2] + split[j][1][1] + split[j][2][0]; //Checks the top-right to bottom-left diagonal
			if (sum == 3)
				winners.add(new Piece(0, 2, 1, -1, j - 1, height / 3, context)); //Piece going down the top-right to bottom-left diagonal
		}
		return winners;
	}

	/**
	 * Splits the board as help for the isWinner method
	 *
	 * @return an int[][][] of boards.
	 * split[0] = O Pieces
	 * split[1] = NA
	 * split[2] = X Pieces
	 * The value is 1 if there is a Piece, 0 otherwise.
	 */
	private int[][][] splitBoard() {
		int[][][] split = new int[3][3][3];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				for (int k = 0; k < pieces.get(i).get(j).size(); k++)
					split[pieces.get(i).get(j).get(k).isX() + 1][pieces.get(i).get(j).get(k).getXPosition() + 1][pieces.get(i).get(j).get(k).getYPosition() + 1] = 1;
		return split;
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
	 * Goes to the next turn by negating the value of turn.
	 */
	public void nextTurn() {
		turn = -turn;
	}


	/**
	 * Updates the positions of the Pieces and wraps around out of bounds Pieces.
	 * 
	 * @return An ArrayList of Pieces to be changed during Animation. (Collisions)
	 */
	public ArrayList<ArrayList<ArrayList<Piece>>> updatePositions() {
		ArrayList<Piece> temp = new ArrayList<Piece>(9); //ArrayList of the Pieces
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
		ArrayList<ArrayList<ArrayList<Piece>>> collisions = new ArrayList<ArrayList<ArrayList<Piece>>>();
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
		ArrayList<ArrayList<Piece>> collisions = new ArrayList<ArrayList<Piece>>();
		for (int i = 0; i < temp.size() - 1; i++) { //For each Piece
			collisions.add(new ArrayList<Piece>()); //Will be the Pieces that collide with temp(i)
			collisions.get(i).add(temp.get(i)); //Add Piece temp(i)
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
		ArrayList<ArrayList<Piece>> collisions = new ArrayList<ArrayList<Piece>>();
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
	
	public void finalize() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < pieces.get(i).get(j).size(); k++) {
					pieces.get(i).get(j).get(k).finalize();
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
	 * Gets the board value reffered to by the indeces
	 * 
	 * @param i First index
	 * @param j Second index
	 * @return the value of space i, j
	 * 
	 * Values:
	 * 0 Pieces: 0
	 * 1 Piece: Piece.isX()
	 * 2 Pieces: Piece1.isX() + Piece2.isX() + 10
	 */
	public int getBoard(int i, int j) {
		if (pieces.get(i).get(j).size() == 0) //No pieces
			return 0;
		if (pieces.get(i).get(j).size() == 1) //One piece
			return pieces.get(i).get(j).get(0).isX();
		//More than one piece
		return 10 + pieces.get(i).get(j).get(0).isX() + pieces.get(i).get(j).get(1).isX();
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
	
	/**
	 * Gets startTurn
	 * 
	 * @return startTurn
	 */
	public int getStartTurn() {
		return startTurn;
	}
	
	/**
	 * Gets the current turn
	 * 
	 * @return turn
	 */
	public int getTurn() {
		return turn;
	}

	/**
	 * Gets the height
	 * 
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the Context
	 * 
	 * @return context
	 */
	public Context getContext() {
		return context;
	}
}