package com.tictacgo.data;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.tictacgo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A Space represents a single square on the TicTacGo board. It's main function is to keep track
 * of the Pieces which are at that location.
 */
public class Space {
    /**
     * A List of the Pieces which are in this Space.
     */
    private List<Piece> pieces;

    /**
     * Creates a new Space, initially empty.
     */
    public Space() {
        pieces = new ArrayList<>(2);
    }

    /**
     * Adds a Piece into this Space.
     *
     * @param piece The Piece to add.
     */
    public void addPiece(Piece piece) {
        pieces.add(piece);
    }

    /**
     * Removes a Piece from this Space.
     *
     * @param piece The Piece to remove.
     */
    public void removePiece(Piece piece) {
        pieces.remove(piece);
    }

    /**
     * Checks if this Space is empty.
     *
     * @return True if this Space contains no Pieces. False otherwise.
     */
    public boolean isEmpty() {
        return pieces.isEmpty();
    }

    /**
     * Checks if this Space contains any X Pieces.
     *
     * @return True if any of the Pieces in this Space are X. False otherwise.
     */
    public boolean hasX() {
        for (Piece piece : pieces) {
            if (piece.isX()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if this Space contains any O Pieces.
     *
     * @return True if any of the Pieces in this Space are O. False otherwise.
     */
    public boolean hasO() {
        for (Piece piece : pieces) {
            if (piece.isO()) {
                return true;
            }
        }

        return false;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public boolean collisionOccurred() {
        return pieces.size() > 1;
    }

    /**
     * Render this Space on the screen
     */
    public void render(FrameLayout fl, Context context, int height, int row, int column,
            View.OnClickListener pieceClicked) {
        if (isEmpty()) { //No Pieces here
            int pieceHeight = height / 3;
            FrameLayout.LayoutParams pieceLayout = new FrameLayout.LayoutParams(pieceHeight,
                    pieceHeight, Gravity.TOP | Gravity.LEFT);
            pieceLayout.setMargins(column * pieceHeight, row * pieceHeight, 0, 0);

            ImageView piece = new ImageView(context);
            piece.setImageResource(R.drawable.clear_piece);
            piece.setLayoutParams(pieceLayout);
            piece.setOnClickListener(pieceClicked);
            fl.addView(piece);

        } else { //Pieces here
            for (Piece piece : pieces) {
                piece.updateUiPosition();
                fl.addView(piece);
            }
        }
    }

    /**
     * Update the image resources of all of our Pieces. That is, ensure that they are just
     * directions if need be, and full pieces otherwise.
     */
    public void updateImageResources() {
        if (pieces.size() == 2 && pieces.get(0).isX() == pieces.get(1).isX()) {
            // The only case we need a direction only
            pieces.get(0).updateImageResourceFullPiece();
            pieces.get(1).updateImageResourceDirectionOnly();
        } else {
            for (Piece piece : pieces) {
            piece.updateImageResourceFullPiece();
            }
        }
    }
}
