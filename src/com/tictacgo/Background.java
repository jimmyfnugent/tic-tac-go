package com.tictacgo;

import android.content.Context;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;

/**
 * The Background class controls drawing the TicTacGo board background image.
 */
public class Background extends ImageView {
	
	/**
	 * Sets up a new Background. It will always have these three parameters
	 * 
	 * @param context The context of the Window
	 */
	public Background(Context context) {
		super(context);
		super.setImageResource(R.drawable.board);
		super.setClickable(false);
		super.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
	
	/**
	 * Draws the background. Sets the width to be equal to the height so it will be square
	 * 
	 * @param widthMeasureSpec The width
	 * @param heightMeasureSpec The height
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, heightMeasureSpec);
	}
}
