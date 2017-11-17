package com.example.android.healthwatch.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.healthwatch.Activities.MedTrackerActivity;
import com.example.android.healthwatch.Model.MedModel;
import com.example.android.healthwatch.R;

import java.util.ArrayList;

/**
 * Created by faitholadele on 10/11/17.
 */

public class MedTrackerAdapter extends BaseAdapter implements View.OnClickListener {

    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    MedModel tempValues=null;

    public MedTrackerAdapter(Activity a, ArrayList d, Resources resLocal) {

        activity = a;
        data=d;
        res = resLocal;
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    public static class ViewHolder{

        public TextView _time;
        public TextView _date;
        public TextView _dosage;
        public TextView _name;
        public ToggleButton _alarmbttn;

    }

    @Override
    public int getCount() {
        if(data.size()<=0)
            return 1;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            vi = inflater.inflate(R.layout.med_layout, null);
            holder = new ViewHolder();
            holder._name = (TextView) vi.findViewById(R.id.Name);
            holder._date = (TextView) vi.findViewById(R.id.Date);
            holder._time = (TextView) vi.findViewById(R.id.Time);
            holder._dosage = (TextView) vi.findViewById(R.id.Dosage);
            holder._alarmbttn = (ToggleButton) vi.findViewById(R.id.alarmToggle);
            holder._alarmbttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToggleButton tb = (ToggleButton)v;
                    MedTrackerActivity mt = (MedTrackerActivity) activity;
                    if(tb.isChecked())
                    {
                        mt.turnAlarmOnOrOff(position, true);
                        Log.i(" TAG: ", "on");
                    }
                    else
                    {
                        mt.turnAlarmOnOrOff(position, false);
                        Log.i(" TAG: ", "off");
                    }
                }
            });

            vi.setTag( holder );
        }
        else {
            holder=(ViewHolder)vi.getTag();
        }

        if(data.size()<=0){

            holder._name.setText("No Data");
            holder._alarmbttn.setVisibility(View.INVISIBLE);

        }
        else
        {
            tempValues=null;
            tempValues = ( MedModel ) data.get( position );

            holder._name.setText( tempValues.getName() );
            holder._date.setText( tempValues.getDate() );
            holder._time.setText( tempValues.getTime() );
            holder._dosage.setText( tempValues.getDosage() );
            holder._alarmbttn.setVisibility(View.VISIBLE);

            vi.setOnClickListener(new OnItemClickListener( position ));
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("EmergencyContactAdapter", "=====Row button clicked=====");
    }

    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View view) {
            MedTrackerActivity sct = (MedTrackerActivity) activity;
            sct.onItemClick(mPosition);
        }
    }
}
