package com.example.android.healthwatch.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.healthwatch.Activities.EmergencyContactActivity;
import com.example.android.healthwatch.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Ryan on 10/18/2017.
 */

public class EmergencyContactFragment extends DialogFragment implements View.OnClickListener{

    private Button add;
    private Button cancel;
    //private String fullName;
    //private String phoneNumber;
    //private Boolean primaryContact;
    private TextView editTextFullName;
    private TextView editTextPhoneNumber;
    private CheckBox pc;
    private String login;

    public EmergencyContactFragment(){

    }

    public static EmergencyContactFragment newInstance(String title){
        EmergencyContactFragment frag = new EmergencyContactFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_emergency_popup, container);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        add = view.findViewById(R.id.addbttn);
        add.setOnClickListener(this);
        cancel = view.findViewById(R.id.cancelbttn);
        cancel.setOnClickListener(this);
        editTextFullName = (EditText) view.findViewById(R.id.emName);
        editTextPhoneNumber = (EditText) view.findViewById(R.id.phonenumber);
        editTextPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        pc = (CheckBox) view.findViewById(R.id.PrimaryCont);


    }

    public void getFormData(){
        try {
            EmergencyContactActivity callingActivity = (EmergencyContactActivity) getActivity();
            Intent myIntent = new Intent();
            Bundle contact = new Bundle();
            if(!validateForm()){
                return;
            }
            boolean checked = false;
            if(pc.isChecked()){
                checked = true;
            }
            contact.putString("fullName", editTextFullName.getText().toString());
            contact.putString("phoneNumber", editTextPhoneNumber.getText().toString());
            contact.putBoolean("pc", checked);
            //Toast.makeText(EmergencyContactFragment.this,contact.getString("fullName"),Toast.LENGTH_LONG).show();
            myIntent.putExtras(contact);
            callingActivity.onActivityResult(1, RESULT_OK, myIntent);
            dismiss();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //Used to ensure the input given by the user is correct and can be stored in the database
    private boolean validateForm() {
        boolean valid = true;

        String contactName = editTextFullName.getText().toString();
        if (TextUtils.isEmpty(contactName)) {
            editTextFullName.setError("Required.");
            valid = false;
        } else {
            editTextFullName.setError(null);
        }

        String phone = editTextPhoneNumber.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            editTextPhoneNumber.setError("Required.");
            valid = false;
        } else if (phone.length() < 14) {
            editTextPhoneNumber.setError("Must be 10 Digits.");
            valid = false;

        } else {
            editTextPhoneNumber.setError(null);
        }

        return valid;
    }



    @Override
    public void onClick(View v) {
        if(v == add){
            getFormData();
        }
        if(v == cancel){
            dismiss();
        }
    }
}
