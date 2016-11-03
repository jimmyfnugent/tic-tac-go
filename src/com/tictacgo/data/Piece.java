package com.tictacgo.data;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;
import android.support.v4.content.ContextCompat;

import com.tictacgo.Angles;
import com.tictacgo.R;
import com.tictacgo.data.Board.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A Piece represents a single game piece (X or O), and information about its location, direction,
 * as well as graphical information from the ImageView class.
 */
public class Piece extends ImageView {

    private final Drawable X_FULL_DRAWABLE = ContextCompat.getDrawable(getContext(),
            R.drawable.piece_x);
    private final Drawable X_DIR_DRAWABLE = ContextCompat.getDrawable(getContext(),
            R.drawable.piece_x_direction);
    private final Drawable O_FULL_DRAWABLE = ContextCompat.getDrawable(getContext(),
            R.drawable.piece_o);
    private final Drawable O_DIR_DRAWABLE = ContextCompat.getDrawable(getContext(),
            R.drawable.piece_o_direction);

    /**
     * An Integer Array representing the position of the Piece.
     *
     * Index 0 represents the row coordinate. 1 represents the column coordinate.
     * The values range from 0 to 2, where 0 represents the left and top.
     */
    private int[] position;

    /**
     * An Integer Array representing the direction of the Piece.
     *
     * Index 0 represents the vertical direction. 1 represents the horizontal direction.
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
     * The animator of this Piece
     */
    private Animator halfwayAnimator;

    /**
     * A List of any dummy Pieces we will use for wraparound animations.
     */
    private List<Piece> dummies;

    /**
     * Constructor
     *
     * @param row The x position of the Piece.
     *
     * @param column The y position of the Piece.
     *
     * @param dirVertical The x direction of the Piece.
     *
     * @param dirHorizontal The y direction of the Piece.
     *
     * @param player The player this piece belongs to.
     *
     * @param sideLength the side length of the Piece, in pixels.
     *
     * @param c the Context of the Piece. Used for the ImageView constructor.
     */
    public Piece(int row, int column, int dirVertical, int dirHorizontal, Player player,
                 int sideLength, Context c) {
        super(c); //ImageView constructor
        position = new int[2];
        position[0] = row;
        position[1] = column;
        direction = new int[2];
        direction[0] = dirVertical;
        direction[1] = dirHorizontal;
        this.player = player;
        this.sideLength = sideLength;
        updateImageResourceFullPiece();

        setLayoutParams(new LayoutParams(sideLength, sideLength, Gravity.TOP | Gravity.LEFT));
        updateUiPosition();

        dummies = new ArrayList<>(3);

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
        switch (getVerticalDirection()) {
            case -1: //top row
                switch (getHorizontalDirection()) {
                    case -1: //left column
                        return Angles.TOP_LEFT;
                    case 1: //right column
                        return Angles.TOP_RIGHT;
                    default: //middle column
                        return Angles.TOP;
                }
            case 1: //bottom row
                switch (getHorizontalDirection()) {
                    case -1: //left column
                        return Angles.BOTTOM_LEFT;
                    case 1: //right column
                        return Angles.BOTTOM_RIGHT;
                    default: //middle column
                        return Angles.BOTTOM;
                }
            default: //middle row
                switch (getHorizontalDirection()) {
                    case -1: //left column
                        return Angles.LEFT;
                    default: //right column
                        return Angles.RIGHT;
                        //Don't need a middle column because that would be no movement
                }
        }
    }

    /**
     * Updates the position of the Piece and fixes wrap-arounds.
     */
    public void updatePositionNoCollision() {
        position[0] += direction[0];
        position[1] += direction[1];

        // Going off one edge results in wrapping around the other side.
        position[0] = (position[0] + 3) % 3;
        position[1] = (position[1] + 3) % 3;
    }

    public int getRow() {
        return position[0];
    }

    public int getColumn() {
        return position[1];
    }

    /**
     * Returns the Piece's last position's row
     *
     * @return the row of of the Piece's last position
     */
    public int getLastRow() {
        return (getRow() - getVerticalDirection() + 3) % 3;
    }

    /**
     * Returns the Piece's last position's column
     *
     * @return the column of the Piece's last position
     */
    public int getLastColumn() {
        return (getColumn() - getHorizontalDirection() + 3) % 3;
    }

    public void updateUiPosition() {
        ((LayoutParams) getLayoutParams()).setMargins(sideLength * getColumn(), sideLength * getRow(), 0, 0);
    }

    /**
     * Returns the vertical direction of the Piece.
     *
     * @return The vertical direction of the Piece.
     */
    public int getVerticalDirection() {
        return direction[0];
    }

