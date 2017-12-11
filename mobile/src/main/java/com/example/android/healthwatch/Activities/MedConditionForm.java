package com.example.android.healthwatch.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.healthwatch.Fragments.AlarmFragment;
import com.example.android.healthwatch.R;

import static android.view.View.VISIBLE;
/*
Form used to get user input. LoC = 96
 */
public class MedConditionForm extends AppCompatActivity {

    EditText Med_Cond;
    EditText Allergies;
    EditText Current_Med;
    EditText Blood_type;
    EditText Other;

    String medcond_;
    String allergies_;
    String curr_med_;
    String blood_type_;
    String other_;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_info);

        Med_Cond = (EditText) findViewById(R.id.MedCondTxt);
        Allergies = (EditText) findViewById(R.id.Allergiestxt);
        Current_Med = (EditText) findViewById(R.id.curt_medtxt);
        Blood_type = (EditText) findViewById(R.id.bloodtypetxt);
        Other = (EditText) findViewById(R.id.othertxt);
        handleEdits();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                getInfoForIntent();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getInfoForIntent() {

        medcond_ = Med_Cond.getText().toString();
        allergies_ = Allergies.getText().toString();
        curr_med_ = Current_Med.getText().toString();
        blood_type_ = Blood_type.getText().toString();
        other_ = Other.getText().toString();

        if(medcond_.isEmpty())
        {
            medcond_ = " ";
        }
        else if(allergies_.isEmpty())
        {
            allergies_ = " ";
        }
        else if(curr_med_.isEmpty())
        {
            curr_med_ = " ";
        }
        else if(blood_type_.isEmpty())
        {
            blood_type_ = " ";
        }
        else if(other_.isEmpty())
        {
            other_ = " ";
        }

        Intent intent = new Intent(this, MedConditionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("MEDCOND", medcond_);
        bundle.putString("ALLERGY", allergies_);
        bundle.putString("CURRENTMED", curr_med_);
        bundle.putString("BLOODTYPE", blood_type_);
        bundle.putString("OTHER", other_);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void handleEdits()
    {
        Intent intent = this.getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null)
        {
            Log.i("extra", "reaching");
            medcond_ = extras.getString("MEDCOND");
            Med_Cond.setText(medcond_);
            allergies_ = extras.getString("ALLERGY");
            Allergies.setText(allergies_);
            curr_med_ = extras.getString("CURRENTMED");
            Current_Med.setText(curr_med_);
            blood_type_ = extras.getString("BLOODTYPE");
            Blood_type.setText(blood_type_);
            other_ = extras.getString("OTHER");
            Other.setText(other_);
            Button bttn = (Button) findViewById(R.id.deleteInfo);
            bttn.setVisibility(VISIBLE);
            bttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    medcond_ = " ";
                    allergies_ = " ";
                    curr_med_ = " ";
                    blood_type_ = " ";
                    other_ = " ";

                    String delete = "true";
                    Intent intent = new Intent(getApplicationContext(), MedConditionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("MEDCOND", medcond_);
                    bundle.putString("ALLERGY", allergies_);
                    bundle.putString("CURRENTMED", curr_med_);
                    bundle.putString("BLOODTYPE", blood_type_);
                    bundle.putString("OTHER", other_);
                    bundle.putString("DELETE", delete);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }
}
