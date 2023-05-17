package edu.acg.ssh.androidproject_02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.textfield.TextInputEditText;
import java.time.LocalDate;

public class GuestReserveActivity extends AppCompatActivity {


    private static int roomNum;
    private static double dPrice;
    private static LocalDate from;
    private static LocalDate to;
    private static Guest guest;
    private static long guestId;
    private static Reservation reservation;
    private static double total;

    private ToggleButton existingGuestSwitch;

    private TextInputEditText inputTextName;
    private TextInputEditText inputTextSurname;
    private TextInputEditText inputTextEmail;
    private TextInputEditText inputTextPhone;

    private TextView textName;
    private TextView textSurname;
    private TextView textEmail;
    private TextView textPhone;
    private TextView textNightsReserve;
    private ImageButton bookBtnFinal;

    final int REQUEST_CODE_GUEST = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_reserve);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ConstraintLayout main = findViewById(R.id.guestReserve);
        AnimationDrawable backgroundAnimation = (AnimationDrawable) main.getBackground();
        backgroundAnimation.start();

        Intent intent = getIntent();

        String fromS = intent.getStringExtra("from");
        from = LocalDate.parse(fromS);
        TextView textViewFromReserve = findViewById(R.id.textViewFromReserve);
        textViewFromReserve.setText(fromS);

        String toS = intent.getStringExtra("to");
        to = LocalDate.parse(toS);
        TextView textViewToReserve = findViewById(R.id.textViewToReserve);
        textViewToReserve.setText(toS);

        textNightsReserve = findViewById(R.id.textNightsReserve);
        textNightsReserve.setText(String.valueOf(Util.daysBetween(from, to)));

        dPrice = intent.getDoubleExtra("dPrice", -1);
        total = dPrice * Util.daysBetween(from, to);
        TextView textViewPriceReserve = findViewById(R.id.textViewPriceReserve);

        String totalS = String.valueOf(total) + "€";
        textViewPriceReserve.setText(totalS);

        roomNum = intent.getIntExtra("roomNum", -1);
        TextView textViewNumberReserve = findViewById(R.id.textViewNumberReserve);
        textViewNumberReserve.setText(String.valueOf(roomNum));

        textName = findViewById(R.id.textName);
        textSurname = findViewById(R.id.textSurname);
        textEmail = findViewById(R.id.textEmail);
        textPhone = findViewById(R.id.textPhone);

        inputTextName = findViewById(R.id.inputTextName);
        inputTextSurname = findViewById(R.id.inputTextSurname);
        inputTextEmail = findViewById(R.id.inputTextEmail);
        inputTextEmail = findViewById(R.id.inputTextEmail);
        inputTextPhone = findViewById(R.id.inputTextPhone);

        bookBtnFinal = findViewById(R.id.bookBtnFinal);
        bookBtnFinal.setAlpha(0.2f);
        bookBtnFinal.setClickable(false);

        existingGuestSwitch = findViewById(R.id.newGuestSwitch);

        ImageButton pickFromListBtn = findViewById(R.id.pickFromListBtn);

        Group newGuestInputGroup = findViewById(R.id.newGuestInputGroup);
        Group existingGuestInputGroup = findViewById(R.id.existingGuestInputGroup);


        existingGuestSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {    //true = existing user

                if (!b) {
                    newGuestInputGroup.setVisibility(View.VISIBLE);
                    existingGuestInputGroup.setVisibility(View.INVISIBLE);
                } else {
                    newGuestInputGroup.setVisibility(View.INVISIBLE);
                    existingGuestInputGroup.setVisibility(View.VISIBLE);
                }
                clearAllText();
                bookBtnCheckChange();
            }
        });

        pickFromListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO open searchForGuestActivity (for result)
                Intent intent = new Intent(GuestReserveActivity.this, GuestsListActivity.class);

                intent.putExtra("return", 1);
                startActivityForResult(intent, REQUEST_CODE_GUEST);   //2 is for returning the guest

                //intent.putExtras(reservationData);

                //guestReserveActivityResultLauncher.launch(intent);

                //startActivityForResult(intent,1);
                //startActivity(intent);
            }
        });


        inputTextName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() == 0) { inputTextName.setError(getString(R.string.mandatoryErr)); }

                else { inputTextName.setError(null); }

                bookBtnCheckChange();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        inputTextSurname.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    inputTextSurname.setError(getString(R.string.mandatoryErr));
                } else {
                    inputTextSurname.setError(null);
                }
                    bookBtnCheckChange();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        inputTextPhone.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {

                String phone = editable.toString();
                if (phone.isEmpty() || Util.isValidMobile(phone)) {
                    inputTextPhone.setTextColor(textViewNumberReserve.getTextColors());
                } else {
                    inputTextPhone.setTextColor(Color.RED);
                }

                bookBtnCheckChange();

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
        });

        inputTextEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {

                String email = editable.toString();
                if (email.isEmpty() || Util.isEmailValid(email)) {
                    inputTextEmail.setTextColor(textViewNumberReserve.getTextColors());
                } else {
                    inputTextEmail.setTextColor(Color.RED);
                }
                bookBtnCheckChange();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
        });


        bookBtnFinal.setOnClickListener(new View.OnClickListener() {    //Final Book Btn
            @Override
            public void onClick(View view) {

                    if (isEmailOk()) { inputTextEmail.setError(getString(R.string.emailErr)); }

                    if (isPhoneOk()) { inputTextPhone.setError(getString(R.string.phoneErr)); }

                    if (isNameOk()) { inputTextName.setError(getString(R.string.mandatoryErr)); }

                    if (isSurnameOk()) { inputTextSurname.setError(getString(R.string.mandatoryErr)); }

                    if (bookBtnCheckChange()) {
                        DbHelperGuests dbHelperGuests = new DbHelperGuests(GuestReserveActivity.this);
                        DbHelperReservations dbHelperReservations = new DbHelperReservations(GuestReserveActivity.this);

                        if (!existingGuestSwitch.isChecked()) {
                            guest = new Guest(
                                    Util.convert(inputTextName.getText().toString()),
                                    Util.convert(inputTextSurname.getText().toString()),
                                    Util.convert(inputTextEmail.getText().toString()),
                                    Util.convert(inputTextPhone.getText().toString()));

                            dbHelperGuests.addOne(guest);
                            guest.setId(dbHelperGuests.getLastAddedId());
                        }

                        reservation = new Reservation(from, to, roomNum, guest, total);
                        Intent reservationConf = new Intent();

                        boolean guestAdded = true;


                        dbHelperReservations.addOne(reservation);
                        if (guestAdded) {
                            Bundle reservationData = new Bundle();
                            reservationData.putLong("reservationId", dbHelperReservations.getLastAddedId());
                            reservationData.putLong("guestId", guest.getId());
                            reservationData.putString("guestName", guest.getName());
                            reservationData.putString("guestSurname", guest.getSurname());
                            reservationConf.putExtras(reservationData);

                            setResult(RESULT_OK, reservationConf);
                        } else {
                            setResult(RESULT_CANCELED, reservationConf);
                        }

                        finish();

                    } else {
                            Toast.makeText(GuestReserveActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

                    Bundle guestData = data.getExtras();
                    guestId = guestData.getLong("id", -1);

                    if(guestId != -1) {
                        guest = new Guest(guestId,
                                guestData.getString("name"),
                                guestData.getString("surname"),
                                guestData.getString("email"),
                                guestData.getString("phone")
                        );

                        textName.setText(guest.getName());
                        textSurname.setText(guest.getSurname());
                        textEmail.setText(guest.getEmail());
                        textPhone.setText(guest.getPhone());

                        textName.setVisibility(View.VISIBLE);
                        textSurname.setVisibility(View.VISIBLE);
                        textEmail.setVisibility(View.VISIBLE);
                        textPhone.setVisibility(View.VISIBLE);

                        bookBtnCheckChange();

                    }

        } else {
            Toast.makeText(GuestReserveActivity.this, R.string.existingGuestError, Toast.LENGTH_LONG).show();
            clearAllText();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void clearAllText(){

        inputTextName.setText("");
        inputTextSurname.setText("");
        inputTextEmail.setText("");
        inputTextPhone.setText("");

        textName.setText(getString(R.string.nameSt));
        textSurname.setText(getString(R.string.surnameSt));
        textEmail.setText(getString(R.string.e_mail));
        textPhone.setText(getString(R.string.phone));

        bookBtnCheckChange();
    }


    private boolean isNameOk(){ return inputTextName.getText().toString().length() > 0; }
    private boolean isSurnameOk(){ return inputTextSurname.getText().toString().length() > 0; }
    private boolean isEmailOk(){
        return (Util.isEmailValid(inputTextEmail.getText().toString())
                || inputTextEmail.getText().toString().equals("")) ;
    }
    private boolean isPhoneOk(){
        return (Util.isValidMobile(inputTextPhone.getText().toString())
            || inputTextPhone.getText().toString().equals("")) ;  }

    private boolean bookBtnCheckChange(){
        if(((isNameOk() && isSurnameOk() && isPhoneOk() && isEmailOk())) || (existingGuestSwitch.isChecked() && !textEmail.getText().toString().equals("E-mail"))){
            bookBtnFinal.setClickable(true);
            bookBtnFinal.setAlpha(1f);
            return true;
        }
        bookBtnFinal.setClickable(false);
        bookBtnFinal.setAlpha(0.2f);
        return false;
    }
}