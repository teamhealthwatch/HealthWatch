package com.example.android.healthwatch;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class ConfirmDialogFragment extends DialogFragment {

    String title;
    String content;

    static ConfirmDialogFragment newInstance(){
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();

        Bundle args = new Bundle();
        args.putString("title", "HealthWatch");


        args.putString("content", "Heart rate monitor is paused. Resume after 5 minutes.");
        confirmDialogFragment.setArguments(args);

        return confirmDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("title");
        content = getArguments().getString("content");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_confirm_dialog, container, false);
        View tv = v.findViewById(R.id.frag_view);
        ((TextView)tv).setText(title);

        View cv = v.findViewById(R.id.content_view);
        ((TextView)tv).setText(content);

        // Watch for button clicks.
        Button button = (Button)v.findViewById(R.id.confirm_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
//                ((FragmentDialog)getActivity()).showDialog();
        }
        });

        return v;
    }
}
