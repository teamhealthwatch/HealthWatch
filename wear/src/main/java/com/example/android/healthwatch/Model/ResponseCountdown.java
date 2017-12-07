package com.example.android.healthwatch.Model;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

import com.example.android.healthwatch.HeartRateService;

import java.io.Serializable;

/**
 * Created by Yan Tan on 12/4/2017.
 */

public class ResponseCountdown implements Parcelable {

    private CountDownTimer countDownTimer;
    private Long currentSec;

    private TextView textView;

    public ResponseCountdown() {
        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
//                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
//                currentSec = millisUntilFinished / 1000;

                if (textView != null){
                    textView.setText("seconds remaining: " + millisUntilFinished / 1000);
                }
            }

            public void onFinish() {
                // send message to mobile to make calls

            }
        }.start();
    }

//    public Long getCurrentSec() {
//        return currentSec;
//    }

    protected ResponseCountdown(Parcel in) {
        if (in.readByte() == 0) {
            currentSec = null;
        } else {
            currentSec = in.readLong();
        }
    }

    public static final Creator<ResponseCountdown> CREATOR = new Creator<ResponseCountdown>() {
        @Override
        public ResponseCountdown createFromParcel(Parcel in) {
            return new ResponseCountdown(in);
        }

        @Override
        public ResponseCountdown[] newArray(int size) {
            return new ResponseCountdown[size];
        }
    };

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeLong(c);
        if (currentSec == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(currentSec);
        }
    }
}
