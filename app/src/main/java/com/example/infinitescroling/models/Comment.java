package com.example.infinitescroling.models;

import java.util.Date;

public class Comment {

    private Date dateComment;
    private String description;
    private String idUser;

    public Comment(){}

    public Comment(Date dateComment, String description) {
        this.dateComment = dateComment;
        this.description = description;
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
}
