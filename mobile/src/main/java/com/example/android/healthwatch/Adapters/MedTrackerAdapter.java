package com.example.android.healthwatch.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.healthwatch.Activities.MedTrackerActivity;
import com.example.android.healthwatch.Model.MedModel;
import com.example.android.healthwatch.R;

import java.util.ArrayList;

/**
 * Created by faitholadele on 10/11/17.
 */

public class MedTrackerAdapter extends ArrayAdapter<MedModel> implements View.OnClickListener {

    MedModel tempValues;
    private ArrayList<MedModel> dataSet;
    Context mContext;


    public MedTrackerAdapter(ArrayList<MedModel> data, Context context) {
        super(context, R.layout.med_item, data);
        this.dataSet = data;
        this.mContext=context;
    }
    public static class ViewHolder{

        public TextView _time;
        public TextView _date;
        public TextView _dosage;
        public TextView _name;
        public ToggleButton _alarmbttn;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        MedModel dataModel = getItem(position);
        ViewHolder holder;

        if(convertView==null){

            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.med_item, parent, false);

            holder._name = (TextView) convertView.findViewById(R.id.Name);
            holder._date = (TextView) convertView.findViewById(R.id.Date);
            holder._time = (TextView) convertView.findViewById(R.id.Time);
            holder._dosage = (TextView) convertView.findViewById(R.id.Dosage);
            holder._alarmbttn = (ToggleButton) convertView.findViewById(R.id.alarmToggle);
            holder._alarmbttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToggleButton tb = (ToggleButton)v;
                    MedTrackerActivity mt = new MedTrackerActivity();
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

            convertView.setTag( holder );
        }
        else {
            holder=(ViewHolder)convertView.getTag();
        }

        if(dataSet.size()<=0){

            holder._name.setText("No Data");
            holder._alarmbttn.setVisibility(View.INVISIBLE);

        }
        else
        {

            holder._name.setText( dataModel.getName() );
            holder._date.setText( dataModel.getDate() );
            holder._time.setText( dataModel.getTime() );
            holder._dosage.setText( dataModel.getDosage() );
            holder._alarmbttn.setVisibility(View.VISIBLE);

            convertView.setOnClickListener(new OnItemClickListener( position ));
        }
        return convertView;
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
            MedTrackerActivity sct = new MedTrackerActivity();
            sct.onItemClick(mPosition);
        }
    }
}