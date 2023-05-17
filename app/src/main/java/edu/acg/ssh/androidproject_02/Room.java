package edu.acg.ssh.androidproject_02;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;


public class Room {

    private int number;
    private int capacity;
    private int beds;
    private double dPrice;
    private double total;

    public Room(int number, int capacity, int beds, double dPrice, double total) {
        this.number = number;
        this.capacity = capacity;
        this.beds = beds;
        this.dPrice = dPrice;
        this.total = total;
    }

    public Room(int number, int capacity, int beds, double dPrice) {
        this.number = number;
        this.capacity = capacity;
        this.beds = beds;
        this.dPrice = dPrice;
        this.total = 0;
    }

    public Room(String[] row) {
        this.number = Integer.parseInt(row[0]);
        this.capacity = Integer.parseInt(row[1]);
        this.beds = Integer.parseInt(row[2]);
        this.dPrice = Double.parseDouble(row[3]);
        this.total = this.dPrice;
    }

    public Room(JSONObject object) throws JSONException {
        this.number = object.getInt("number");
        this.capacity = object.getInt("capacity");
        this.beds = object.getInt("beds");
        this.dPrice = object.getDouble("dPrice");
        this.total = this.dPrice;
    }


    public static ArrayList<Room> fromJson(JSONArray objects) throws JSONException {
        ArrayList<Room> rooms = new ArrayList<>();

        for ( int i = 0; i < objects.length(); i++ ) { rooms.add(new Room(objects.getJSONObject(i))); }

        return rooms;
    }

    @Override
    public String toString() {
        return "Room{" +
                "number=" + number +
                ", capacity=" + capacity +
                ", beds=" + beds +
                ", dPrice=" + dPrice +
                '}';
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public double getdPrice() {
        return dPrice;
    }

    public void setdPrice(double dPrice) {
        this.dPrice = dPrice;
    }

    public void setTotal(int days){
        this.total = this.dPrice * days * ((double) (Util.priceModifier / 100));
    }

    public void setTotal(LocalDate from, LocalDate to){
        int days = Util.daysBetween(from, to);
        this.total = this.dPrice * days;
    }

    public void setTotal(double total){
        this.total = total;
    }

    public double getTotal(){
        return this.total;
    }

    public boolean isRoomFree(LocalDate from, LocalDate to){
        return true;
    }
}
