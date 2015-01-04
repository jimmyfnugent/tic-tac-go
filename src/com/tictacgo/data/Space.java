package com.tictacgo.data;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.tictacgo.R;

import java.util.ArrayList;
import java.util.List;

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
    return pieces.size() == 0;
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
   * @return True if any of the Pieces in this Space are X. False otherwise.
   */
  public boolean hasO() {
    for (Piece piece : pieces) {
      if (!piece.isX()) {
        return true;
      }
    }

    return false;
  }

  /**
   * Creates and returns a deep copy of this Space. That is, one whose Pieces have also been cloned.
   *
   * @return A deep copy of this Space.
   */
  public Space copy() {
    Space newSpace = new Space();

    for (Piece piece : pieces) {
      newSpace.addPiece(piece.copy());
    }

    return newSpace;
  }

  public void updateUiPosition() {
    for (Piece piece : pieces) {
      piece.updateUiPosition();
    }
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
  public void render(FrameLayout fl, Context context, int height, int gravity,
                     View.OnClickListener pieceClicked) {
    if (isEmpty()) { //No Pieces here
      FrameLayout.LayoutParams pieceLayout = new FrameLayout.LayoutParams(height / 3, height / 3,
          gravity);
      ImageView piece = new ImageView(context);
      piece.setImageResource(R.drawable.clearpiece);
      piece.setLayoutParams(pieceLayout);
      piece.setOnClickListener(pieceClicked);
      //piece.setOnDragListener(pieceDragged);
      fl.addView(piece);

    } else { //Pieces here
      for (Piece piece : pieces) {
        fl.addView(piece);
      }
    }
  }

  /**
   * Update the image resources of all of our Pieces. That is, ensure that they are just directions
   * if need be, and full pieces otherwise.
   */
  public void updateImageResources() {
    if (pieces.size() == 2 && pieces.get(0).isX() == pieces.get(1).isX()) {
      // The only case we need a direction only
      pieces.get(0).updateImageResource(true);
      pieces.get(1).updateImageResource(false);

    } else {
      for (Piece piece : pieces) {
        piece.updateImageResource(true);
      }
    }
  }
}
