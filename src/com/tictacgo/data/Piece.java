package com.example.helloandroid;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;

class Piece extends ImageView {

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
	 * 1 if the Piece is an X. -1 if it is an O.
	 */
	private int isX;
	
	/**
	 * The length of each side of the Piece
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
	 * @param x 1 if the Piece is an X. -1 if the Piece is an O.
	 * 
	 * @param s the side length of the Piece.
	 * 
	 * @param c the Context of the Piece. Used for the ImageView constructor.
	 */
	public Piece (int posx, int posy, int dirx, int diry, int x, int s, Context c) {
		super(c); //ImageView constructor
		position = new int[2];
		position[0] = posx;
		position[1] = posy;
		direction = new int[2];
		direction[0] = dirx;
		direction[1] = diry;
		isX = x;
		sideLength = s;
		updateImageResource(); // Initialize which drawable to use for the Piece
		setLayoutParams(new LayoutParams(sideLength, sideLength, Board.getGravity(posx + 1, posy + 1)));
		
		/**
		 * Set the Piece to rotate around its center
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
				//Don't need a middle column becausethat would be no movement
			}
		}
	}

	/**
	 * Creates a clone of the Piece Object. Used to copy the Board object
	 * 
	 * @return A clone of this Piece object
	 */
	public Piece clone() {
		return new Piece(getXPosition(), getYPosition(), getDirection()[0], getDirection()[1], isX(), sideLength, getContext());
	}

	/**
	 * Updates the position of the Piece and fixes wrap-arounds.
	 */
	public void updatePosition() {
		position[0]+= direction[0];
		position[1]+= direction[1];
		if (position[0] > 1) //off bottom edge
			position[0]-= 3;
		if (position[0] < -1) //off top edge
			position[0]+= 3;
		if (position[1] > 1) //off right edge
			position[1]-= 3;
		if (position[1] < -1) //of left edge
			position[1]+= 3;
		//((LayoutParams) getLayoutParams()).gravity = Board.getGravity(position[0] + 1, position[1] + 1);
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
			pos-= 3;
		if (pos < -1) //Wrap around
			pos+= 3;
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
			pos-= 3;
		if (pos < -1) //Wrap around
			pos+= 3;
		return pos;
	}
	
	public void finalize() {
		((FrameLayout.LayoutParams) getLayoutParams()).gravity = Board.getGravity(position[0], position[1]);
	}

	/**
	 * Method getDirection Returns the direction Array of the Piece.
	 *
	 * @return The direction Array of the Piece.
	 */
	public int[] getDirection() {
		return direction;
	}

	/**
	 * Method isX Returns the isX value of the Piece.
	 *
	 * @return The isX of the Piece. 1 if the Piece is X, -1 if the Piece is O.
	 */
	public int isX() {
		return isX;
	}

	/**
	 * Sets the value of isX and changes the drawable resource if necessary.
	 *
	 * @param x the value to set isX to.
	 */
	public void setisX(int x) {
	 	isX = x;
	 	if (isX != x) //Piece changed drawables
	 		updateImageResource();
	}
	
	/**
	 * Sets the drawable resource for this Piece
	 */
	private void updateImageResource() {
		if (isX == 1) //Piece is an x
			setImageResource(R.drawable.piecex);
		else //Piece is an o
			setImageResource(R.drawable.pieceo);
	}
}