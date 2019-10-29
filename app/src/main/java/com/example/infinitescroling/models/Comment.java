package com.example.infinitescroling.models;

import java.util.Date;

public class Comment {

    private String firstName;
    private String lastName;
    private Date dateComment;
    private String description;
    private String idUser;
    private String image;

    public Comment(){}

    public Comment(String firstName, String lastName, Date dateComment, String description) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateComment = dateComment;
        this.description = description;
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

    public Date getDateComment() {
        return dateComment;
    }

    public void setDateComment(Date dateComment) {
        this.dateComment = dateComment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
