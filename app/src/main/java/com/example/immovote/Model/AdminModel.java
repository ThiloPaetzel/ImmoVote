package com.example.immovote.Model;


//ETML
//Auteur : Thilo Paetzel
//Date : 25.05.2023
//Description : Class model. Permet d'aller chercher dans la base de données la valeur d'un champ portant le même nom que la variable
public class AdminModel {
    private String firstName, lastName, email;

    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

}
