package com.example.immovote.Model;

//ETML
//Auteur : Thilo Paetzel
//Date : 26.05.2023
//Description : Class model. Permet d'aller chercher dans la base de données la valeur d'un champ portant le même nom que la variable
public class CommentModel {
    private String Message, Project, Title, Creator;

    public String getCreator() {
        return Creator;
    }

    public void setCreator(String creator) {
        Creator = creator;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getProject() {
        return Project;
    }

    public void setProject(String project) {
        Project = project;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
