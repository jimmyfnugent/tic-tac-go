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
		 * Initializes the spaces ArrayList to all empty Spaces
		 */
		spaces = new ArrayList<>(SIDE_LENGTH);
    pieces = new ArrayList<>(SIDE_LENGTH * SIDE_LENGTH * 2);

    for (int i = 0; i < SIDE_LENGTH; i++) {
      List<Space> row = new ArrayList<>(SIDE_LENGTH);

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
		for (int i = 0; i < SIDE_LENGTH; i++) {
			spaces.add(new ArrayList<Space>(SIDE_LENGTH));
			for (int j = 0; j < SIDE_LENGTH; j++) {
				spaces.get(i).add(b.getSpace(i, j).copy());
        pieces.addAll(spaces.get(i).get(j).getPieces());
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
    //TODO: This allows spaces with multiple Pieces in them to still contribute to a win.
    // We don't need to map to a count here because a Player may only win once per row, column, or
    // diagonal.
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
   * Merges the winners from an individual row, column, or diagonal with the running sum of overall
   * winners.
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
	 * @param dirx The X direction of the Piece.
	 * @param diry The Y direction of the Piece.
	 * @return The new Piece
	 */
	public View newPiece(int dirx, int diry) {
		Piece p = new Piece(posx, posy, dirx, diry, turn, height / 3, context);
		spaces.get(posx + 1).get(posy + 1).addPiece(p);
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
				if ((pieces.get(i).getLastXPosition() == pieces.get(j).getLastXPosition() &&
					 pieces.get(i).getXPosition() == pieces.get(j).getXPosition() &&
					 pieces.get(i).getLastYPosition() == pieces.get(j).getYPosition() &&
					 pieces.get(i).getYPosition() == pieces.get(j).getLastYPosition()) ||
				
				/**
				 * X values cross.
				 * Y values are the same.
				 */
					(pieces.get(i).getLastXPosition() == pieces.get(j).getXPosition() &&
					 pieces.get(i).getXPosition() == pieces.get(j).getLastXPosition() &&
					 pieces.get(i).getLastYPosition() == pieces.get(j).getLastYPosition() &&
					 pieces.get(i).getYPosition() == pieces.get(j).getYPosition()) ||

				/**
				 * X values cross.
				 * Y values cross.
				 */
					(pieces.get(i).getLastXPosition() == pieces.get(j).getXPosition() &&
					 pieces.get(i).getXPosition() == pieces.get(j).getLastXPosition() &&
					 pieces.get(i).getLastYPosition() == pieces.get(j).getYPosition() &&
					 pieces.get(i).getYPosition() == pieces.get(j).getLastYPosition())) {

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
      for (int i = collision.size() - 1; i >= 0; i--) { // Foreach here creates concurrent mod exception.
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
	public void removePiece(Piece piece) { //We need the + 1 because pos ranges from -1 to 1
										   //And the Array goes from 0 to 2
		spaces.get(piece.getXPosition() + 1).get(piece.getYPosition() + 1).removePiece(piece);
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
		int gravity;
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
	 * Returns the Space at index i, j
	 * 
	 * @param i the first index
	 * @param j the second index
	 * @return The Space at index i, j
	 */
	public Space getSpace(int i, int j) {
		return spaces.get(i).get(j);
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
