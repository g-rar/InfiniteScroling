package com.example.infinitescroling.models;

import java.util.Date;

public class Posts {
    private String firstNameUser;
    private String lastNameUser;
    private String description;
    private String image;
    private String video;
    private Date datePublication;

    public Posts(){

    }

    public Posts(String firstNameUser, String lastNameUser, String description, String multimedia, Date datePublication, boolean type){
        this.firstNameUser = firstNameUser;
        this.lastNameUser = lastNameUser;
        this.description = description;
        this.datePublication = datePublication;
        if(type)
            this.image = multimedia;
        else
            this.video = multimedia;

    }

    public Posts(String firstNameUser, String lastNameUser, String description, Date datePublication){
        this.firstNameUser = firstNameUser;
        this.lastNameUser = lastNameUser;
        this.description = description;
        this.datePublication = datePublication;
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
}
