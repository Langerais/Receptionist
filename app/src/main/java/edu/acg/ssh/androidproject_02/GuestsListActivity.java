package edu.acg.ssh.androidproject_02;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class GuestsListActivity extends AppCompatActivity {

    private static Guest selectedGuest;
    private static ArrayList<Guest> guests;
    private static GuestsAdapter adapter;

    private static TextInputLayout textNameSearch;
    private static TextInputLayout textSurnameSearch;
    private static TextInputLayout textEmailSearch;
    private static TextInputLayout textPhoneSearch;
    private static TextInputLayout textIdGuest;
    private static TextView textGuestEmailPicked;
    private static TextView textGuestPhonePicked;
    private static ListView guestsListView;

    private static boolean sure;

    private static ImageButton guestsReservationsBtn;
    private static ImageButton guestSearchBtn;
    private static ImageButton resetBtn;
    private static ImageButton deleteBtn;
    private static ImageButton sendEmailBtn;
    private static ImageButton phoneCallBtn;
    private static boolean returnGuest;

    private static long guestIdToReturn;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guests_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ConstraintLayout main = findViewById(R.id.guestListLayout);
        AnimationDrawable backgroundAnimation = (AnimationDrawable) main.getBackground();
        backgroundAnimation.start();

        Intent intent = getIntent();

        guestIdToReturn = -1;
        returnGuest = false;

        sendEmailBtn = findViewById(R.id.sendEmailBtn);
        phoneCallBtn = findViewById(R.id.phoneCallBtn);

        sendEmailBtn.setVisibility(View.INVISIBLE);
        phoneCallBtn.setVisibility(View.INVISIBLE);

        textNameSearch = findViewById(R.id.textNameSearch);
        textSurnameSearch = findViewById(R.id.textSurnameSearch);
        textEmailSearch = findViewById(R.id.textGuestsEmailSearch);
        textPhoneSearch = findViewById(R.id.textGuestsPhoneSearch);
        textIdGuest = findViewById(R.id.textIdGuest);

        textGuestEmailPicked = findViewById(R.id.textGuestEmailPicked);
        textGuestPhonePicked = findViewById(R.id.textGuestPhonePicked);

        guestSearchBtn = findViewById(R.id.guestsSearchBtn);
        resetBtn = findViewById(R.id.guestsResetBtn);
        deleteBtn = findViewById(R.id.guestDeleteBtn);

        deleteBtn.setClickable(false);
        deleteBtn.setAlpha(0.2f);

        guestsReservationsBtn = findViewById(R.id.guestsReservationsBtn);
        guestsReservationsBtn.setClickable(false);
        guestsReservationsBtn.setAlpha(0.1f);

        if(intent.getIntExtra("return", -1) == 1){
            returnGuest = true;
            guestsReservationsBtn.setImageDrawable(getDrawable(R.mipmap.reservesign));
        } else if (intent.getIntExtra("return", -1) == 2) {
            guestsReservationsBtn.setImageDrawable(getDrawable(R.mipmap.reservations_list));
            guestIdToReturn = intent.getLongExtra("guestId", -1);
        }

        guestsListView = findViewById(R.id.reservationsListView);
        View headerView = getLayoutInflater().inflate(R.layout.guests_table_header,null);
        guestsListView.addHeaderView(headerView);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {}
        });

        DbHelperGuests dbHelperGuests = new DbHelperGuests(GuestsListActivity.this);

        guests = new ArrayList<>();

        //if activity was opened from calendar view, searches for the guest from picked reservation
        if(guestIdToReturn != -1){
            guests.add(dbHelperGuests.searchById(guestIdToReturn));

            if(guests.isEmpty()){
                Toast.makeText(GuestsListActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
            } else if (guests.size() == 1){

                selectedGuest = guests.get(0);

                textNameSearch.setAlpha(0.6f);
                textNameSearch.setEnabled(false);

                textSurnameSearch.setAlpha(0.6f);
                textSurnameSearch.setEnabled(false);

                textEmailSearch.setAlpha(0.6f);
                textEmailSearch.setEnabled(false);

                textPhoneSearch.setAlpha(0.6f);
                textPhoneSearch.setEnabled(false);

                textIdGuest.setAlpha(0.6f);
                textIdGuest.setEnabled(false);

                try {

                    textNameSearch.getEditText().setText(selectedGuest.getName());

                    textSurnameSearch.getEditText().setText(selectedGuest.getSurname());

                    textEmailSearch.getEditText().setText(selectedGuest.getEmail());

                    textPhoneSearch.getEditText().setText(selectedGuest.getPhone());

                    textIdGuest.getEditText().setText(String.valueOf(guestIdToReturn));

                } catch (NullPointerException e){   //if guest can't be found (probably deleted) then displays toast message and shows empty listView
                    Toast.makeText(GuestsListActivity.this,
                            getString(R.string.guest) +
                                    " " + String.valueOf(guestIdToReturn) +
                                    " " + getString(R.string.notFound),
                            Toast.LENGTH_LONG).show();
                    guests.clear();
                }
                guestSearchBtn.setAlpha(0.2f);
                guestSearchBtn.setEnabled(false);

                resetBtn.setAlpha(0.2f);
                resetBtn.setEnabled(false);

            }

        } else {    //if activity was opened from MainActivity - loads all guests
            guests = dbHelperGuests.getAllGuests();
        }

        adapter = new GuestsAdapter(this, guests);
        guestsListView.setAdapter(adapter);

        guestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGuest = (Guest) adapterView.getItemAtPosition(i);
                guestsReservationsBtn.setClickable(true);
                guestsReservationsBtn.setAlpha(1f);

                deleteBtn.setClickable(true);
                deleteBtn.setAlpha(1f);

                String email;
                if(selectedGuest.getEmail().length() < 3){
                    email = getString(R.string.noEmail);
                    sendEmailBtn.setVisibility(View.INVISIBLE);
                }
                else {
                    email = selectedGuest.getEmail();
                    sendEmailBtn.setVisibility(View.VISIBLE);
                }
                textGuestEmailPicked.setText(email);

                String phone;
                if(selectedGuest.getPhone().length() < 3){
                    phone = getString(R.string.noPhone);
                    phoneCallBtn.setVisibility(View.INVISIBLE);
                }
                else {
                    phone = selectedGuest.getPhone();
                    phoneCallBtn.setVisibility(View.VISIBLE);
                }
                textGuestPhonePicked.setText(phone);
            }
        });

        guestSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                keyboardClose();
                guestsListView.clearChoices();

                deleteBtn.setClickable(false);
                deleteBtn.setAlpha(0.1f);

                sendEmailBtn.setVisibility(View.INVISIBLE);
                phoneCallBtn.setVisibility(View.INVISIBLE);

                String idS = textIdGuest.getEditText().getText().toString();
                String name = textNameSearch.getEditText().getText().toString();
                String surname = textSurnameSearch.getEditText().getText().toString();
                String email = textEmailSearch.getEditText().getText().toString();
                String phone = textPhoneSearch.getEditText().getText().toString();
                long id;

                ArrayList<Guest> guestsResult = dbHelperGuests.getAllGuests();

                guests.clear();

                for (Guest guest : guestsResult) {

                    if(inputsNum() == 0){
                        guests = guestsResult;
                        break;
                    }

                    if((!idS.isEmpty() && guest.getId() == Long.parseLong(idS))){ guests.add(guest); }

                    else if(!name.isEmpty() && (guest.getName().contains(name) || guest.getName().contains(Util.convert(name)))){ guests.add(guest); }

                    else if(!surname.isEmpty() && ( guest.getSurname().contains(surname) || guest.getSurname().contains(Util.convert(surname)))){ guests.add(guest); }

                    else if(!email.isEmpty() && guest.getEmail().equalsIgnoreCase(email)){ guests.add(guest); }

                    else if(!phone.isEmpty() && guest.getPhone().equals(phone)){ guests.add(guest); }

                }

                adapter = new GuestsAdapter(GuestsListActivity.this, guests);
                guestsListView.setAdapter(adapter);

                guestsReservationsBtn.setAlpha(0.1f);
                guestsReservationsBtn.setClickable(false);
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboardClose();
                guestsReservationsBtn.setAlpha(0.1f);
                guestsReservationsBtn.setClickable(false);
                guests.clear();
                guests = dbHelperGuests.getAllGuests();
                adapter = new GuestsAdapter(GuestsListActivity.this, guests);
                guestsListView.setAdapter(adapter);

                selectedGuest = null;

                guestsListView.clearChoices();

                sendEmailBtn.setVisibility(View.INVISIBLE);
                phoneCallBtn.setVisibility(View.INVISIBLE);

                textGuestEmailPicked.setText("");
                textGuestPhonePicked.setText("");

                textNameSearch.getEditText().setText("");
                textSurnameSearch.getEditText().setText("");
                textEmailSearch.getEditText().setText("");
                textPhoneSearch.getEditText().setText("");
                textIdGuest.getEditText().setText("");
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sure = false;
                keyboardClose();
                showPopup();
            }
        });

        guestsReservationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                if(returnGuest){
                    Bundle guestData = new Bundle();
                    guestData.putLong("id", selectedGuest.getId());
                    guestData.putString("name", selectedGuest.getName());
                    guestData.putString("surname", selectedGuest.getSurname());
                    guestData.putString("email", selectedGuest.getEmail());
                    guestData.putString("phone", selectedGuest.getPhone());
                    intent.putExtras(guestData);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    intent = new Intent(GuestsListActivity.this, CalendarActivity.class);
                    intent.putExtra("guestId",selectedGuest.getId());
                    startActivity(intent);
                }
            }
        });

        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.putExtra( Intent.EXTRA_EMAIL, new String[]{ textGuestEmailPicked.getText().toString() } );
                intent.setData(Uri.parse("mailto:" + textGuestEmailPicked.getText().toString()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        phoneCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData( Uri.parse("tel:" + textGuestPhonePicked.getText().toString()) );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

    }

    static int inputsNum(){

        int inputs = 0;

        if(!textIdGuest.getEditText().getText().toString().isEmpty()){ inputs++; }
        if(!textNameSearch.getEditText().getText().toString().isEmpty()){ inputs++; }
        if(!textSurnameSearch.getEditText().getText().toString().isEmpty()){ inputs++; }
        if(!textEmailSearch.getEditText().getText().toString().isEmpty()){ inputs++; }
        if(!textPhoneSearch.getEditText().getText().toString().isEmpty()){ inputs++; }
        if(!textIdGuest.getEditText().getText().toString().isEmpty()){ inputs++; }

        return inputs;
    }

    void keyboardClose(){
        try {
            InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e){

        }
    }

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
                getString(R.string.guest),
                (selectedGuest.getName() + " " + selectedGuest.getSurname())
        );
        guestDeletePopupText.setText(message);

        ImageButton popupGuestOkBtn = (ImageButton) view.findViewById(R.id.popupGuestOkBtn);
        ImageButton popupGuestCancelBtn = (ImageButton) view.findViewById(R.id.popupGuestCancelBtn);

        popupGuestOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sure){
                    DbHelperGuests dbHelperGuests = new DbHelperGuests(GuestsListActivity.this);

                    if(!dbHelperGuests.deleteOne(selectedGuest.getId())){
                        String deleted = getString(R.string.guest) + " " + selectedGuest.getName() + " " + selectedGuest.getSurname() + " " + getString(R.string.objDeleted);
                        Toast.makeText(GuestsListActivity.this, deleted, Toast.LENGTH_LONG).show();
                        selectedGuest = null;

                        textGuestEmailPicked.setText("");
                        textGuestPhonePicked.setText("");

                        sendEmailBtn.setVisibility(View.INVISIBLE);
                        phoneCallBtn.setVisibility(View.INVISIBLE);
                    } else {
                        Toast.makeText(GuestsListActivity.this, getString(R.string.deletedErr), Toast.LENGTH_LONG).show();
                    }
                    loadTable();

                    deleteBtn.setAlpha(0.1f);
                    deleteBtn.setClickable(false);

                    guestsReservationsBtn.setAlpha(0.1f);
                    guestsReservationsBtn.setClickable(false);

                    guestsListView.clearChoices();
                    POPUP_WINDOW_GUEST_DELETE.dismiss();
                } else {
                    String message = getString(R.string.objDeletePopupSure,
                            getString(R.string.guest),
                            (selectedGuest.getName() + " " + selectedGuest.getSurname())
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

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void loadTable(ArrayList<Guest> content){
        guests = content;
        adapter = new GuestsAdapter(GuestsListActivity.this, guests);
        guestsListView.clearChoices();
        selectedGuest = null;
        guestsListView.setAdapter(adapter);
    }
    private void loadTable() {
        DbHelperGuests dbHelperGuests = new DbHelperGuests(GuestsListActivity.this);
        loadTable(dbHelperGuests.getAllGuests());
    }

}