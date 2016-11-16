package com.tictacgo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.tictacgo.data.Board.Player;

/**
 * The GameEndFragment contains code for showing the game end dialog.
 */
public class GameEndFragment extends Fragment {
    private static final String WINNER_ARGUMENT_KEY = "winner";

    /**
     * The player who won, or null if it was a tie game.
     */
    public Player winner;

    /**
     * Create and return a new GameEndFragment with the given winner.
     *
     * @param winner The winner of the game, or null if it was a tie game.
     * @return The GameEndFragmentwith the given winner.
     */
    public static GameEndFragment newInstance(Player winner) {
        Bundle arguments = new Bundle();

        arguments.putSerializable(WINNER_ARGUMENT_KEY, winner);

        GameEndFragment fragment = new GameEndFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putSerializable(WINNER_ARGUMENT_KEY, winner);
    }

    @Override
    public void setArguments(Bundle arguments) {
        winner = (Player) arguments.getSerializable(WINNER_ARGUMENT_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            setArguments(savedInstanceState);
        }

        // Create and locate view properly
        View view = inflater.inflate(R.layout.fragment_game_end_dialog, container, false);

        ImageView pieceLeft = (ImageView) view.findViewById(R.id.gameEndPieceLeft);
        ImageView pieceRight = (ImageView) view.findViewById(R.id.gameEndPieceRight);
        TextView text = (TextView) view.findViewById(R.id.gameEndText);

        // Set correct pieces
        pieceLeft.setImageResource(winner == Player.O ? R.drawable.piece_o : R.drawable.piece_x);
        pieceRight.setImageResource(winner == Player.X ? R.drawable.piece_x : R.drawable.piece_o);

        Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        rotation.setDuration(1000);
        pieceLeft.startAnimation(rotation);
        pieceRight.startAnimation(rotation);

        // Set winner text
        if (winner == Player.X) {
            CharSequence name =
                    ((TextView) getActivity().findViewById(R.id.gamePlayerOneName)).getText();
            text.setText(name + getString(R.string.winsSuffix));

        } else if (winner == Player.O) {
            CharSequence name =
                    ((TextView) getActivity().findViewById(R.id.gamePlayerTwoName)).getText();
            text.setText(name + getString(R.string.winsSuffix));

        } else {
            text.setText(getString(R.string.gameEndTie));
        }

        return view;
    }
}
