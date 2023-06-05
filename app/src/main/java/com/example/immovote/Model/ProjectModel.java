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


    public int getVoteMiddle() {
        return VoteMiddle;
    }


    public int getVoteUp() {
        return VoteUp;
    }


    public int getStatus() {
        return Status;
    }


    public String getTitle() {
        return Title;
    }


    public String getCost() {
        return Cost;
    }


    public String getDescription() {
        return Description;
    }


    public String getEndDate() {
        return EndDate;
    }


    public String getStartDate() {
        return StartDate;
    }

}
