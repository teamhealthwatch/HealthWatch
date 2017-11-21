package com.example.android.healthwatch.Adapter;

import android.content.Context;
import android.support.wear.widget.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.healthwatch.Contact;
import com.example.android.healthwatch.R;

import java.util.ArrayList;

/**
 * Created by Yan Tan on 10/22/2017.
 */

public class ContactAdapter extends WearableRecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private ArrayList<Contact> contactList;
    private Context context;

    public ContactAdapter(ArrayList<Contact> contactList, Context context) {
        this.contactList = contactList;
        this.context = context;
    }

    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        holder.setName(contact.getName());
        holder.setNum(contact.getPhoneNumber());
        holder.setPrimary(contact.getPrimary());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
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

        public void setName(String name) {
            nameView.setText(name);
        }

        public void setNum(String name) {
            numView.setText(name);
        }

        public void setPrimary(boolean b) {
            if (b)
                primaryView.setText("Primary");
            else
                primaryView.setText("");
        }
    }
}
