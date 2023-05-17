package edu.acg.ssh.androidproject_02;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class GuestsAdapter extends ArrayAdapter<Guest> {

    public GuestsAdapter(Context context, ArrayList<Guest> guests){super(context,0,guests);}

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Guest guest = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_guest, parent, false);
        }

        TextView id = (TextView) convertView.findViewById(R.id.id);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView surname = (TextView) convertView.findViewById(R.id.surname);

        id.setText(String.valueOf(guest.getId()));
        name.setText(guest.getName());
        surname.setText(guest.getSurname());

        return convertView;
    }



}
