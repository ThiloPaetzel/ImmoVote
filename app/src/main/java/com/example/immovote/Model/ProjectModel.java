package com.example.immovote.Model;

import com.google.firebase.Timestamp;

//ETML
//Auteur : Thilo Paetzel
//Date : 22.05.2023
//Description : Class model. Permet d'aller chercher dans la base de données la valeur d'un champ portant le même nom que la variable
public class ProjectModel {
    private String Title, Cost, Description;
    private String EndDate, StartDate;
    private int Status, VoteDown, VoteMiddle, VoteUp;

    public int getVoteDown() {
        return VoteDown;
    }

    public void setVoteDown(int voteDown) {
        VoteDown = voteDown;
    }

    public int getVoteMiddle() {
        return VoteMiddle;
    }

    public void setVoteMiddle(int voteMiddle) {
        VoteMiddle = voteMiddle;
    }

    public int getVoteUp() {
        return VoteUp;
    }

    public void setVoteUp(int voteUp) {
        VoteUp = voteUp;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }
}
