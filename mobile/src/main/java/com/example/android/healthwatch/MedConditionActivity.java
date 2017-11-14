package com.example.android.healthwatch;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MedConditionActivity extends AppCompatActivity implements View.OnClickListener {

    TextView Med_Cond;
    TextView Allergies;
    TextView Current_Med;
    TextView Blood_type;
    TextView Other;

    TextView Med_Cond1;
    TextView Allergies1;
    TextView Current_Med1;
    TextView Blood_type1;
    TextView Other1;

    FloatingActionButton floatingButton;
    boolean fabButtonVisibility;

    String medcond_;
    String allergies_;
    String curr_med_;
    String blood_type_;
    String other_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_condition);

        Med_Cond = (TextView) findViewById(R.id.MedCondShow1);
        Allergies = (TextView) findViewById(R.id.Allergies1);
        Current_Med = (TextView) findViewById(R.id.curt_med1);
        Blood_type = (TextView) findViewById(R.id.bloodtype1);
        Other = (TextView) findViewById(R.id.other1);

        Med_Cond1 = (TextView) findViewById(R.id.MedCondShow);
        Allergies1 = (TextView) findViewById(R.id.Allergies);
        Current_Med1 = (TextView) findViewById(R.id.curt_med);
        Blood_type1 = (TextView) findViewById(R.id.bloodtype);
        Other1 = (TextView) findViewById(R.id.other);

        conditions();
        floatingButton = (FloatingActionButton)findViewById(R.id.fabButton3);
        floatingButton.setOnClickListener(this);




    }

    public void homeClick(View v)
    {
        startActivity(new Intent(MedConditionActivity.this, HomePageActivity.class));
    }

    @Override
    public void onClick(View v) {

        if(v == floatingButton)
        {
            Intent intent = new Intent(this, EmergencyInfo.class);
            startActivityForResult(intent, 999);
            finish();
        }

    }

    public void conditions()
    {
        Intent intent = getIntent();
        if(intent.hasExtra("MEDCOND"))
        {
            visiblity(false);
            medcond_ = getIntent().getExtras().getString("MEDCOND");
            Med_Cond.setText(medcond_); ;
            allergies_ = getIntent().getExtras().getString("ALLERGY");
            Allergies.setText(allergies_); ;
            curr_med_ = getIntent().getExtras().getString("CURRENTMED");
            Current_Med.setText(curr_med_); ;
            blood_type_ = getIntent().getExtras().getString("BLOODTYPE");
            Blood_type.setText(blood_type_);
            other_ = getIntent().getExtras().getString("OTHER");
            Other.setText(other_);
        }
        else {
            visiblity(true);
        }
    }

    public void visiblity(boolean cond)
    {
        if(cond)
        {
            Med_Cond1.setText("NO DATA");
            Allergies1.setVisibility(View.INVISIBLE);
            Current_Med1.setVisibility(View.INVISIBLE);
            Blood_type1.setVisibility(View.INVISIBLE);
            Other1.setVisibility(View.INVISIBLE);

            Med_Cond.setVisibility(View.INVISIBLE);
            Allergies.setVisibility(View.INVISIBLE);
            Current_Med.setVisibility(View.INVISIBLE);
            Blood_type.setVisibility(View.INVISIBLE);
            Other.setVisibility(View.INVISIBLE);
            fabButtonVisibility = true;

        }
        else
        {
            Med_Cond1.setText("Medical conditions");
            Allergies1.setVisibility(View.VISIBLE);
            Current_Med1.setVisibility(View.VISIBLE);
            Blood_type1.setVisibility(View.VISIBLE);
            Other1.setVisibility(View.VISIBLE);

            Med_Cond.setVisibility(View.VISIBLE);
            Allergies.setVisibility(View.VISIBLE);
            Current_Med.setVisibility(View.VISIBLE);
            Blood_type.setVisibility(View.VISIBLE);
            Other.setVisibility(View.VISIBLE);
//            floatingButton.setVisibility(View.GONE);
            fabButtonVisibility = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(fabButtonVisibility == false)
        {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
            return true;
        }
       return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Toast.makeText(this, "Edit =)", Toast.LENGTH_SHORT).show();
//                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
