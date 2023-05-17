package edu.acg.ssh.androidproject_02;

public class Guest {

    private long id;
    private String name;
    private String surname;
    private String email;
    private String phone;


    public Guest(long id, String name, String surname, String email, String phone) {

        this.id = id;

        this.name = name;
        this.surname = surname;

        if(email.isEmpty()){ this.email = "-"; }
        else { this.email = email; }

        if(phone.isEmpty()){ this.phone = "-"; }
        else { this.phone = phone; }

    }

    public Guest(String name, String surname, String email, String phone) {

        this.id = 0;

        this.name = name;
        this.surname = surname;

        if(email.isEmpty()){ this.email = "-"; }
        else { this.email = email; }

        if(phone.isEmpty()){ this.phone = "-"; }
        else { this.phone = phone; }

    }


    public long getId() {
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Guest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
