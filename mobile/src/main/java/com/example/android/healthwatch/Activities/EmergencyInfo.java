package com.example.android.healthwatch.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.healthwatch.R;

public class EmergencyInfo extends AppCompatActivity {

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

        Intent intent = new Intent(this, MedConditionActivity.class);
        intent.putExtra("MEDCOND", medcond_);
        intent.putExtra("ALLERGY", allergies_);
        intent.putExtra("CURRENTMED", curr_med_);
        intent.putExtra("BLOODTYPE", blood_type_);
        intent.putExtra("OTHER", other_);

        startActivity(intent);
    }
}
