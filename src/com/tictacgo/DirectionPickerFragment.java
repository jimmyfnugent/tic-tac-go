package com.tictacgo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TableRow;

import com.tictacgo.data.Board.Player;

/**
 * The DirectionPickerFragment contains all of the code for choosing the direction of a new Piece.
 * An Activity instantiating this Fragment must implement OnDirectionPickedListener.
 */
public class DirectionPickerFragment extends Fragment {
  public static final String PLAYER_ARGUMENT_KEY = "player";
  public static final String GRAVITY_ARGUMENT_KEY = "gravity";
  public static final String HEIGHT_ARGUMENT_KEY = "boardHeight";

  private Player player;
  private int gravity;
  private int boardHeight;

  private View.OnClickListener directionClicked;

  private OnDirectionPickedListener listener;

  // Container Activity must implement this interface
  public interface OnDirectionPickedListener {
    public void onDirectionPicked(int dirx, int diry, int gravity);
  }

  /**
   * Creates and returns a new DirectionPicker object.
   *
   * @param player The player whose turn it is.
   * @param gravity The gravity of the location that was just pressed.
   * @param boardHeight The height of the board.
   * @return A new DirectionPicker object with the given parameters.
   */
  public static DirectionPickerFragment newInstance(Player player, int gravity, int boardHeight) {
    Bundle arguments = new Bundle();
    arguments.putSerializable(PLAYER_ARGUMENT_KEY, player);
    arguments.putInt(GRAVITY_ARGUMENT_KEY, gravity);
    arguments.putInt(HEIGHT_ARGUMENT_KEY, boardHeight);

    DirectionPickerFragment directionPicker = new DirectionPickerFragment();
    directionPicker.setArguments(arguments);
    return directionPicker;
  }

  @Override
  public void setArguments(Bundle arguments) {
    player = (Player) arguments.getSerializable(PLAYER_ARGUMENT_KEY);
    boardHeight = arguments.getInt(HEIGHT_ARGUMENT_KEY);
    gravity = arguments.getInt(GRAVITY_ARGUMENT_KEY);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    listener = (OnDirectionPickedListener) getActivity();

    directionClicked = new View.OnClickListener() {
      public void onClick(View v) {
       switch (v.getId()) { //Which direction was picked
          case R.id.directionTopLeft:
            listener.onDirectionPicked(-1, -1, gravity);
            break;
          case R.id.directionTopMiddle:
            listener.onDirectionPicked(-1, 0, gravity);
            break;
          case R.id.directionTopRight:
            listener.onDirectionPicked(-1, 1, gravity);
            break;
          case R.id.directionMiddleLeft:
            listener.onDirectionPicked(0, -1, gravity);
            break;
          case R.id.directionMiddleRight:
            listener.onDirectionPicked(0, 1, gravity);
            break;
          case R.id.directionBottomLeft:
            listener.onDirectionPicked(1, -1, gravity);
            break;
          case R.id.directionBottomMiddle:
            listener.onDirectionPicked(1, 0, gravity);
            break;
          case R.id.directionBottomRight:
            listener.onDirectionPicked(1, 1, gravity);
            break;
          default:
            getFragmentManager().popBackStack();
        }
      }
    };
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Create and locate view properly
    View view = inflater.inflate(R.layout.fragment_direction_picker, container, false);
    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
        gravity);
    view.setLayoutParams(params);

    int id = player == Player.X ? R.drawable.piecexdirection : R.drawable.pieceodirection;

    // Set direction buttons
    setDirectionButton((ImageView) view.findViewById(R.id.directionTopRight), id, Angles.TOP_RIGHT);
    setDirectionButton((ImageView) view.findViewById(R.id.directionTopMiddle), id, Angles.TOP);
    setDirectionButton((ImageView) view.findViewById(R.id.directionTopLeft), id, Angles.TOP_LEFT);
    setDirectionButton((ImageView) view.findViewById(R.id.directionMiddleLeft), id, Angles.LEFT);
    setDirectionButton((ImageView) view.findViewById(R.id.directionBottomLeft), id, Angles.BOTTOM_LEFT);
    setDirectionButton((ImageView) view.findViewById(R.id.directionBottomMiddle), id, Angles.BOTTOM);
    setDirectionButton((ImageView) view.findViewById(R.id.directionBottomRight), id, Angles.BOTTOM_RIGHT);
    setDirectionButton((ImageView) view.findViewById(R.id.directionMiddleRight), id, Angles.RIGHT);

    id = player == Player.X ? R.drawable.piecex : R.drawable.pieceo;
    setDirectionButton((ImageView) view.findViewById(R.id.directionClear), id, 0);

    return view;
  }

  /**
   * Helper method for creating the direction picker PopupWindow
   *
   * @param imageView the ImageView to edit
   * @param id the image to use
   * @param rotation the rotation of the image
   */
  private void setDirectionButton(ImageView imageView, int id, int rotation) {
    TableRow.LayoutParams pieceLayout = new TableRow.LayoutParams(boardHeight / 6, boardHeight / 6);
    imageView.setLayoutParams(pieceLayout);
    imageView.setOnClickListener(directionClicked);
    imageView.setImageResource(id);
    imageView.setPivotX(boardHeight / 12);
    imageView.setPivotY(boardHeight / 12);
    imageView.setRotation(rotation);
  }
}
