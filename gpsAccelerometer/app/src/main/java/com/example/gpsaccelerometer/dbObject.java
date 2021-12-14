package com.example.gpsaccelerometer;

public class dbObject {
    private String accelType;
    private String location;
    private int speed;
    private String time;

    public dbObject(String accelType,String location, int speed,String time) {
        this.accelType = accelType;
        this.location = location;
        this.speed = speed;
        this.time = time;
    }

    void setAccelType(String accelType){
        this.accelType = accelType;
    }
    public String getAccelType(){
        return this.accelType;
    }
    void setLocation(String location){
        this.location=location;
    }
    public String getLocation(){
        return this.location;
    }
    void setSpeed(int speed){
        this.speed=speed;
    }
    public int getSpeed(){
        return this.speed;
    }
    void setTime(String time){
        this.time = time;
    }
    public String getTime(){
        return this.time;
    }
}
