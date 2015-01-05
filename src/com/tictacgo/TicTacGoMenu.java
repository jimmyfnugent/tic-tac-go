package com.tictacgo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.tictacgo.data.Board;
import com.tictacgo.data.Board.Player;
import com.tictacgo.data.Piece;


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

        // First turn
        int first = ((RadioGroup) findViewById(R.id.localTurnSelect)).getCheckedRadioButtonId();
        playGame.putExtra("first", first);

        v.getContext().startActivity(playGame);
      }
    });
  }
}