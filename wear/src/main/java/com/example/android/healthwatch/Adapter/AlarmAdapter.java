package com.example.android.healthwatch.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.healthwatch.R;

import java.util.ArrayList;

/**
 * Created by Yan Tan on 10/17/2017.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder>{

    private ArrayList<String> alarmList;
    private Context context;

    private AlarmClickListener alarmClickListener;

    public AlarmAdapter(ArrayList<String> alarmList, Context context, AlarmClickListener alarmClickListener) {
        this.alarmList = alarmList;
        this.context = context;
        this.alarmClickListener = alarmClickListener;
    }

    @Override
    public AlarmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_list, parent, false);
        return new AlarmAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AlarmAdapter.ViewHolder holder, int position) {
        String currentAlarm = alarmList.get(position);

        holder.timeView.setText(currentAlarm);
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView timeView;

        private Button editButton;
        private Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);

            timeView = itemView.findViewById(R.id.time_view);

            // edit and delete button
            editButton = itemView.findViewById(R.id.edit_alarm_button);
            deleteButton = itemView.findViewById(R.id.delete_alarm_button);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alarmClickListener.onEditClickListener(editButton, getLayoutPosition());
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alarmClickListener.onDeleteClickListener(deleteButton, getLayoutPosition());
                }
            });
        }


    }

    public interface AlarmClickListener {

        void onEditClickListener(View v, int pos);

        void onDeleteClickListener(View v, int pos);

    }
}
