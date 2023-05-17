package edu.acg.ssh.androidproject_02;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Util {

    public static int priceModifier = 100;
    public static DateTimeFormatter dbDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static LocalDate stringToLocalDate(DateTimeFormatter formatter, String date){
        return LocalDate.parse(date,formatter);
    }

    public static int daysBetween(LocalDate from, LocalDate to){

        if(from.isAfter(to)){ return -1; }
        Period period = Period.between(from, to);
        return period.getDays();
    }


    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidMobile(String phone) {
        String ph = phone.replaceAll("\\s+","");
        return  !TextUtils.isEmpty(ph) &&
                    android.util.Patterns.PHONE.matcher(phone).matches() &&
                        ph.length() > 5 && ph.length() < 15;
    }

    public static String convert(String str)
    {

        char ch[] = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {

            if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') {
                if (ch[i] >= 'a' && ch[i] <= 'z') {
                    ch[i] = (char)(ch[i] - 'a' + 'A');
                }
            }

            else if (ch[i] >= 'A' && ch[i] <= 'Z') {
                ch[i] = (char) (ch[i] + 'a' - 'A');
            }
        }

        String st = new String(ch);
        return st;
    }


    public static boolean doIntervalsIntersect(LocalDate from_A, LocalDate to_A, LocalDate from_B, LocalDate to_B){
        if((from_A.isBefore(to_B)) && (to_A.isAfter(from_B))){
            return true;
        }
        return false;
    }

    public static ArrayList<Reservation> getReservationsOfRoom(ArrayList<Reservation> allReservations, long roomNum){

        ArrayList<Reservation> roomReservations = new ArrayList<>();

        for (Reservation reservation : allReservations) {

            if(reservation.getRoomNum() == roomNum){
                roomReservations.add(reservation);
            }

        }
        return roomReservations;
    }

    public static ArrayList<Reservation> getReservationsOfGuest(ArrayList<Reservation> allReservations, long guestId){

        ArrayList<Reservation> roomReservations = new ArrayList<>();

        for (Reservation reservation : allReservations) {

            if(reservation.getGuestId() == guestId){
                roomReservations.add(reservation);
            }

        }
        return roomReservations;
    }

    public static HashMap<Integer, ArrayList<Reservation>> getReservationsByRooms(ArrayList<Reservation> reservations, ArrayList<Room> rooms){

        HashMap<Integer, ArrayList<Reservation>> reservationsSplitList = new HashMap<>();

        for (Room room : rooms){
            reservationsSplitList.put(room.getNumber(),new ArrayList<>());
        }

        for (Reservation reservation : reservations){
            if(reservationsSplitList.get(reservation.getRoomNum()) == null){
                reservationsSplitList.get(reservation.getRoomNum()).add(reservation);
            } else {
                if(!reservationsSplitList.get(reservation.getRoomNum()).contains(reservation)){
                    reservationsSplitList.get(reservation.getRoomNum()).add(reservation);
                }
            }

        }
        return reservationsSplitList;
    }

    public static ArrayList<Room> getFreeRooms(ArrayList<Room> rooms, ArrayList<Reservation> reservations, LocalDate from, LocalDate to){

        ArrayList<Room> freeRooms = new ArrayList<>();
        HashMap<Integer, ArrayList<Reservation>> reservationsSplit = getReservationsByRooms(reservations,rooms);
        ArrayList<Reservation> roomsReservations;

        for(Room r : rooms){

            roomsReservations = reservationsSplit.get(r.getNumber());

            try {
                if(roomsReservations == null || roomsReservations.isEmpty()){
                    freeRooms.add(r);
                } else {
                    boolean free = true;
                    for (Reservation reservation : roomsReservations) {
                        if (doIntervalsIntersect(from, to, reservation.getFrom(), reservation.getTo())) {
                            free = false;
                            break;
                        }
                    }
                    if(free){
                        freeRooms.add(r);
                    }
                }
            } catch (Exception e){
                return null;
            }

        }
        return freeRooms;

    }

}
