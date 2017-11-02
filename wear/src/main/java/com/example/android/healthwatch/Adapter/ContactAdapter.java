package com.example.android.healthwatch.Adapter;

import android.content.Context;
import android.support.wear.widget.WearableRecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.healthwatch.Model.EmergencyContact;
import com.example.android.healthwatch.R;

import java.util.ArrayList;

/**
 * Created by Yan Tan on 10/22/2017.
 */

public class ContactAdapter extends WearableRecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private ArrayList<EmergencyContact> contactList;
    private Context context;

    public ContactAdapter(ArrayList<EmergencyContact> contactList, Context context) {
        this.contactList = contactList;
        this.context = context;
    }

    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends WearableRecyclerView.ViewHolder {

        public TextView nameView;
        public TextView numView;
        public TextView primaryView;


        public ViewHolder(View itemView) {
            super(itemView);

            nameView = itemView.findViewById(R.id.name_view);
            numView = itemView.findViewById(R.id.num_view);
            primaryView = itemView.findViewById(R.id.primary_view);

        }

        public TextView getNameView() {
            return nameView;
        }

        public TextView getNumView() {
            return numView;
        }

        public TextView getPrimaryView() {
            return primaryView;
        }
    }
}
