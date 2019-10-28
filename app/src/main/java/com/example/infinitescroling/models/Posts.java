package com.example.infinitescroling.models;

import java.util.ArrayList;
import java.util.Date;

public class Posts {
    private String firstNameUser;
    private String lastNameUser;
    private String description;
    private String image;
    private String imgProfile;
    private String video;
    private String postedBy;
    private ArrayList<String> likes;
    private ArrayList<String> dislikes;
    private ArrayList<String> friends;
    private Date datePublication;

    public Posts(){

    }

    public Posts(String firstNameUser, String lastNameUser, String postedBy, String imgProfile, ArrayList<String> friendsIds){
        this.firstNameUser = firstNameUser;
        this.lastNameUser = lastNameUser;
        this.likes = new ArrayList<String>();
        this.dislikes = new ArrayList<String>();
        this.friends = friendsIds;
        this.postedBy = postedBy;
        this.imgProfile = imgProfile;
    }


    public String getFirstNameUser() {
        return firstNameUser;
    }

    public void setFirstNameUser(String firstNameUser) {
        this.firstNameUser = firstNameUser;
    }

    public String getLastNameUser() {
        return lastNameUser;
    }

    public void setLastNameUser(String lastNameUser) {
        this.lastNameUser = lastNameUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public Date getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Date datePublication) {
        this.datePublication = datePublication;
    }

    public String getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(String imgProfile) {
        this.imgProfile = imgProfile;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public ArrayList<String> getDislikes() {
        return dislikes;
    }

    public void setDislikes(ArrayList<String> dislikes) {
        this.dislikes = dislikes;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }
}
