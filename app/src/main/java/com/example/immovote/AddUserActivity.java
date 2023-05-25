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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

//Class accessible uniquement en admin permettant d'ajouter un copropriétaire, un copropriétaire ne peut être ajouter uniquement dans une PPE existante dans la db
public class AddUserActivity extends AppCompatActivity {

    private EditText editLastName, editFirstName, editEmail, editPassword, editAddress;
    private Button saveUserBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        mAuth = FirebaseAuth.getInstance();//Instance d'authentification
        FirebaseFirestore db = FirebaseFirestore.getInstance();//Instance de la db
        editLastName = findViewById(R.id.editTextLastName);
        editFirstName = findViewById(R.id.editTextFirstName);
        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        editAddress = findViewById(R.id.editTextAddress);
        saveUserBtn = findViewById(R.id.buttonSaveUser);

        //Lors du clique sur le boutton
        saveUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastName, firstName, email, password, address;
                lastName = editLastName.getText().toString().trim();//trim permet d'avoir un string vide si uniquement des espaces
                firstName = editFirstName.getText().toString().trim();
                email = editEmail.getText().toString().trim();
                password = editPassword.getText().toString().trim();
                address = editAddress.getText().toString().trim();
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
                if (TextUtils.isEmpty(address)){
                    editAddress.setError("Vide impossible");
                    return;
                }

                //Vérifier si l'adresse de la copro existe
                Query query = db.collection("PPE").whereEqualTo("Address", address);//Query qui va vérifier si l'adresse existe dans un des documents de la collection PPE
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            //Check si la query a retourné un résultat
                            if (querySnapshot != null && !querySnapshot.isEmpty()){
                                //Méthode pour créer un nouvel utilisateur dans l'outil authentification de firebase
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
                                                        if (!document.exists()){
                                                            Map<String, Object> userInfo = new HashMap<>();
                                                            userInfo.put("firstName", firstName);
                                                            userInfo.put("lastName", lastName);
                                                            userInfo.put("isAdmin", false);
                                                            Map<String, Object> ppeInfo = new HashMap<>();
                                                            ppeInfo.put("address", address);
                                                            //Premier enregistrement de l'utilisateur il faut créer les collections et les documents du nouvel utilisateur
                                                            db.collection("Users").document(email).set(new HashMap<>());
                                                            db.collection("Users").document(email).collection("Info").add(userInfo);
                                                            db.collection("Users").document(email).collection("myPPE").add(ppeInfo);
                                                        }
                                                    }
                                                }
                                            });
                                            Toast.makeText(AddUserActivity.this, "Copropriétaire" + lastName.toString() + " " + firstName.toString() + " crée", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(AddUserActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(AddUserActivity.this, "Adresse de PPE inconue", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddUserActivity.this, "Erreur " +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}