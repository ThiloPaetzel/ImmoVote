package com.example.immovote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
//ETML
//Auteur : Thilo Paetzel
//Date : 16.05.2023
//Description : Class Activity. Représente une page de l'application. Permet d'ajouter une nouvelle PPE dans la base de données
public class AddNewPPEActivity extends AppCompatActivity {

    private EditText editName, editAddress;
    private Button saveBtn;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_ppeactivity);
        editName = findViewById(R.id.AddName);
        editAddress = findViewById(R.id.AddAddress);
        saveBtn = findViewById(R.id.add_btn);
        db = FirebaseFirestore.getInstance();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, adresse;
                name = editName.getText().toString().trim();
                adresse = editAddress.getText().toString().trim();

                //Check si les champs sont remplis
                if (TextUtils.isEmpty(name)){
                    editName.setError("Vide impossible");
                    return;
                }
                else if (TextUtils.isEmpty(adresse)){
                    editAddress.setError("Vide impossible");
                    return;
                }

                //Vérification si l'adresse de la PPE existe déjà
                Query query = db.collection("PPE").whereEqualTo("Address", adresse);

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot.isEmpty()){
                                //Adresse n'existe pas encore
                                Map<String, Object> ppeInfo = new HashMap<>();
                                ppeInfo.put("Name", name);
                                ppeInfo.put("Address", adresse);
                                db.collection("PPE").add(ppeInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(AddNewPPEActivity.this, "PPE ajoutée ", Toast.LENGTH_SHORT).show();

                                        //Id du document crée
                                        String newPpeId = documentReference.getId();

                                        //Collection projet pour la copropriété
                                        Map<String, Object> projCollection = new HashMap<>();
                                        projCollection.put("Title", "init");

                                        //Ajout de la collection Projet à la PPE qui contient un document d'initialisation de la collection
                                        db.collection("PPE").document(newPpeId).collection("Projet").document("Document_d'initialisation").set(projCollection).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(AddNewPPEActivity.this, "Collection projet ajoutée", Toast.LENGTH_SHORT).show();
                                                //Reset les champs de texte
                                                editAddress.setText("");
                                                editName.setText("");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddNewPPEActivity.this, "Erreur lors de l'ajout de Projet " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddNewPPEActivity.this, "Erreur lors de l'ajout de la PPE " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else{
                                Toast.makeText(AddNewPPEActivity.this, "La copropriété existe déjà dans la base de données", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddNewPPEActivity.this, "Erreur " + task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}