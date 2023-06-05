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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
//ETML
//Auteur : Thilo Paetzel
//Date : 17.05.2023
//Description : Class Activity. Représente une page de l'application. Permet d'éditer les informations d'une PPE
public class EditPPEActivity extends AppCompatActivity {
    private EditText editName, editAddress;
    private Button modifyBtn;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ppeactivity);
        editName = findViewById(R.id.editAddName);
        editAddress = findViewById(R.id.editAddAddress);
        modifyBtn = findViewById(R.id.save_btn);
        db = FirebaseFirestore.getInstance();

        //Récupère les données du bundle
        Bundle bundle = getIntent().getExtras();
        String ppeId = bundle.getString("ppeId");
        String ppeName = bundle.getString("ppeName");
        String ppeAddress = bundle.getString("ppeAddress");

        //Mets l'ancienne addresse et l'ancien nom
        editName.setText(ppeName);
        editAddress.setText(ppeAddress);

        //Lorsque le boutton est cliqué
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editName.getText().toString().trim();//Nouveau nom
                String newAddress = editAddress.getText().toString().trim();//Nouvelle adresse
                updatePPE(newName, newAddress, ppeId);//Appel la méthode updatePPE
            }
        });
    }

    private void updatePPE(String newName, String newAdress, String ppeId){
        //Check si les champs ne sont pas vide
        if (TextUtils.isEmpty(newName)){
            editName.setError("Vide impossible");
        }
        else if (TextUtils.isEmpty(newAdress)){
            editAddress.setError("Vide impossible");
        } else {
            //Si les champs ne sont pas vide, le document est mis à jour
            db.collection("PPE").document(ppeId)
                    .update("Address", newAdress, "Name", newName)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(EditPPEActivity.this, "PPE : modifiée", Toast.LENGTH_SHORT).show();//Message en cas de succès
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditPPEActivity.this, "Erreur : " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();//Message en cas d'echec
                }
            });
        }
    }//Fin updatePPE
}