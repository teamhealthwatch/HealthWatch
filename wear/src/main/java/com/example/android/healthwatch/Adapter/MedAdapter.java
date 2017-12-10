package com.example.android.healthwatch.Adapter;

import android.content.Context;
import android.support.wear.widget.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.healthwatch.Model.MedModel;
import com.example.android.healthwatch.Model.Medication;
import com.example.android.healthwatch.R;

import java.util.ArrayList;

/**
     * Created by Yan Tan on 9/28/2017.
 */

public class MedAdapter extends WearableRecyclerView.Adapter<MedAdapter.ViewHolder>{

    private ArrayList<MedModel> medList;
    private Context context;
    private MedClickListener medClickListener;


    public MedAdapter(ArrayList<MedModel> medList, Context context, MedClickListener medClickListener) {
        this.medList = medList;
        this.context = context;
        this.medClickListener = medClickListener;
    }

    @Override
    public MedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.med_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MedAdapter.ViewHolder holder, int position) {
        MedModel medication = medList.get(position);

        holder.titleView.setText(medication.getName());

    }


    @Override
    public int getItemCount() {
        return medList.size();
    }

    public class ViewHolder extends WearableRecyclerView.ViewHolder{

        public TextView titleView;


        public ViewHolder(View itemView) {
            super(itemView);

            titleView = itemView.findViewById(R.id.med_title_view);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    medClickListener.onMedClickListener(getLayoutPosition());
                }
            });

        }


        public void setOnMedClickListener(MedClickListener newMedClickListener){
            medClickListener = newMedClickListener;
        }


    }

    public interface MedClickListener{

        void onMedClickListener(int pos);
    }

}
