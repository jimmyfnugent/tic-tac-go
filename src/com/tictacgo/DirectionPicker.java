package com.tictacgo;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DirectionPicker extends DialogFragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    getDialog().setCanceledOnTouchOutside(true);
    return inflater.inflate(R.layout.direction, container, false);
  }
}