    /**
     * Returns the horizontal direction of the Piece.
     *
     * @return The horizontal direction of the Piece.
     */
    public int getHorizontalDirection() {
        return direction[1];
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        updateImageResourceFullPiece();

        for (Piece dummy : dummies) {
            dummy.setPlayer(player);
        }
    }

    /**
     * Returns true if this piece belongs to player X; false otherwise.
     */
    public boolean isX() {
        return player == Player.X;
    }

    /**
     * Returns true if this piece belongs to player O; false otherwise.
    */
    public boolean isO() {
        return player == Player.O;
    }

    /**
     * Sets the drawable resource for this Piece to be the full piece with direction.
     */
    public void updateImageResourceFullPiece() {
        if (isX()) {
            setImageDrawable(X_FULL_DRAWABLE);
        } else { //Piece is an o
            setImageDrawable(O_FULL_DRAWABLE);
        }
    }

    /**
    * Sets the drawable resource for this Piece to be the direction only.
    */
    public void updateImageResourceDirectionOnly() {
        if (isX()) {
            setImageDrawable(X_DIR_DRAWABLE);
        } else { //Piece is an o
            setImageDrawable(O_DIR_DRAWABLE);
        }
    }

    /**
     * Get a List of the dummy Pieces we will need for wrap-animation.
     *
     * @return A List of dummy Pieces for wrapping around the edges of the Board.
     */
    public void updateDummyPieces() {
        dummies = new ArrayList<>(3);

        if (getHorizontalDirection() == -1 && getLastColumn() == 0) {
            // Wrapped around the left edge
            dummies.add(new Piece(getLastRow(), 3, getVerticalDirection(),
                    getHorizontalDirection(), player, sideLength, getContext()));

        } else if (getHorizontalDirection() == 1 && getLastColumn() == 2) {
            // Wrapped around the right edge
            dummies.add(new Piece(getLastRow(), -1, getVerticalDirection(),
                    getHorizontalDirection(), player, sideLength, getContext()));
        }

        if (getVerticalDirection() == -1 && getLastRow() == 0) {
            // Wrapped around the top edge
            dummies.add(new Piece(3, getLastColumn(), getVerticalDirection(),
                    getHorizontalDirection(), player, sideLength, getContext()));

        } else if (getVerticalDirection() == 1 && getLastRow() == 2) {
            // Wrapped around the bottom edge
            dummies.add(new Piece(-1, getLastColumn(), getVerticalDirection(),
                    getHorizontalDirection(), player, sideLength, getContext()));
        }

        if (dummies.size() == 2) {
            // Wrapped around diagonally
            dummies.add(new Piece(getRow() - getVerticalDirection(),
                    getColumn() - getHorizontalDirection(), getVerticalDirection(),
                    getHorizontalDirection(), player, sideLength, getContext()));
        }
    }

    /**
     * Get the dummy Pieces to use in animation this step for wrap-arounds.
     *
     * @return The dummy Pieces associated with this Piece.
     */
    public List<Piece> getDummies() {
        return dummies;
    }

    /**
     * Update the animator for this piece.
     *
     * NOTE: In this method, the position array has already been updated and wrapped around.
     */
    public void updateHalfwayAnimator() {
        final LayoutParams params = (LayoutParams) getLayoutParams();

        PropertyValuesHolder horizontalValues = PropertyValuesHolder.ofInt("left",
                params.leftMargin, params.leftMargin + sideLength / 2 * getHorizontalDirection());
        PropertyValuesHolder verticalValues = PropertyValuesHolder.ofInt("top",
                params.topMargin, params.topMargin + sideLength / 2 * getVerticalDirection());

        halfwayAnimator = ValueAnimator.ofPropertyValuesHolder(verticalValues, horizontalValues);
        ((ValueAnimator) halfwayAnimator).addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                params.topMargin = (int) valueAnimator.getAnimatedValue("top");
                params.leftMargin = (int) valueAnimator.getAnimatedValue("left");
                requestLayout();
            }
        });

        if (!dummies.isEmpty()) {
            List<Animator> animators = new ArrayList<>(1 + dummies.size());
            animators.add(halfwayAnimator);
            for (Piece dummy : dummies) {
                dummy.updateHalfwayAnimator();
                animators.add(dummy.getHalfwayAnimator());
            }

            halfwayAnimator = new AnimatorSet();
            ((AnimatorSet) halfwayAnimator).playTogether(animators);
        }
    }

    /**
     * Get this Piece's animator.
     *
     * @return This Piece's animator.
     */
    public Animator getHalfwayAnimator() {
        return halfwayAnimator;
    }
}
