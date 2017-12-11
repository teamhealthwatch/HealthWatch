package com.example.android.healthwatch.Adapter;

import android.content.Context;
import android.support.wear.widget.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.healthwatch.Model.MedInfo;
import com.example.android.healthwatch.R;

import java.util.ArrayList;

/**
 * Created by Yan Tan on 12/9/2017.
 */

public class InfoAdapter extends WearableRecyclerView.Adapter<InfoAdapter.ViewHolder>{

    ArrayList<MedInfo> infoList;
    private Context context;

    public InfoAdapter(ArrayList<MedInfo> infoList, Context context) {
        this.infoList = infoList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MedInfo medInfo = infoList.get(position);
        holder.setTitle(medInfo.getTitle());
        holder.setInput(medInfo.getInput());
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends WearableRecyclerView.ViewHolder {

        public TextView titleView;
        public TextView inputView;


        public ViewHolder(View itemView) {
            super(itemView);

            titleView = itemView.findViewById(R.id.info_title_view);
            inputView = itemView.findViewById(R.id.input_view);


        }

        public void setTitle(String name) {
            titleView.setText(name);
        }

        public void setInput(String name) {
            inputView.setText(name);
        }

    }
}
