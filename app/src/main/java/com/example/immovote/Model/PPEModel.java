package com.example.immovote.Model;

//ETML
//Auteur : Thilo Paetzel
//Date : 15.05.2023
//Description : Class model. Permet d'aller chercher dans la base de données la valeur d'un champ portant le même nom que la variable
public class PPEModel {
    private String Name;
    private String Address;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }
}
