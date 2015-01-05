package com.tictacgo;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableRow;

import com.tictacgo.data.Board;

public class DirectionPicker extends DialogFragment {
  private Board.Player player;

  private int xOffset;
  private int yOffset;

  private int height;

  public DirectionPicker(Board.Player player, int gravity, int height) {
    super();
    this.player = player;
    this.height = height;

    switch (gravity) {
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
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    getDialog().setCanceledOnTouchOutside(true);

    int id = player == Board.Player.X ? R.drawable.piecexdirection : R.drawable.pieceodirection;

    // Set direction buttons
    setDirectionButton((ImageView) container.findViewById(R.id.directionTopRight), id, 315);
    setDirectionButton((ImageView) container.findViewById(R.id.directionTopMiddle), id, 270);
    setDirectionButton((ImageView) container.findViewById(R.id.directionTopLeft), id, 225);
    setDirectionButton((ImageView) container.findViewById(R.id.directionMiddleLeft), id, 180);
    setDirectionButton((ImageView) container.findViewById(R.id.directionBottomLeft), id, 135);
    setDirectionButton((ImageView) container.findViewById(R.id.directionBottomMiddle), id, 90);
    setDirectionButton((ImageView) container.findViewById(R.id.directionBottomRight), id, 45);
    setDirectionButton((ImageView) container.findViewById(R.id.directionMiddleRight), id, 0);

    id = player == Board.Player.X ? R.drawable.piecex : R.drawable.pieceo;
    setDirectionButton((ImageView) container.findViewById(R.id.directionClear), id, 0);

    return inflater.inflate(R.layout.direction, container, false);
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
