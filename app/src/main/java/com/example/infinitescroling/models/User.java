package com.example.infinitescroling.models;

import java.util.Date;

public class User {
    private String name;
    private String lastName;
    private String city;
    private String gender;
    private String email;
    private Date birthDate;
    private int phoneNumber;

    public User(){

    }

    public User(String name, String lastName, String city, String gender, String email, Date birthDate, int phoneNumber) {
        this.name = name;
        this.lastName = lastName;
        this.city = city;
        this.gender = gender;
        this.email = email;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
