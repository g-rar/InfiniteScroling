package com.example.infinitescroling.models;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;

public class User {

    private String firstName;
    private String lastName;
    private String city;
    private String gender;
    private String email;
    private Date birthDate;
    private String phoneNumber;
    private String profilePicture;
    private ArrayList<String> friendIds;
    private ArrayList<String> friendRequests;
    private ArrayList<String> requestsSent;

    public User(){

    }

    public User(String firstName, String lastName, String city, String gender, String email, Date birthDate, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.gender = gender;
        this.email = email;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        friendIds = new ArrayList<>();
    }

    public User(String firstName, String lastName, String city, String gender, String email, Date birthDate, String phoneNumber, String profilePicture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.gender = gender;
        this.email = email;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.profilePicture = profilePicture;
        friendIds = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public ArrayList<String> getFriendIds() {
        return friendIds;
    }

    public void setFriendIds(ArrayList<String> friendIds) {
        this.friendIds = friendIds;
    }

    public ArrayList<String> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(ArrayList<String> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public ArrayList<String> getRequestsSent() {
        return requestsSent;
    }

    public void setRequestsSent(ArrayList<String> requestsSent) {
        this.requestsSent = requestsSent;
    }

}
