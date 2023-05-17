package edu.acg.ssh.androidproject_02;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Reservation {

    private long id;
    private LocalDate from;
    private LocalDate to;
    private int roomNum;
    private Guest guest;
    private double total;

    public Reservation(long id, LocalDate from, LocalDate to, int roomNum, Guest guest, double total) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.roomNum = roomNum;
        this.guest = guest;
        this.total = total;
    }

    public Reservation(LocalDate from, LocalDate to, int roomNum, Guest guest, double total) {
        this.id = -1;
        this.from = from;
        this.to = to;
        this.roomNum = roomNum;
        this.guest = guest;
        this.total = total;
    }

    public long getId() {
        return id;
    }
    public void setId(long id){ this.id = id; }

    public LocalDate getFrom() { return from; }
    public String getFromS() { return from.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); }

    public void setFrom(LocalDate from) { this.from = from; }

    public LocalDate getTo() { return to; }
    public String getToS() { return to.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); }

    public void setTo(LocalDate to) { this.to = to; }

    public int getRoomNum() {
        return roomNum;
    }

    public void setRoom(int roomNum) {
        this.roomNum = roomNum;
    }

    public Guest getGuest() {
        return guest;
    }

    public String getGuestName(){
        if(guest == null){ return "NaN"; }
        return (guest.getName() + " " + guest.getSurname());
    }


    public long getGuestId(){
        if(guest == null){ return -1; }
        return guest.getId();
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", from=" + from.toString() +
                ", to=" + to.toString() +
                ", roomNum=" + roomNum +
                ", guest=" + guest.getName() +
                ", total=" + total +
                '}';
    }
}


