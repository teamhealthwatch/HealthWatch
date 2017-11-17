package com.example.android.healthwatch.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.example.android.healthwatch.Model.Contact;
import com.example.android.healthwatch.R;

import java.util.ArrayList;


/**
 * Created by anupamchugh on 09/02/16.
 */
public class EmergencyContactAdapter extends ArrayAdapter<Contact> implements View.OnClickListener{

    private ArrayList<Contact> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolderItem {
        TextView txtName;
        TextView txtPhoneNumber;
        Switch pContact;
    }



//    public EmergencyContactAdapter(ArrayList<Contact> data, Context context) {
    public EmergencyContactAdapter(ArrayList<Contact> data, Context context) {
        super(context, R.layout.contact_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Contact contact = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolderItem viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.contact_item, parent, false);

            viewHolder = new ViewHolderItem();
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtPhoneNumber = (TextView) convertView.findViewById(R.id.phone_number);
            viewHolder.pContact = (Switch) convertView.findViewById(R.id.simpleSwitch);
            if(contact.primaryContact){
                viewHolder.pContact.setChecked(true);
            }

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
            //result=convertView;
        }

        viewHolder.txtName.setText(contact.getName());
        //viewHolder.txtType.setText(contact.getType());
        viewHolder.txtPhoneNumber.setText(contact.getPhoneNumber());
        viewHolder.pContact.setOnClickListener(this);
        viewHolder.pContact.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }

    public void onClick(View v){
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Contact dataModel=(Contact)object;

        switch (v.getId()){
            case R.id.simpleSwitch:
                Switch button = (Switch) v.getTag();
                if(dataModel.primaryContact){
                    dataModel.primaryContact = false;
                    updateList(dataModel, false);
                }
                if(!button.isChecked()){
                    button.setChecked(true);
                }
        }
    }

    private void updateList(Contact c, boolean isSwitched){
        for (Contact listContact: dataSet) {
            if(listContact.getName().equals(c.getName())){
                listContact.primaryContact = isSwitched;
            }
        }
    }
    public ArrayList<Contact> getList(){
        return dataSet;
    }


}