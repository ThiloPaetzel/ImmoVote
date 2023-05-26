package com.example.immovote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
//ETML
//Auteur : Thilo Paetzel
//Date : 25.05.2023
//Description : Class Activity. Représente une page de l'application. Permet d'éditer le profile d'un administrateur
public class EditAdminProfile extends AppCompatActivity {

    private EditText newLastName, newFirstName;
    private Button editAdmin;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_admin_profile);
        editAdmin = findViewById(R.id.editAdminBtn);
        newLastName = findViewById(R.id.editAdminLastName);
        newFirstName = findViewById(R.id.editAdminFirstName);
        db = FirebaseFirestore.getInstance();

        //Récupère les informations du bundle
        Bundle bundle = getIntent().getExtras();
        String lastNameData = bundle.getString("adminLastName");
        String firstNameData = bundle.getString("adminFirstName");
        String adminDocumentId = bundle.getString("adminId");

        newLastName.setText(lastNameData);
        newFirstName.setText(firstNameData);

        editAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = newFirstName.getText().toString().trim();
                String lastName = newLastName.getText().toString().trim();
                updateAdminProfile(firstName, lastName, adminDocumentId);
            }
        });

    }

    private void updateAdminProfile(String firstName, String lastName, String adminDocumentId) {
        //Check si les champs ne sont pas vide
        if (TextUtils.isEmpty(firstName)){
            newFirstName.setError("Vide impossible");
        } else if (TextUtils.isEmpty(lastName)) {
            newLastName.setError("Vide impossible");
        } else{
            db.collection("Users").document(adminDocumentId).update("firstName", firstName, "lastName", lastName).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(EditAdminProfile.this, "Profile : modifié", Toast.LENGTH_SHORT).show();//Message en cas de succès
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditAdminProfile.this, "Erreur : " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();//Message en cas d'echec
                }
            });
        }
    }
}