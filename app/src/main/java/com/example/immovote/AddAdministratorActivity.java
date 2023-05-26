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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
//ETML
//Auteur : Thilo Paetzel
//Date : 25.05.2023
//Description : Class Activity. Représente une page de l'application. Permet d'ajouter un nouvel administrateur dans la base de données
public class AddAdministratorActivity extends AppCompatActivity {

    private EditText editLastName, editFirstName, editEmail, editPassword;
    private Button saveAdminBtn;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_administrator);
        mAuth = FirebaseAuth.getInstance();//Instance d'authentification
        editLastName = findViewById(R.id.editTextLastName);
        editFirstName = findViewById(R.id.editTextFirstName);
        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        saveAdminBtn = findViewById(R.id.buttonSaveAdministrator);

        saveAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastName, firstName, email, password;
                lastName = editLastName.getText().toString().trim();//trim permet d'avoir un string vide si uniquement des espaces
                firstName = editFirstName.getText().toString().trim();
                email = editEmail.getText().toString().trim();
                password = editPassword.getText().toString().trim();

                //Check si les champs sont remplis
                if (TextUtils.isEmpty(lastName)){
                    editLastName.setError("Vide impossible");
                    return;
                }
                if (TextUtils.isEmpty(firstName)){
                    editFirstName.setError("Vide impossible");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    editEmail.setError("Vide impossible");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    editPassword.setError("Vide impossible");
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userDocRef = db.collection("Users").document(email);//Référence du document de l'utilisateur
                            userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        //Si le document n'existe pas ajoute toutes les données de l'utilisateurs dans les collections necessaire
                                        if (!document.exists()) {
                                            Map<String, Object> userInfo = new HashMap<>();
                                            userInfo.put("firstName", firstName);
                                            userInfo.put("lastName", lastName);
                                            userInfo.put("isAdmin", true);
                                            userInfo.put("email", email);

                                            //Enregistrement de l'utilisateur, il faut créer le document du nouvel utilisateur
                                            db.collection("Users").document(email).set(userInfo)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Map<String, Object> ppeInfo = new HashMap<>();
                                                            ppeInfo.put("address", "isAdmin");

                                                            // Ajouter le document "myPPE" pour l'utilisateur
                                                            db.collection("Users").document(email).collection("myPPE").add(ppeInfo)
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                            //Ajout de sa PPE réussi
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(AddAdministratorActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(AddAdministratorActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });
                            Toast.makeText(AddAdministratorActivity.this, "Administrateur " + lastName.toString() + " " + firstName.toString() + " crée", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddAdministratorActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}