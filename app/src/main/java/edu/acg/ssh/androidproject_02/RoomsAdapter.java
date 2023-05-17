package edu.acg.ssh.androidproject_02;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RoomsAdapter extends ArrayAdapter<Room> {

    public RoomsAdapter(Context context, ArrayList<Room> rooms){
        super(context, 0, rooms);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Room room = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_room, parent, false);
        }


        // Lookup view for data population
        TextView number = (TextView) convertView.findViewById(R.id.id);
        TextView capacity = (TextView) convertView.findViewById(R.id.name);
        TextView beds = (TextView) convertView.findViewById(R.id.surname);
        TextView price = (TextView) convertView.findViewById(R.id.total);

        number.setText(String.valueOf(room.getNumber()));
        capacity.setText(String.valueOf(room.getCapacity()));
        beds.setText(String.valueOf(room.getBeds()));

        String priceS = "€" + String.valueOf(room.getTotal());
        price.setText(priceS);


        return convertView;
    }

}
