package edu.acg.ssh.androidproject_02;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DbHelperRooms extends SQLiteOpenHelper {

    public static final String ROOMS_TABLE = "ROOMS_TABLE";
    public static final String COLUMN_NUMBER = "NUMBER";
    public static final String COLUMN_CAPACITY = "CAPACITY";
    public static final String COLUMN_BEDS = "BEDS";
    public static final String COLUMN_DPRICE = "DPRICE";

    public DbHelperRooms(@Nullable Context context) {
        super(context, "rooms.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + ROOMS_TABLE + " (" + COLUMN_NUMBER + " INTEGER, " + COLUMN_CAPACITY + " INTEGER, " + COLUMN_BEDS + " INTEGER, " + COLUMN_DPRICE + " FLOAT)";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(Room room){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NUMBER, room.getNumber());
        cv.put(COLUMN_CAPACITY, room.getCapacity());
        cv.put(COLUMN_BEDS, room.getBeds());
        cv.put(COLUMN_DPRICE, room.getdPrice());

        long insert = db.insert(ROOMS_TABLE, null, cv);

        return (insert != -1);

    }
    public boolean addOne(int number, int capacity, int beds, float dPrice){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NUMBER, number);
        cv.put(COLUMN_CAPACITY, capacity);
        cv.put(COLUMN_BEDS, beds);
        cv.put(COLUMN_DPRICE, dPrice);


        long insert = db.insert(ROOMS_TABLE, null, cv);

        db.close();

        return (insert != -1);

    }

    public boolean deleteOne(int roomNum){

        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + ROOMS_TABLE + " WHERE " + COLUMN_NUMBER + " = " + roomNum;
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
        String queryString = "DELETE FROM " + ROOMS_TABLE;
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }


    }

    public ArrayList<Room> getAllRooms(){

        ArrayList<Room> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + ROOMS_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        Room room;

        if(cursor.moveToFirst()){
            do{
                int roomNum = cursor.getInt(0);
                int capacity = cursor.getInt(1);
                int beds = cursor.getInt(2);
                float dPrice = cursor.getFloat(3);

                room = new Room(roomNum,capacity,beds,dPrice);
                returnList.add(room);

            } while(cursor.moveToNext());

        } else {
            //Rooms Db is empty
        }

        cursor.close();
        db.close();

        return returnList;

    }



}
