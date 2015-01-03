package com.tictacgo.data;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;

import com.tictacgo.R;
import com.tictacgo.data.Board.Player;

public class Piece extends ImageView {

	/**
	 * An Integer Array representing the position of the Piece.
	 *
	 * Index 0 represents the X coordinate. 1 represents the Y coordinate.
	 * The values range from -1 to 1, where -1 represents the left and top.
	 */
	private int[] position;

	/**
	 * An Integer Array representing the direction of the Piece.
	 *
	 * Index 0 represents the X direction. 1 represents the Y direction.
	 * The values range from -1 to 1, where -1 represents the left and top.
	 */
	private int[] direction;

	/**
     * The player this piece belongs to.
	 */
	private Player player;
	
	/**
	 * The length of each side of the Piece, in pixels.
	 */
	private int sideLength;

	/**
	 * Constructor
	 *
	 * @param posx The x position of the Piece.
	 *
	 * @param posy The y position of the Piece.
	 *
	 * @param dirx The x direction of the Piece.
	 *
	 * @param dirx The x direction of the Piece.
	 *
	 * @param player The player this piece belongs to.
	 * 
	 * @param sideLength the side length of the Piece, in pixels.
	 * 
	 * @param c the Context of the Piece. Used for the ImageView constructor.
	 */
	public Piece(int posx, int posy, int dirx, int diry, Player player, int sideLength, Context c) {
		super(c); //ImageView constructor
		position = new int[2];
		position[0] = posx;
		position[1] = posy;
		direction = new int[2];
		direction[0] = dirx;
		direction[1] = diry;
        this.player = player;
		this.sideLength = sideLength;
		updateImageResource(); // Initialize which drawable to use for the Piece
		setLayoutParams(new LayoutParams(sideLength, sideLength, Board.getGravity(posx + 1, posy + 1)));
		
		/**
		 * Set the Piece to rotate around its center, to face the correct direction.
		 */
		setPivotX(sideLength / 2);
		setPivotY(sideLength / 2);
		setRotation(getPieceRotation());
	}

	/**
	 * Returns what the Piece's rotation should be based on its direction
	 * 
	 * @return A value to be used in the ImageView.setRotation Method
	 */
	private float getPieceRotation() {
		switch (direction[0]) {
		case -1: //top row
			switch (direction[1]) {
			case -1: //left column
				return 225;
			case 1: //right column
				return 315;
			default: //middle column
				return 270;
			}
		case 1: //bottom row
			switch (direction[1]) {
			case -1: //left column
				return 135;
			case 1: //right column
				return 45;
			default: //middle column
				return 90;
			}
		default: //middle row
			switch (direction[1]) {
			case -1: //left column
				return 180;
			default: //right column
				return 0;
				//Don't need a middle column because that would be no movement
			}
		}
	}

	/**
	 * Creates a clone of the Piece Object. Used to copy the Board object
	 * 
	 * @return A clone of this Piece object
	 */
	public Piece clone() {
		return new Piece(getXPosition(), getYPosition(), getDirection()[0], getDirection()[1], getPlayer(), sideLength, getContext());
	}

	/**
	 * Updates the position of the Piece and fixes wrap-arounds.
	 */
	public void updatePosition() {
		position[0] += direction[0];
		position[1] += direction[1];

        // Going off one edge results in wrapping around the other side.
        position[0] = (position[0] + 4) % 3 - 1;
        position[1] = (position[1] + 4) % 3 - 1;
	}

	/**
	 * Returns the position Array of the Piece.
	 *
	 * @return The position Array of the Piece.
	 */
	public int[] getPosition() {
		return position;
	}

	/**
	 * Returns the x position of the Piece.
	 *
	 * @return The x position of the Piece.
	 */
	public int getXPosition() {
		return position[0];
	}

  public int getRow() {
    return position[0] + 1;
  }

  public int getColumn() {
    return position[1] + 1;
  }

	/**
	 * Returns the y position of the Piece.
	 *
	 * @return The y position of the Piece.
	 */
	public int getYPosition() {
		return position[1];
	}

	/**
	 * Returns the x value of the Piece's last position
	 *
	 * @return the x coordinate of of the Piece's last position
	 */
	public int getLastXPosition() {
		int pos = position[0] - direction[0];
		if (pos > 1) //Wrap around
			pos -= 3;
		if (pos < -1) //Wrap around
			pos += 3;
		return pos;
	}

	/**
	 * Returns the y value of the Piece's last position
	 *
	 * @return the y coordinate of of the Piece's last position
	 */
	public int getLastYPosition() {
		int pos = position[1] - direction[1];
		if (pos > 1) //Wrap around
			pos -= 3;
		if (pos < -1) //Wrap around
			pos += 3;
		return pos;
	}

	public void updateUiPosition() {
        // Board expects positions in the [0, 2] range.
		((FrameLayout.LayoutParams) getLayoutParams()).gravity = Board.getGravity(position[0] + 1, position[1] + 1);
	}

	/**
	 * Method getDirection Returns the direction Array of the Piece.
	 *
	 * @return The direction Array of the Piece.
	 */
	public int[] getDirection() {
		return direction;
	}

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        updateImageResource();
    }

	/**
     * Returns true if this piece belongs to player X; false otherwise.
	 */
	public boolean isX() {
		return player == Player.X;
	}

	/**
	 * Sets the drawable resource for this Piece
	 */
	private void updateImageResource() {
		if (isX()) //Piece is an x
			setImageResource(R.drawable.piecex);
		else //Piece is an o
			setImageResource(R.drawable.pieceo);
	}
}
