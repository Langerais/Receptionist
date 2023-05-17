package edu.acg.ssh.androidproject_02;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ReserveActivity extends AppCompatActivity {

    public static LocalDate from;
    public static LocalDate to;
    private static boolean selectTo;
    private static RoomsAdapter adapter;
    private static ArrayList<Room> rooms;

    private static Room selectedRoom;
    private static Bundle reservationData;
    private static boolean searched;

    private static DbHelperRooms dbHelperRooms;
    private static DbHelperReservations dbHelperReservations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        searched = false;

        ConstraintLayout layout = findViewById(R.id.reserveLayout);
        AnimationDrawable backgroundAnimation = (AnimationDrawable) layout.getBackground();
        backgroundAnimation.start();

        selectTo = false;

        Button searchBtn = findViewById(R.id.searchBtn);
        ImageButton fromCalendarBtn = findViewById(R.id.imageButtonFrom);
        ImageButton toCalendarBtn = findViewById(R.id.imageButtonTo);
        DatePicker reserveDatePicker = findViewById(R.id.reserveDatePicker);
        TextView textViewFrom = findViewById(R.id.textViewFrom);
        TextView textViewTo = findViewById(R.id.textViewTo);

        reserveDatePicker.setMinDate(System.currentTimeMillis() - 1000);

        from = LocalDate.now();
        to = LocalDate.now().plusDays(1);

        ListView roomsListView = findViewById(R.id.roomsListView);

        dbHelperRooms = new DbHelperRooms(ReserveActivity.this);
        dbHelperReservations = new DbHelperReservations(ReserveActivity.this);

        resetRooms();   //Haven't done room create, so have to use this

        rooms = dbHelperRooms.getAllRooms();

        adapter = new RoomsAdapter(this, rooms);
        roomsListView.setAdapter(adapter);

        TextView fromToText = findViewById(R.id.fromToText);

        reserveDatePicker.setVisibility(View.GONE);

        textViewFrom.setText(from.format(DateTimeFormatter.ofPattern("dd   MMMM   yyyy")));
        textViewTo.setText(to.format(DateTimeFormatter.ofPattern("dd   MMMM   yyyy")));

        TextView textNumberSelected = findViewById(R.id.textNumberSelected);
        TextView textTotalPriceSelected = findViewById(R.id.textTotalPriceSelected);
        TextView textDaysSelected = findViewById(R.id.textDaysSelected);
        textDaysSelected.setText("1");

        ImageButton bookBtn = findViewById(R.id.bookBtn);
        bookBtn.setAlpha(0.1f);
        bookBtn.setClickable(false);

        Group selectedRoomGroup = findViewById(R.id.selectedRoomGroup);

        selectedRoomGroup.setVisibility(View.INVISIBLE);
        roomsListView.setVisibility(View.INVISIBLE);

        View headerView = getLayoutInflater().inflate(R.layout.rooms_table_header,null);
        roomsListView.addHeaderView(headerView);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        roomsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {            //ListView Room selection Listener
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selectedRoom = (Room) adapterView.getItemAtPosition(i);
                textNumberSelected.setText(String.valueOf(selectedRoom.getNumber()));

                double value = selectedRoom.getdPrice() * Util.daysBetween(from, to);
                String total = "€ " + String.format("%.2f",value); //TODO May have bugz here

                textTotalPriceSelected.setText(total);

                selectedRoomGroup.setVisibility(View.VISIBLE);


                bookBtn.setAlpha(1f);
                bookBtn.setClickable(true);

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedRoomGroup.setVisibility(View.INVISIBLE);
                roomsListView.setVisibility(View.VISIBLE);
                searched = true;

                reserveDatePicker.setVisibility(View.INVISIBLE);
                fromToText.setVisibility(View.INVISIBLE);

                bookBtn.setAlpha(0.1f);
                bookBtn.setClickable(false);

                searchBtn.setClickable(false);
                searchBtn.setAlpha(0.1f);

                textViewFrom.setText(from.format(DateTimeFormatter.ofPattern("dd  MMMM  yyyy")));
                textViewTo.setText(to.format(DateTimeFormatter.ofPattern("dd  MMMM  yyyy")));

                textDaysSelected.setText(String.valueOf(Util.daysBetween(from, to)));
                roomsListView.setVisibility(View.VISIBLE);

                rooms.clear();


                rooms = Util.getFreeRooms(
                        dbHelperRooms.getAllRooms(),
                        dbHelperReservations.getAllReservations(),
                        from,to
                );
                calculateTotalForAllRooms();

                adapter.clear();
                adapter = new RoomsAdapter(ReserveActivity.this, rooms);
                roomsListView.clearChoices();
                roomsListView.setAdapter(adapter);

            }
        });


        fromCalendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reserveDatePicker.getVisibility() == View.INVISIBLE || selectTo) {
                    reserveDatePicker.setVisibility(View.VISIBLE);
                    textViewFrom.setTypeface(null, Typeface.BOLD);
                    textViewTo.setTypeface(null, Typeface.NORMAL);
                    fromToText.setText(R.string.fromDate);
                    fromToText.setVisibility(View.VISIBLE);
                    roomsListView.setVisibility(View.INVISIBLE);



                } else {
                    reserveDatePicker.setVisibility(View.INVISIBLE);
                    textViewFrom.setTypeface(null, Typeface.NORMAL);
                    fromToText.setVisibility(View.INVISIBLE);

                    if(searched) {
                        roomsListView.setVisibility(View.INVISIBLE);
                    }

                }
                selectTo = false;
            }
        });

        toCalendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(reserveDatePicker.getVisibility() == View.INVISIBLE || !selectTo) {
                    reserveDatePicker.setVisibility(View.VISIBLE);
                    textViewTo.setTypeface(null, Typeface.BOLD);
                    textViewFrom.setTypeface(null, Typeface.NORMAL);

                    fromToText.setText(R.string.toDate);
                    fromToText.setVisibility(View.VISIBLE);

                    roomsListView.setVisibility(View.INVISIBLE);
                } else {
                    reserveDatePicker.setVisibility(View.INVISIBLE);
                    textViewTo.setTypeface(null, Typeface.NORMAL);
                    fromToText.setVisibility(View.INVISIBLE);

                    if(searched){
                        roomsListView.setVisibility(View.VISIBLE);
                    }
                }
                selectTo = true;
            }
        });

        reserveDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {

                String dateS;

                bookBtn.setAlpha(0.1f);
                bookBtn.setClickable(false);
                textNumberSelected.setText("NaN");
                textTotalPriceSelected.setText("NaN");
                textDaysSelected.setText(String.valueOf(Util.daysBetween(from, to)));
                roomsListView.setVisibility(View.INVISIBLE);

                searchBtn.setAlpha(1);
                searchBtn.setClickable(true);

                if(selectTo){

                    to = LocalDate.of(year, month + 1, day);
                    if(to.isBefore(from) || to.equals(from)){
                        textViewTo.setTextColor(Color.RED);
                        textViewFrom.setTextColor(Color.RED);
                        searchBtn.setAlpha(0.1f);
                        searchBtn.setClickable(false);
                    } else {
                        textViewFrom.setTextColor(textNumberSelected.getTextColors());
                        textViewTo.setTextColor(textNumberSelected.getTextColors());
                        searchBtn.setAlpha(1);
                        searchBtn.setClickable(true);
                    }

                    dateS = to.format(DateTimeFormatter.ofPattern("dd   MMMM   yyyy"));
                    textViewTo.setText(dateS);
                } else {

                    from = LocalDate.of(year, month + 1, day);
                    if(from.equals(to) || from.isAfter(to)){
                        textViewFrom.setTextColor(Color.RED);
                        textViewTo.setTextColor(Color.RED);
                        searchBtn.setAlpha(0.1f);
                        searchBtn.setClickable(false);
                    } else {
                        textViewFrom.setTextColor(textNumberSelected.getTextColors());
                        textViewTo.setTextColor(textNumberSelected.getTextColors());
                    }

                    dateS = from.format(DateTimeFormatter.ofPattern("dd   MMMM   yyyy"));
                    textViewFrom.setText(dateS);
                }


                int days = Util.daysBetween(from, to);
                if(days <= 0){
                    textDaysSelected.setText("NaN");
                } else {
                    textDaysSelected.setText(String.valueOf(days));
                }

            }
        });

        ActivityResultLauncher<Intent> guestReserveActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK ){
                    Intent data = result.getData();
                    Bundle reservationData = data.getExtras();
                    long reservationId = reservationData.getLong("reservationId", -1);
                    String name = reservationData.getString("guestName");
                    String surname = reservationData.getString("guestSurname");

                    StringBuilder msgBldr = new StringBuilder();

                    msgBldr.append(getString(R.string.reservation))
                            .append(" ").append(reservationId).append("\n ")
                            .append(getString(R.string.f)).append("\n ")
                            .append(name).append(" ").append(surname)
                            .append(" ").append(getString(R.string.created));

                    Toast.makeText(ReserveActivity.this, msgBldr.toString(),Toast.LENGTH_LONG).show();

                    roomsListView.clearChoices();
                    rooms.clear();

                    roomsListView.setVisibility(View.INVISIBLE);
                    roomsListView.clearChoices();

                    bookBtn.setAlpha(0.1f);
                    bookBtn.setClickable(false);

                    searchBtn.setAlpha(1);
                    searchBtn.setClickable(true);

                    textNumberSelected.setText(getString(R.string.nan));
                    textDaysSelected.setText(String.valueOf(Util.daysBetween(from,to)));
                    textTotalPriceSelected.setText(getString(R.string.nan));

                } else {
                    Toast.makeText(ReserveActivity.this, R.string.reservingError,Toast.LENGTH_LONG).show();
                }
            }
        });



        bookBtn.setOnClickListener(new View.OnClickListener() {             //Go To booking
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ReserveActivity.this, GuestReserveActivity.class);
                reservationData = new Bundle();
                reservationData.putInt("roomNum", selectedRoom.getNumber());
                reservationData.putDouble("dPrice", selectedRoom.getdPrice());
                reservationData.putString("from", from.toString());
                reservationData.putString("to", to.toString());

                intent.putExtras(reservationData);

                guestReserveActivityResultLauncher.launch(intent);

            }
        });


    }

    public static void calculateTotalForAllRooms(){
        try {
            for (Room room : rooms) {
                room.setTotal(from, to);
            }
        } catch(Exception e){ }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }


    public void resetRooms(){
        dbHelperRooms.deleteAll();
        dbHelperRooms.addOne(1,2,1, 125.00f);
        dbHelperRooms.addOne(2,3,2, 100.00f);
        dbHelperRooms.addOne(3,2,2, 125.00f);
        dbHelperRooms.addOne(4,2,1, 140.00f);
        dbHelperRooms.addOne(5,3,2, 140.00f);
        dbHelperRooms.addOne(6,2,1, 160.00f);
        dbHelperRooms.addOne(7,2,1, 200.00f);
        dbHelperRooms.addOne(8,2,2, 115.00f);
        dbHelperRooms.addOne(9,2,1, 140.00f);
        dbHelperRooms.addOne(10,4,2, 200.00f);
    }


}