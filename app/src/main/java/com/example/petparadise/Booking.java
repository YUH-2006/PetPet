package com.example.petparadise;

public class Booking {
    private int id;
    private String userEmail;
    private String petName;
    private String serviceType;
    private String date;
    private String time;
    private String status;

    public Booking(int id, String userEmail, String petName, String serviceType, String date, String time, String status) {
        this.id = id;
        this.userEmail = userEmail;
        this.petName = petName;
        this.serviceType = serviceType;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public int getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getPetName() { return petName; }
    public String getServiceType() { return serviceType; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
}
