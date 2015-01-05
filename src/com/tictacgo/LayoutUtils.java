package com.tictacgo;

import android.widget.TextView;

public class LayoutUtils {
  /**
   * Gets the entered text from the given TextView, if any has been entered, or the hint from that
   * TextView otherwise.
   */
  public static String getTextOrHint(TextView view) {
    CharSequence text = view.getText();

    if (text.length() == 0) {
      text = view.getHint();
    }

    return text.toString();
  }
}
