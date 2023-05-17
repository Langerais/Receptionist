package edu.acg.ssh.androidproject_02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;

import static edu.acg.ssh.androidproject_02.DbHelperReservations.RESERVATIONS_TABLE;

public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ConstraintLayout main = findViewById(R.id.mainMenu);
        AnimationDrawable backgroundAnimation = (AnimationDrawable) main.getBackground();
        backgroundAnimation.start();

        ImageButton reserveImgBtn = findViewById(R.id.reserveImgBtn);
        ImageButton calendarImgBtn = findViewById(R.id.reservationsBtn);
        ImageButton guestListImgBtn = findViewById(R.id.guestListImgBtn);

    }

    public void goToReserve(View view){ startActivity(new Intent(this, ReserveActivity.class)); }

    public void goToCalendar(View view){ startActivity(new Intent(this,CalendarActivity.class)); }

    public void goToGuests(View view){ startActivity(new Intent(this, GuestsListActivity.class)); }


}