package com.tictacgo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tictacgo.data.Board.Player;

/**
 * The GameEndFragment contains code for showing the game end dialog.
 */
public class GameEndFragment extends Fragment {
    private static final String WINNER_ARGUMENT_KEY = "winner";

    public Player winner;

    public static GameEndFragment newInstance(Player winner) {
        Bundle arguments = new Bundle();

        arguments.putSerializable(WINNER_ARGUMENT_KEY, winner);

        GameEndFragment fragment = new GameEndFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void setArguments(Bundle arguments) {
        winner = (Player) arguments.getSerializable(WINNER_ARGUMENT_KEY);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create and locate view properly
        View view = inflater.inflate(R.layout.fragment_game_end_dialog, container, false);

        return view;
    }
}
