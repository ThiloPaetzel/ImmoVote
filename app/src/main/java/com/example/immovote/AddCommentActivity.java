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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
//ETML
//Auteur : Thilo Paetzel
//Date : 26.05.2023
//Description : Class Activity. Représente une page de l'application. Permet d'ajouter un nouveau commentaire dans la base de données
public class AddCommentActivity extends AppCompatActivity {
    private EditText addTitle, addContent;
    private Button addCommentBtn;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        addCommentBtn = findViewById(R.id.addCommentBtn);
        addContent = findViewById(R.id.commentMessage);
        addTitle = findViewById(R.id.commentTile);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        //Récupère les éléments du bundle
        Bundle bundle = getIntent().getExtras();
        String projectId = bundle.getString("projectId");

        addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentTitle = addTitle.getText().toString().trim();
                String commentContent = addContent.getText().toString().trim();

                //Check si les champs sont vides
                if (TextUtils.isEmpty(commentContent)){
                    addContent.setError("Vide impossible");
                    return;
                }
                if (TextUtils.isEmpty(commentTitle)){
                    addTitle.setError("Vide impossible");
                    return;
                }

                //HashMap qui contient les informations du commentaire
                HashMap<String, Object> commentInfo = new HashMap<>();
                commentInfo.put("Message", commentContent);
                commentInfo.put("Project", projectId);
                commentInfo.put("Title", commentTitle);
                commentInfo.put("Creator", user.getEmail());
                db.collection("Comments").add(commentInfo)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(AddCommentActivity.this, "Votre commentaire à bien été ajouté", Toast.LENGTH_SHORT).show();
                        addTitle.setText("");
                        addContent.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddCommentActivity.this, "Erreur lors de l'ajout du commentaire : " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}