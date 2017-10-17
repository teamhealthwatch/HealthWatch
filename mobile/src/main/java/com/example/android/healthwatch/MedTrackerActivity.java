package com.example.android.healthwatch;

import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MedTrackerActivity extends AppCompatActivity implements View.OnClickListener {


    FloatingActionButton floatingButton;
    MedCustomAdapter adapter;
    public ArrayList<MedModel> CustomListViewValuesArr = new ArrayList<MedModel>();
    ListView listView;
    String allTime;
    String allDate;
    String MedName;
    String Dosage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_tracker);
        listView = (ListView)findViewById(R.id.listview);
        floatingButton = (FloatingActionButton)findViewById(R.id.fabButton);
        floatingButton.setOnClickListener(this);

        displayMeds();
        Resources res =getResources();
        adapter=new MedCustomAdapter( this, CustomListViewValuesArr,res );
        listView.setAdapter( adapter );


    }
    public void conditionClick(View v)
    {
        startActivity(new Intent(MedTrackerActivity.this, MedConditionActivity.class));
    }

    @Override
    public void onClick(View v) {

        if(v == floatingButton)
        {
            Intent intent = new Intent(this, MedicationForm.class);
            startActivityForResult(intent, 999);
            finish();
        }

    }

    public void displayMeds()
    {

        Intent intent = getIntent();
          if(intent.hasExtra("NAME"))
          {
              MedName = getIntent().getExtras().getString("NAME");
              allTime = getIntent().getExtras().getString("TIME");
              allDate = getIntent().getExtras().getString("DATE");
              Dosage = getIntent().getExtras().getString("DOSAGE");;
//              Log.i("Name", MedName + allTime + allDate + Dosage);
              setListData();
          }

    }

    private void setListData() {

        final MedModel sched = new MedModel();
        sched.setName(MedName);
        sched.setDate(allDate);
        sched.setTime(allTime);
        sched.setDosage(Dosage);

        CustomListViewValuesArr.add( sched );

    }

    public void onItemClick(int mPosition)
    {
        MedModel tempValues = ( MedModel ) CustomListViewValuesArr.get(mPosition);

        Toast.makeText(this, " "+tempValues.getName()
                        +"Time:"+tempValues.getTime()
                        +"Date:"+tempValues.getDate()
                        +"Dosage:"+tempValues.getDosage(),
                Toast.LENGTH_SHORT).show();
        Log.i("TAG NAME: " , tempValues.getName());

    }
}
