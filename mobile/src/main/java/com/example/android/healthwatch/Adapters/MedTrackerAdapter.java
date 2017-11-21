package com.example.android.healthwatch.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.example.android.healthwatch.Activities.MedTrackerActivity;
import com.example.android.healthwatch.Model.MedModel;
import com.example.android.healthwatch.R;

import java.util.ArrayList;

/**
 * Created by faitholadele on 10/11/17.
 */

public class MedTrackerAdapter extends ArrayAdapter<MedModel> implements View.OnClickListener {

    private Activity activity;
    private ArrayList<MedModel> dataSet;
    private static LayoutInflater inflater=null;
    public Resources res;
    MedModel tempValues;
    Context mContext;

    public MedTrackerAdapter(Activity a, ArrayList<MedModel> data, Context context) {
        super(context, R.layout.med_item, data);
        this.dataSet = data;
        this.mContext = context;
        activity = a;


    }
    public static class ViewHolder{

        public TextView _time;
        public TextView _date;
        public TextView _dosage;
        public TextView _name;
        public Switch _alarmbttn;

    }

    @Override
    public int getCount() {
        if (dataSet.size() <= 0)
            return 1;
        return dataSet.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        MedModel dataModel = getItem(position);
        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.med_item, parent, false);
            holder = new ViewHolder();
            holder._name = (TextView) convertView.findViewById(R.id.Name);
            holder._date = (TextView) convertView.findViewById(R.id.Date);
            holder._time = (TextView) convertView.findViewById(R.id.Time);
            holder._dosage = (TextView) convertView.findViewById(R.id.Dosage);
            holder._alarmbttn = (Switch) convertView.findViewById(R.id.alarm_switch);
            holder._alarmbttn.setChecked(true);
            holder._alarmbttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Switch tb = (Switch)v;
                    MedTrackerActivity mt = (MedTrackerActivity) activity;
                    if(tb.isChecked())
                    {
                        mt.getAlarmPosition(position);
                        Log.i(" TAG ", "on");

                    }
                    else
                    {
                        mt.cancelAlarm(position);
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

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        MedModel mm = (MedModel)object;
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
