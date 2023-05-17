package edu.acg.ssh.androidproject_02;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ReservationsAdapter  extends ArrayAdapter<Reservation> {

    public ReservationsAdapter(Context context, ArrayList<Reservation> reservations){super(context,0, reservations);}

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Reservation reservation = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_reservation, parent, false);
        }

        TextView id = (TextView) convertView.findViewById(R.id.id);
        TextView roomText = (TextView) convertView.findViewById(R.id.room);
        TextView guestText = (TextView) convertView.findViewById(R.id.surname);
        TextView fromText = (TextView) convertView.findViewById(R.id.from);
        TextView toText = (TextView) convertView.findViewById(R.id.name);


        id.setText(String.valueOf(reservation.getId()));
        roomText.setText(String.valueOf(reservation.getRoomNum()));
        guestText.setText(reservation.getGuestName());
        fromText.setText(reservation.getFromS());
        toText.setText(reservation.getToS());


        return convertView;
    }


}
