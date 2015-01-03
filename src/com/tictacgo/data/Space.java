package com.tictacgo.data;

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
    pieces = new ArrayList<Piece>(2);
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
  public Space clone() {
    Space newSpace = new Space();

    for (Piece piece : pieces) {
      newSpace.addPiece(piece.clone());
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
}
