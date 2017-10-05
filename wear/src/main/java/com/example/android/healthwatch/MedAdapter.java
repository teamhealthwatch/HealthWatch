package com.example.android.healthwatch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
     * Created by Yan Tan on 9/28/2017.
 */

public class MedAdapter extends WearableRecyclerView.Adapter<MedAdapter.ViewHolder>{

    private ArrayList<Medication> medList;
    private Context context;

    public MedAdapter(ArrayList<Medication> medList, Context context) {
        this.medList = medList;
        this.context = context;
    }

    @Override
    public MedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.med_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MedAdapter.ViewHolder holder, int position) {
        Medication medication = medList.get(position);

        holder.titleView.setText(medication.getMedName());
        holder.dosageView.setText(medication.getDosage());
    }


    @Override
    public int getItemCount() {
        return medList.size();
    }

    public class ViewHolder extends WearableRecyclerView.ViewHolder{

        public TextView titleView;
        public TextView dosageView;

        public ViewHolder(View itemView) {
            super(itemView);

            titleView = (TextView)itemView.findViewById(R.id.med_title_view);
            dosageView = (TextView)itemView.findViewById(R.id.dosage_view);
        }
    }

}
