package com.tictacgo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.TextView;


public class TicTacGoMenu extends Activity {
	
	/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.menu);

    findViewById(R.id.playButton).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent playGame = new Intent(v.getContext(), TicTacGoGame.class);

        String p1Name = LayoutUtils.getTextOrHint((TextView) findViewById(R.id.localPlayerOneName));
        playGame.putExtra("p1Name", p1Name);

        String p2Name = LayoutUtils.getTextOrHint((TextView) findViewById(R.id.localPlayerTwoName));
        playGame.putExtra("p2Name", p2Name);

        int first = ((RadioGroup) findViewById(R.id.localTurnSelect)).getCheckedRadioButtonId();
        playGame.putExtra("first", first);

        int height = findViewById(R.id.gameSelectScreen).getBottom();
        playGame.putExtra("height", height);

        v.getContext().startActivity(playGame);
      }
    });
  }
}