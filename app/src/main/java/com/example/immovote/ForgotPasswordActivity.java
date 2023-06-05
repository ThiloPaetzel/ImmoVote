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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.List;
//ETML
//Auteur : Thilo Paetzel
//Date : 25.05.2023
//Description : Class Activity. Représente une page de l'application. Permet de reset son mot de passe
public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText userEmail;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        userEmail = findViewById(R.id.currentEmail);
        resetButton = findViewById(R.id.buttonResetPassword);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredEmail = userEmail.getText().toString().trim();
                if (TextUtils.isEmpty(enteredEmail)) {
                    userEmail.setError("Vide impossible");
                } else {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    //Vérification si l'email est bien associer à un compte
                    auth.fetchSignInMethodsForEmail(enteredEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.isSuccessful()) {
                                SignInMethodQueryResult result = task.getResult();
                                List<String> signInMethods = result.getSignInMethods();
                                if (signInMethods == null || signInMethods.isEmpty()) {
                                    // L'email n'est pas associé à un compte existant
                                    userEmail.setError("Email invalide");
                                } else {
                                    // L'email existe
                                    auth.sendPasswordResetEmail(enteredEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ForgotPasswordActivity.this, "Email envoyé, pensez à vérifier vos spams", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ForgotPasswordActivity.this, "Échec de l'envoi de l'email de réinitialisation", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                // Une erreur s'est produite lors de la récupération des méthodes de connexion
                                Toast.makeText(ForgotPasswordActivity.this, "Une erreur s'est produite", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });//Fin de l'écouteur du bouton de reset
    }
}