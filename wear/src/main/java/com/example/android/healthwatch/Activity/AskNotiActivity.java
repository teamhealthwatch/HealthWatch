package com.example.android.healthwatch.Activity;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import com.example.android.healthwatch.R;

import java.awt.font.TextAttribute;

public class AskNotiActivity extends Activity {

    String info;
    TextView notiView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_noti);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            info = "No information";
        } else {
            info = extras.getString("NotiID");
        }
        notiView = findViewById(R.id.noti_view);
        notiView.setText(info);
    }

}
