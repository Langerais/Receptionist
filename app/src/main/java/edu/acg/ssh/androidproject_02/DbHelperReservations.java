package edu.acg.ssh.androidproject_02;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.DateInterval;

import androidx.annotation.Nullable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DbHelperReservations extends SQLiteOpenHelper {

    public static final String RESERVATIONS_TABLE = "RESERVATIONS_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_ROOM_NUM = "ROOM_NUM";
    public static final String COLUMN_GUEST = "GUEST";
    public static final String COLUMN_GUEST_ID = "GUEST_ID";
    public static final String COLUMN_FROM = "DATE_FROM";
    public static final String COLUMN_TO = "DATE_TO";
    public static final String COLUMN_TOTAL = "TOTAL";
    private static Context contextSide;


    public DbHelperReservations(@Nullable Context context) {
        super(context, "reservations.db", null, 1);
        contextSide = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + RESERVATIONS_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " + COLUMN_ROOM_NUM + " INTEGER, " + COLUMN_GUEST + " STRING, "  + COLUMN_GUEST_ID + " LONG, " + COLUMN_FROM + " STRING, " + COLUMN_TO + " STRING, " + COLUMN_TOTAL + " FLOAT)";

        db.execSQL(createTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(Reservation reservation){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ROOM_NUM, reservation.getRoomNum());
        cv.put(COLUMN_GUEST, reservation.getGuestName());
        cv.put(COLUMN_GUEST_ID, reservation.getGuest().getId());
        cv.put(COLUMN_FROM, reservation.getFromS());
        cv.put(COLUMN_TO, reservation.getToS());
        cv.put(COLUMN_TOTAL, reservation.getTotal());

        long insert = db.insert(RESERVATIONS_TABLE, null, cv);

        db.close();

        return (insert != -1);

    }
    public boolean addOne(int roomNum, String guestNameSurname, long guestId, String from, String to, float total){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ROOM_NUM, roomNum);
        cv.put(COLUMN_GUEST, guestNameSurname);
        cv.put(COLUMN_GUEST_ID, guestId);
        cv.put(COLUMN_FROM, from);
        cv.put(COLUMN_TO, to);
        cv.put(COLUMN_TOTAL, total);


        long insert = db.insert(RESERVATIONS_TABLE, null, cv);

        db.close();

        return (insert != -1);

    }

    public boolean deleteOne(long id){

        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + RESERVATIONS_TABLE + " WHERE " + COLUMN_ID + " = " + id;
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
        String queryString = "DELETE FROM " + RESERVATIONS_TABLE;
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }


    }

    public ArrayList<Reservation> getAllReservations(){

        ArrayList<Reservation> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + RESERVATIONS_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        Reservation reservation;

        if(cursor.moveToFirst()){
            do{
                long id = cursor.getLong(0);
                int roomNum = cursor.getInt(1);
                String guestName = cursor.getString(2).split(" ")[0];
                String guestSurname = cursor.getString(2).split(" ")[1];
                long guestId = cursor.getLong(3);
                LocalDate from = Util.stringToLocalDate(Util.dbDate,cursor.getString(4));
                LocalDate to = Util.stringToLocalDate(Util.dbDate,cursor.getString(5));
                float total = cursor.getFloat(6);

                Guest guest = new Guest(guestId,guestName,guestSurname,"-","-");
                reservation = new Reservation(id,from,to,roomNum,guest, total);
                returnList.add(reservation);
            } while(cursor.moveToNext());

        } else {
            //Rooms Db is empty
        }

        cursor.close();
        db.close();

        return returnList;

    }

  //  COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " + COLUMN_ROOM_NUM + " INTEGER, " + COLUMN_GUEST + " STRING, "
    //  + COLUMN_GUEST_ID + " LONG, " + COLUMN_FROM + " STRING, " + COLUMN_TO + " STRING, " + COLUMN_TOTAL + " FLOAT)

    public long getLastAddedId(){

        //String queryString = "SELECT * FROM " + RESERVATIONS_TABLE + " WHERE ID = (SELECT MAX(ID) FROM " + RESERVATIONS_TABLE + ")";
        String queryString = "SELECT * FROM " + RESERVATIONS_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToLast()){
            long lastReservationId = cursor.getLong(0);
            db.close();
            return lastReservationId;
        } else {
            db.close();
            return 0;
        }

    }

    public boolean isRoomFree(LocalDate from, LocalDate to, long roomNum){

        for (Reservation r : getReservationsOfRoom(roomNum)) {
            if(Util.doIntervalsIntersect(from,to,r.getFrom(),r.getTo())){
                return false;
            }
        }
        return true;

    }


    public ArrayList<Reservation> getReservationsOfRoom(long roomNum){

        String queryString = "SELECT * FROM " + RESERVATIONS_TABLE + " WHERE " + COLUMN_ROOM_NUM + " LIKE ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, new String[]{String.valueOf(roomNum)});

        ArrayList<Reservation> reservations = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                reservations.add(getReservation(cursor));
            } while(cursor.moveToNext());
        }
        db.close();
        return reservations;
    }

    public ArrayList<Reservation> getReservationsById(long id){

        String queryString = "SELECT * FROM " + RESERVATIONS_TABLE + " WHERE " + COLUMN_ID + " LIKE ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, new String[]{String.valueOf(id)});

        ArrayList<Reservation> reservations = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                reservations.add(getReservation(cursor));
            } while(cursor.moveToNext());
        }

        if(reservations.isEmpty()){
            db.close();
            return new ArrayList<Reservation>();
        } else {
            db.close();
            return reservations;
        }

    }

    public ArrayList<Reservation> getReservationsByGuestId(long guestId){

        String queryString = "SELECT * FROM " + RESERVATIONS_TABLE + " WHERE " + COLUMN_GUEST_ID + " LIKE ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, new String[]{String.valueOf(guestId)});

        ArrayList<Reservation> reservations = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                reservations.add(getReservation(cursor));
            } while(cursor.moveToNext());
        }

        if(reservations.isEmpty()){
            db.close();
            return new ArrayList<Reservation>();
        } else {
            db.close();
            return reservations;
        }

    }

    static Reservation getReservation(Cursor cursor){
        long id = cursor.getLong(0);
        int roomNum = cursor.getInt(1);
        String nameSurname = cursor.getString(2);
        String name = nameSurname.split(" ")[0];
        String surname = nameSurname.split(" ")[1];
        long guestId = cursor.getLong(3);
        LocalDate from = Util.stringToLocalDate(Util.dbDate, cursor.getString(4));
        LocalDate to = Util.stringToLocalDate(Util.dbDate, cursor.getString(5));
        double total = cursor.getFloat(6);

        DbHelperGuests dbHelperGuests = new DbHelperGuests(contextSide);

        Guest guest = dbHelperGuests.searchById(guestId);
        return new Reservation(id,from,to,roomNum,guest,total);
    }

    static Reservation getReservation(Cursor cursor, Context context){
        long id = cursor.getLong(0);
        int roomNum = cursor.getInt(1);
        String nameSurname = cursor.getString(2);
        String name = nameSurname.split(" ")[0];
        String surname = nameSurname.split(" ")[1];
        long guestId = cursor.getLong(3);
        LocalDate from = Util.stringToLocalDate(Util.dbDate, cursor.getString(4));
        LocalDate to = Util.stringToLocalDate(Util.dbDate, cursor.getString(5));
        double total = cursor.getFloat(6);

        DbHelperGuests dbHelperGuests = new DbHelperGuests(context);

        Guest guest = dbHelperGuests.searchById(guestId);
        return new Reservation(id,from,to,roomNum,guest,total);
    }






}
