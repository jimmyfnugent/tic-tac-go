package com.tictacgo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tictacgo.data.Board.Player;

/**
 * The TicTacGoMenuActivity class is the Activity associated with starting a local game. It contains
 * code for choosing player names and deciding which player will go first.
 */
public class TicTacGoMenuActivity extends Activity {

    public static final String P1_NAME_KEY = "com.tictacgo.p1Name";
    public static final String P2_NAME_KEY = "com.tictacgo.p2Name";
    public static final String PLAYER_KEY = "com.tictacgo.player";
    public static final String HEIGHT_KEY = "com.tictacgo.height";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_go_menu);

        findViewById(R.id.playButton).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent playGame = new Intent(v.getContext(), TicTacGoGameActivity.class);

                String p1Name = LayoutUtils.getTextOrHint(
                        (TextView) findViewById(R.id.localPlayerOneName));
                playGame.putExtra(P1_NAME_KEY, p1Name);

                String p2Name = LayoutUtils.getTextOrHint(
                        (TextView) findViewById(R.id.localPlayerTwoName));
                playGame.putExtra(P2_NAME_KEY, p2Name);

                int first = ((RadioGroup) findViewById(R.id.localTurnSelect)).
                        getCheckedRadioButtonId();
                Player turn = null;
                if (first == R.id.localTurnSelectX) {
                    turn = Player.X;
                } else if (first == R.id.localTurnSelectO) {
                    turn = Player.O;
                }
                playGame.putExtra(PLAYER_KEY, turn);

                int height = findViewById(R.id.gameSelectScreen).getBottom();
                playGame.putExtra(HEIGHT_KEY, height);

                v.getContext().startActivity(playGame);
            }
        });
    }
}