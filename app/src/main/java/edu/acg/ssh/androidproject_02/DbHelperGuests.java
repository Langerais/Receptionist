package edu.acg.ssh.androidproject_02;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DbHelperGuests extends SQLiteOpenHelper {

    public static final String GUESTS_TABLE = "GUESTS_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_SURNAME = "SURNAME";
    public static final String COLUMN_EMAIL = "EMAIL";
    public static final String COLUMN_PHONE = "PHONE";

    public DbHelperGuests(@Nullable Context context) {
        super(context, "guests.db", null, 1);
    }

    @SuppressLint("SQLiteString")
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + GUESTS_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " + COLUMN_NAME + " STRING, " + COLUMN_SURNAME + " STRING, " + COLUMN_EMAIL + " STRING, " + COLUMN_PHONE + " STRING)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(Guest guest){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, guest.getName());
        cv.put(COLUMN_SURNAME, guest.getSurname());
        cv.put(COLUMN_EMAIL, guest.getEmail());
        cv.put(COLUMN_PHONE, guest.getPhone());

        long insert = db.insert(GUESTS_TABLE, null, cv);

        db.close();

        return (insert != -1);

    }

    public boolean addOne(String name, String surname, String email, String phone){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_SURNAME, surname);
        cv.put(COLUMN_EMAIL, email);
        cv.put(COLUMN_PHONE, phone);

        long insert = db.insert(GUESTS_TABLE, null, cv);

        db.close();

        return (insert != -1);

    }

    public boolean deleteOne(long id){

        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + GUESTS_TABLE + " WHERE " + COLUMN_ID + " = " + id;
        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()){
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }

    }

    public boolean deleteAll(){

        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + GUESTS_TABLE;
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }

    }

    public ArrayList<Guest> getAllGuests(){

        ArrayList<Guest> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + GUESTS_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        Guest guest;

        if(cursor.moveToFirst()){
            do{
                long id = cursor.getInt(0);
                String name = cursor.getString(1);
                String surname = cursor.getString(2);
                String email = cursor.getString(3);
                String phone = cursor.getString(4);

                guest = new Guest(id,name,surname,email,phone);
                returnList.add(guest);

            } while(cursor.moveToNext());

        } else {
            //Rooms Db is empty
        }

        cursor.close();
        db.close();

        return returnList;

    }

    public long getLastAddedId(){

        String queryString = "SELECT * FROM " + GUESTS_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);


        if(cursor.moveToLast()){
            long lastGuestId = cursor.getLong(0);
            db.close();
            return lastGuestId;
        }
        else {
            db.close();
            return 0;
        }
    }

    public Guest searchById(long id){

        Guest guest;

        String queryString = "SELECT * FROM " + GUESTS_TABLE + " WHERE " + COLUMN_ID + " LIKE ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, new String[]{String.valueOf(id)});

        if(cursor.moveToFirst()){

            guest = getGuest(cursor);
            db.close();
            return guest;

        }
        return null;
    }

    public ArrayList<Guest> searchByColumn(String columnName, String toFind){

        ArrayList<Guest> guests = new ArrayList<>();

        String queryString = "SELECT * FROM " + GUESTS_TABLE + " WHERE " + COLUMN_NAME + " LIKE ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, new String[]{toFind});

        if(cursor.moveToFirst()){
            do{
                guests.add(getGuest(cursor));
            } while(cursor.moveToNext());

        }
        db.close();
        return guests;
    }


    static Guest getGuest(Cursor cursor){
        long id = cursor.getLong(0);
        String name = cursor.getString(1);
        String surname = cursor.getString(2);
        String email = cursor.getString(3);
        String phone = cursor.getString(4);
        return new Guest(id,name,surname,email,phone);
    }

}
