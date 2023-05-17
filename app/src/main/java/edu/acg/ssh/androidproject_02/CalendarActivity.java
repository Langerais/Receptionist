package edu.acg.ssh.androidproject_02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity {

    private static ArrayList<Reservation> reservations;
    private static Reservation selectedReservation;
    private static LocalDate from;
    private static LocalDate to;

    private static ReservationsAdapter adapter;

    private TextInputLayout textIdReservation;
    private TextInputLayout textRoomNum;

    private TextView textReservationTotalPicked;
    private ListView reservationsListView;

    private ImageButton sendDetailsEmailBtn;
    private ImageButton reservationsGuestBtn;

    private ImageButton reservationsSearchBtn;
    private ImageButton deleteBtn;
    private ImageButton imageButtonFrom;
    private ImageButton imageButtonTo;

    private TextView fromHint;
    private TextView textFromReservationsSearch;
    private TextView textToReservationsSearch;
    private TextView calendarText;

    private static boolean toClicked;

    private static boolean sure;

    private DbHelperReservations dbHelperReservations;
    private static DbHelperGuests dbHelperGuests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ConstraintLayout main = findViewById(R.id.calendarActivityView);
        AnimationDrawable backgroundAnimation = (AnimationDrawable) main.getBackground();
        backgroundAnimation.start();

        toClicked = false;

        dbHelperReservations = new DbHelperReservations(CalendarActivity.this);
        dbHelperGuests = new DbHelperGuests(CalendarActivity.this);

        from = LocalDate.now();
        to = LocalDate.now().plusDays(1);

        fromHint = findViewById(R.id.fromHint);
        TextView toHint = findViewById(R.id.toHint);

        toHint.setTextSize(fromHint.getTextSize());

        textFromReservationsSearch = findViewById(R.id.textFromReservationsSearch);
        textToReservationsSearch = findViewById(R.id.textToReservationsSearch);

        from = LocalDate.now();
        to = LocalDate.now().plusDays(1);

        textFromReservationsSearch.setText(from.format(Util.dbDate));
        textToReservationsSearch.setText(to.format(Util.dbDate));

        textIdReservation = findViewById(R.id.textIdReservation);
        textRoomNum = findViewById(R.id.textRoomNum);

        DatePicker calendarDatePicker = findViewById(R.id.calendarDatePicker);

        reservationsSearchBtn = findViewById(R.id.reservationsSearchBtn);
        ImageButton resetBtn = findViewById(R.id.reservationsResetBtn);

        deleteBtn = findViewById(R.id.reservationDeleteBtn);
        deleteBtn.setAlpha(0.1f);
        deleteBtn.setClickable(false);

        reservationsGuestBtn = findViewById(R.id.reservationsGuestBtn);
        reservationsGuestBtn.setAlpha(0.1f);
        reservationsGuestBtn.setClickable(false);

        sendDetailsEmailBtn = findViewById(R.id.sendDetailsEmailBtn);
        sendDetailsEmailBtn.setAlpha(0.1f);
        sendDetailsEmailBtn.setClickable(false);

        imageButtonFrom = findViewById(R.id.fromReservationSearchBtn);
        imageButtonTo = findViewById(R.id.toReservationSearchBtn);

        calendarText = findViewById(R.id.calendarText);
        calendarText.setTextSize((float) (fromHint.getTextSize() * 0.8));

        textReservationTotalPicked = findViewById(R.id.textReservationTotalPicked);

        reservationsListView = findViewById(R.id.reservationsListView);
        View headerView = getLayoutInflater().inflate(R.layout.reservations_table_header,null);
        reservationsListView.addHeaderView(headerView);

        headerView.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) { }});   //Placeholder


        //  Handling the Guest's reservations request from GuestsListActivity, else loads all reservations, as usual
        Intent intent = getIntent();
        long requestedGuestId = intent.getLongExtra("guestId", -1);
        if(requestedGuestId > -1){
            loadTable(dbHelperReservations.getReservationsByGuestId(requestedGuestId));
        } else {
            loadTable();
        }



        reservationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selectedReservation = (Reservation) adapterView.getItemAtPosition(i);

                textReservationTotalPicked.setText(getString(R.string.total) + " " + String.valueOf(selectedReservation.getTotal()) + "€");

                reservationsGuestBtn.setAlpha(1f);
                reservationsGuestBtn.setClickable(true);

                sendDetailsEmailBtn.setAlpha(1f);
                sendDetailsEmailBtn.setClickable(true);

                deleteBtn.setAlpha(1f);
                deleteBtn.setClickable(true);

            }
        });

        calendarDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {


                if(toClicked){
                    calendarDatePicker.getDayOfMonth();
                    to = LocalDate.of(year, month + 1, day);
                    textToReservationsSearch.setText(to.format(Util.dbDate));
                } else {
                    from = LocalDate.of(year, month + 1, day);
                    textFromReservationsSearch.setText(from.format(Util.dbDate));
                }

                if(!from.isBefore(to)){
                    reservationsSearchBtn.setAlpha(0.1f);
                    reservationsSearchBtn.setClickable(false);
                    textFromReservationsSearch.setTextColor(Color.RED);
                    textToReservationsSearch.setTextColor(Color.RED);
                } else {
                    reservationsSearchBtn.setAlpha(1f);
                    reservationsSearchBtn.setClickable(true);
                    textFromReservationsSearch.setTextColor(fromHint.getTextColors());
                    textToReservationsSearch.setTextColor(fromHint.getTextColors());
                }
            }
        });

        //Opens Date picker for arrival Date
        imageButtonFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open date picker

                if(calendarDatePicker.getVisibility() == View.INVISIBLE || toClicked){
                    calendarDatePicker.setVisibility(View.VISIBLE);
                    reservationsListView.setVisibility(View.INVISIBLE);
                    imageButtonFrom.setAlpha(0.3f);
                    imageButtonTo.setAlpha(1f);
                    calendarText.setText(getString(R.string.fromDate));
                    calendarText.setVisibility(View.VISIBLE);
                } else {
                    calendarDatePicker.setVisibility(View.INVISIBLE);
                    reservationsListView.setVisibility(View.VISIBLE);
                    calendarText.setVisibility(View.INVISIBLE);
                    imageButtonFrom.setAlpha(1f);
                }

                toClicked = false;

            }
        });
        //Opens Date picker for departure Date
        imageButtonTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(calendarDatePicker.getVisibility() == View.INVISIBLE || !toClicked){
                    calendarDatePicker.setVisibility(View.VISIBLE);
                    reservationsListView.setVisibility(View.INVISIBLE);
                    imageButtonTo.setAlpha(0.3f);
                    imageButtonFrom.setAlpha(1f);
                    calendarText.setText(getString(R.string.toDate));
                    calendarText.setVisibility(View.VISIBLE);
                } else {
                    calendarDatePicker.setVisibility(View.INVISIBLE);
                    calendarText.setVisibility(View.INVISIBLE);
                    reservationsListView.setVisibility(View.VISIBLE);
                    imageButtonTo.setAlpha(1f);
                }

                toClicked = true;
            }
        });

        //Opens performs search
        reservationsSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                keyboardClose();
                reservationsListView.clearChoices();

                deleteBtn.setClickable(false);
                deleteBtn.setAlpha(0.1f);

                String fromS = textFromReservationsSearch.getText().toString();
                String toS = textToReservationsSearch.getText().toString();

                String idS = textIdReservation.getEditText().getText().toString();
                String roomNumS = textRoomNum.getEditText().getText().toString();

                ArrayList<Reservation> searchList;

                searchList = dbHelperReservations.getAllReservations();

                if (searchList != null) {

                    if (!idS.isEmpty()) {  searchList = dbHelperReservations.getReservationsById(Long.parseLong(idS)); }
                    else if (!roomNumS.isEmpty()) { searchList = dbHelperReservations.getReservationsOfRoom(Long.parseLong(roomNumS)); }

                    if ((!fromS.isEmpty() || !toS.isEmpty()) && idS.isEmpty()) {

                        LocalDate from = LocalDate.now().minusYears(1000);
                        LocalDate to = LocalDate.now().plusYears(1000);

                        if(!fromS.isEmpty()){ from = LocalDate.parse(fromS, Util.dbDate); }
                        if(!toS.isEmpty())  { to = LocalDate.parse(toS, Util.dbDate); }

                        ArrayList<Reservation> toRemove = new ArrayList<>();

                        for (Reservation reservation : searchList) {
                            if(reservation.getFrom().isAfter(to) || reservation.getTo().isBefore(from)) {
                                toRemove.add(reservation);
                            }
                        }
                        searchList.removeAll(toRemove);
                    }

                    loadTable(searchList);
                }
                else {
                    loadTable();    //loads all reservations
                }

            }
        });

        //Clears search criteria and loads all existing reservations
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboardClose();

                textReservationTotalPicked.setText("");

                reservationsGuestBtn.setAlpha(0.1f);
                reservationsGuestBtn.setClickable(false);

                sendDetailsEmailBtn.setAlpha(0.1f);
                sendDetailsEmailBtn.setClickable(false);

                reservationsSearchBtn.setAlpha(1f);
                reservationsSearchBtn.setClickable(true);

                textFromReservationsSearch.setTextColor(fromHint.getTextColors());
                textToReservationsSearch.setTextColor(fromHint.getTextColors());

                reservations.clear();
                try {
                    reservations = dbHelperReservations.getAllReservations();
                }  catch(NullPointerException e){
                    reservations = new ArrayList<>();
                }

                adapter = new ReservationsAdapter(CalendarActivity.this, reservations);
                reservationsListView.setAdapter(adapter);
            }
        });

        //opens reservation deletion conformation popup
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sure = false;
                keyboardClose();
                showPopup();
            }
        });

        //Shows the guest of selected reservation in GuestsListActivity; If guest was deleted - shows empty list and displays Toast about missing guest
        reservationsGuestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CalendarActivity.this, GuestsListActivity.class);

                long guestId = selectedReservation.getGuest().getId();

                intent.putExtra("return", 2);
                intent.putExtra("guestId", guestId);
                startActivity(intent);
            }
        });

        //Opens default email app to send all the details of the reservation. Also puts guest email to recipient field if email is provided in guests" database
        sendDetailsEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo send email
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                Guest guest = dbHelperGuests.searchById(selectedReservation.getGuestId());

                String email;
                String phone;

                if(guest == null){
                    email = "";
                    phone = "";
                } else {
                    email = guest.getEmail();
                    phone = guest.getPhone();
                }

                intent.putExtra( Intent.EXTRA_EMAIL, new String[]{ email } );
                String message = getString(R.string.reservationSendEmail,
                        String.valueOf(selectedReservation.getId()),
                        selectedReservation.getFromS(),
                        selectedReservation.getToS(),
                        selectedReservation.getGuestName(),
                        email,
                        phone,
                        String.valueOf(selectedReservation.getTotal()));
                //Reservation ID: %s \nFrom: %s \nTo: %s\nGuest: %s\nEmail: %s\nPhone: %s\nTotal price: %s

                intent.putExtra(Intent.EXTRA_SUBJECT, ("Reservation " + selectedReservation.getId()));
                intent.putExtra(Intent.EXTRA_TEXT, message);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }


    //Popup window for deleting the reservation
    private PopupWindow POPUP_WINDOW_GUEST_DELETE = null;

    private void showPopup(){

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.objdelete_popup, null);

        POPUP_WINDOW_GUEST_DELETE = new PopupWindow(this);
        POPUP_WINDOW_GUEST_DELETE.setContentView(view);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        POPUP_WINDOW_GUEST_DELETE.setWidth(displayMetrics.widthPixels);
        POPUP_WINDOW_GUEST_DELETE.setHeight(displayMetrics.heightPixels);

        POPUP_WINDOW_GUEST_DELETE.setFocusable(true);
        POPUP_WINDOW_GUEST_DELETE.setBackgroundDrawable(null);

        POPUP_WINDOW_GUEST_DELETE.showAtLocation(view, Gravity.CENTER,1,1);

        TextView guestDeletePopupText = (TextView) view.findViewById(R.id.guestDeletePopupText);

        String message = getString(R.string.objDeletePopup,
                getString(R.string.reservation),
                String.valueOf(selectedReservation.getId())
        );
        guestDeletePopupText.setText(message);

        ImageButton popupGuestOkBtn = (ImageButton) view.findViewById(R.id.popupGuestOkBtn);
        ImageButton popupGuestCancelBtn = (ImageButton) view.findViewById(R.id.popupGuestCancelBtn);

        popupGuestOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sure){
                    DbHelperReservations dbHelperReservations = new DbHelperReservations(CalendarActivity.this);

                    if(!dbHelperReservations.deleteOne(selectedReservation.getId())){
                        String deleted = getString(R.string.reservation) + " " + selectedReservation.getId() + " " + getString(R.string.deleted);
                        Toast.makeText(CalendarActivity.this, deleted, Toast.LENGTH_SHORT).show();
                        selectedReservation = null;
                    } else {
                        Toast.makeText(CalendarActivity.this, getString(R.string.deletedErr), Toast.LENGTH_LONG).show();
                    }
                    loadTable();

                    deleteBtn.setAlpha(0.1f);
                    deleteBtn.setClickable(false);

                    reservationsGuestBtn.setAlpha(0.1f);
                    reservationsGuestBtn.setClickable(false);

                    sendDetailsEmailBtn.setAlpha(0.1f);
                    sendDetailsEmailBtn.setClickable(false);

                    textReservationTotalPicked.setText("");

                    reservationsListView.clearChoices();
                    POPUP_WINDOW_GUEST_DELETE.dismiss();
                } else {
                    String message = getString(R.string.objDeletePopupSure,
                            getString(R.string.reservation),
                            String.valueOf(selectedReservation.getId())
                    );
                    guestDeletePopupText.setText(message);
                    sure = true;
                }
            }
        });

        popupGuestCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                POPUP_WINDOW_GUEST_DELETE.dismiss();
            }
        });
    }




    private void loadTable(ArrayList<Reservation> content){
        reservations = content;
        adapter = new ReservationsAdapter(CalendarActivity.this, reservations);
        reservationsListView.clearChoices();
        selectedReservation = null;
        reservationsListView.setAdapter(adapter);
    }
    private void loadTable() {      //All Reservations
        dbHelperReservations = new DbHelperReservations(CalendarActivity.this);
        try {
            loadTable(dbHelperReservations.getAllReservations());
        } catch (NullPointerException e){
            loadTable(new ArrayList<Reservation>());
        }
    }

    void keyboardClose(){
        try {
            InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e){ }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}