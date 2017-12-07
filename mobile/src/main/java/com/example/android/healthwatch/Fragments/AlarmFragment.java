package com.example.android.healthwatch.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;

import com.example.android.healthwatch.R;

import java.util.ArrayList;

/**
 * Created by faitholadele on 11/26/17.
 */

public class AlarmFragment extends DialogFragment {

    public interface RepeatSelectionListener {
        void onRepeatSelection(ArrayList items);
    }

    RepeatSelectionListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (RepeatSelectionListener) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ArrayList items = new ArrayList();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.week_days)
            .setMultiChoiceItems(R.array.repeat_array, null,
                    new DialogInterface.OnMultiChoiceClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if(isChecked)
                            {
                                items.add(which);
                                String wh = Integer.toString(which);
                                Log.i("Log-stuff", wh);
                            }
                            else if (items.contains(which)) {
                                items.remove(Integer.valueOf(which));
                            }
                        }
                    })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onRepeatSelection(items);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();

    }
}
