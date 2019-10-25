package com.example.infinitescroling.models;

import java.util.Date;

public class AcademicInfo {

    private String title;
    private String institution;
    private Date beginDate;
    private Date conclusionDate;

    public AcademicInfo(String title, String institution, Date beginDate, Date conclusionDate) {
        this.title = title;
        this.institution = institution;
        this.beginDate = beginDate;
        this.conclusionDate = conclusionDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(Date conclusionDate) {
        this.conclusionDate = conclusionDate;
    }
}
