package com.example.android.healthwatch.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.healthwatch.DatabaseHelper;
import com.example.android.healthwatch.Model.MedInfoModel;
import com.example.android.healthwatch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MedConditionActivity extends AppCompatActivity implements View.OnClickListener, DatabaseHelper.MedInfoCallback {

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
    boolean fabIsAdd;
    private FirebaseAuth mAuth;

    String medcond_;
    String allergies_;
    String curr_med_;
    String blood_type_;
    String other_;

    String login;
    boolean firstTime;

    DatabaseHelper dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_condition);
        mAuth = FirebaseAuth.getInstance();
        firstTime = false;
        Intent intent = getIntent();
        login = intent.getExtras().getString("login");
        if(intent.hasExtra("Not_Registered")){
            firstTime = true;
        }

        floatingButton = (FloatingActionButton)findViewById(R.id.fabButton3);
        floatingButton.show();
        floatingButton.setOnClickListener(this);

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

        dh = new DatabaseHelper();
        dh.registerMedInfoCallback(this);
        dh.getMedConditions(login);

        fabIsAdd = true;
        hasInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 999){
            if(resultCode == RESULT_OK){
                conditions(data);
            }
        }
        else if(requestCode == 998)
        {
            if(resultCode == RESULT_OK) {
                deleteMedInfo(data);
            }
        }
    }



    private void storeMedConditions() {

        final String medcond  = medcond_;
        final String allergies = allergies_;
        final String currmed = curr_med_;
        final String bloodtype = blood_type_;
        final String other = other_;

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("medInfo").child(login).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference();
                MedInfoModel medInfo = new MedInfoModel(medcond, allergies, currmed, bloodtype, other);
                usersRef.child("medInfo").child(login).setValue(medInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void hasInfo() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("medInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(login))
                {
                    floatingButton.setImageResource(R.drawable.ic_create_white_24dp);
                    fabIsAdd = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void homeClick(View v)
    {
        startActivity(new Intent(MedConditionActivity.this, HomePageActivity.class));
    }

    @Override
    public void onClick(View v) {

        if(v == floatingButton && fabIsAdd == true)
        {
            Intent intent = new Intent(this, MedConditionForm.class);
            startActivityForResult(intent, 999);
        }
        else if(v == floatingButton && fabIsAdd == false)
        {
            Log.i("fab", "reaching");
            Intent intent = new Intent(this, MedConditionForm.class);
            Bundle bundle = new Bundle();
            bundle.putString("MEDCOND", medcond_);
            bundle.putString("ALLERGY", allergies_);
            bundle.putString("CURRENTMED", curr_med_);
            bundle.putString("BLOODTYPE", blood_type_);
            bundle.putString("OTHER", other_);
            intent.putExtras(bundle);
            startActivityForResult(intent, 998);
        }

    }

    public void setMedConditions()
    {
        Med_Cond.setText(medcond_);
        Allergies.setText(allergies_);
        Current_Med.setText(curr_med_);
        Blood_type.setText(blood_type_);
        Other.setText(other_);
    }

    public void conditions(Intent data)
    {
        Bundle extras = data.getExtras();
        if(extras != null){
            visiblity(false);
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
            storeMedConditions();
        }
        else{
//            Toast.makeText(MedConditionActivity.this,"Something went wrong.",Toast.LENGTH_LONG).show();
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
            fabIsAdd = true;
            floatingButton = (FloatingActionButton)findViewById(R.id.fabButton3);
            floatingButton.show();
            floatingButton.setOnClickListener(this);


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
            fabIsAdd = false;
            floatingButton = (FloatingActionButton)findViewById(R.id.fabButton3);
            floatingButton.show();
            floatingButton.setImageResource(R.drawable.ic_create_white_24dp);
            floatingButton.setOnClickListener(this);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(firstTime){
            floatingButton = (FloatingActionButton)findViewById(R.id.fabButton3);
            floatingButton.show();
            floatingButton.setOnClickListener(this);
//            visiblity(true);
            MenuInflater mi = getMenuInflater();
            mi.inflate(R.menu.menu_finish, menu);
            return super.onCreateOptionsMenu(menu);
        }
        else
        {
            if(fabIsAdd == false)
            {
                fabIsAdd = false;
                floatingButton = (FloatingActionButton)findViewById(R.id.fabButton3);
                floatingButton.show();
                floatingButton.setImageResource(R.drawable.ic_create_white_24dp);
                floatingButton.setOnClickListener(this);
                getMenuInflater().inflate(R.menu.menu, menu);
                return true;
            }

        }
       return false;
    }


    private void deleteMedInfo(Intent data) {
        floatingButton = (FloatingActionButton)findViewById(R.id.fabButton3);
        Bundle extras = data.getExtras();
        if(extras != null) {

            if(extras.containsKey("DELETE"))
            {
                fabIsAdd = true;
                floatingButton.setImageResource(R.drawable.fab_button);
                floatingButton.show();
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
                Log.i("deleting..", "DELETE");
                dh.deleteMedConditions(login);
            }
            else
            {
                fabIsAdd = false;
                floatingButton.setImageResource(R.drawable.ic_create_white_24dp);
                floatingButton.show();
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
                dh.updateMedConditions(login, medcond_, allergies_, curr_med_, blood_type_, other_);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_finish:
                Toast.makeText(this, "All Done!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, HomePageActivity.class);
                startActivity(intent);
            case R.id.hmpg:
                Toast.makeText(this, "Homepage", Toast.LENGTH_SHORT).show();
                Intent intt = new Intent(this, HomePageActivity.class);
                intt.putExtra("login", login);
                startActivity(intt);
                return true;
            case R.id.med_tracker:
                Toast.makeText(this, "Medication Tracker", Toast.LENGTH_SHORT).show();
                Intent inten = new Intent(this, MedTrackerActivity.class);
                inten.putExtra("login", login);
                startActivity(inten);
                return true;
            case R.id.contact:
                Toast.makeText(this, "Emergency Contact", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, EmergencyContactActivity.class);
                intent2.putExtra("login", login);
                startActivity(intent2);
                return true;
            case R.id.info:
                Toast.makeText(this, "Personal Info", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(this, MedConditionActivity.class);
                intent3.putExtra("login", login);
                startActivity(intent3);
                return true;
            case R.id.acct:
                Toast.makeText(this, "Account", Toast.LENGTH_SHORT).show();
                Intent intent5 = new Intent(this, AccountActivity.class);
                startActivity(intent5);
                return true;
            case R.id.history:
                Toast.makeText(this, "Medication History", Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(this, MainActivity.class);
                intent4.putExtra("login", login);
                startActivity(intent4);
                return true;
            case R.id.signout:
                Toast.makeText(this, "Signing out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void medInfoValues(String medCond, String allergies, String medications, String bloodType, String other) {
        medcond_ = medCond;
        allergies_ = allergies;
        curr_med_ = medications;
        blood_type_ = bloodType;
        other_ = other;
        setMedConditions();
    }
}
