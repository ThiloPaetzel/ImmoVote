package com.example.immovote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddNewProject extends AppCompatActivity {

    private EditText editName, editDescription, editCost;
    private TextView editStartDate, editEndDate;
    private Button addBtn;
    private FirebaseFirestore db;
    private String finalStartDate, finaleEndDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_project);
        editName = findViewById(R.id.projTitle);
        editDescription = findViewById(R.id.projDescription);
        editCost = findViewById(R.id.projCost);
        editStartDate = findViewById(R.id.setStartDate);
        editEndDate = findViewById(R.id.setEndDate);
        addBtn = findViewById(R.id.add_btn);
        db = FirebaseFirestore.getInstance();

        //Récupère les données du bundle
        Bundle bundle = getIntent().getExtras();
        String ppeId = bundle.getString("ppeId");

        //Permet à l'utilisateur de sélectionner la date de début dans un calendrier
        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                int day = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddNewProject.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month +1;
                        editStartDate.setText(dayOfMonth + "/" + month + "/" +year);
                        finalStartDate = dayOfMonth + "/" + month + "/" + year;
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        //Permet à l'utilisateur de sélectionner la date de fin dans un calendrier
        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                int day = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddNewProject.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month +1;
                        editEndDate.setText(dayOfMonth + "/" + month + "/" +year);
                        finaleEndDate = dayOfMonth + "/" + month + "/" + year;
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });



        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, description, cost, startDate, endDate;
                name = editName.getText().toString().trim();
                description = editDescription.getText().toString().trim();
                cost = editCost.getText().toString().trim();
                startDate = editStartDate.toString().trim();
                endDate = editEndDate.toString().trim();
                //Check si les champs sont remplis
                if (TextUtils.isEmpty(name)){
                    editName.setError("Null impossible");
                    return;
                }
                if (TextUtils.isEmpty(description)){
                    editDescription.setError("Null impossible");
                    return;
                }
                if (TextUtils.isEmpty(cost)){
                    editCost.setError("Null impossible");
                    return;
                }
                if (TextUtils.isEmpty(finalStartDate)){
                    editStartDate.setError("Null impossible");
                    return;
                }
                if (TextUtils.isEmpty(finaleEndDate)){
                    editEndDate.setError("Null impossible");
                    return;
                }

                //HashMap qui va stocker les informations du projet ajouté
                Map<String, Object> projectInfo = new HashMap<>();
                projectInfo.put("Title", name);
                projectInfo.put("Status", 0);
                projectInfo.put("StartDate", finalStartDate);
                projectInfo.put("EndDate", finaleEndDate);
                projectInfo.put("Description", description);
                projectInfo.put("Cost", cost);

                //Ajoute le document du projet ajouté dans la db
                db.collection("PPE").document(ppeId).collection("Projet").add(projectInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Message de réussite pour l'utilisateur
                        Toast.makeText(AddNewProject.this, "Projet ajouté avec succès", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Message d'erreur pour l'utilisateur
                        Toast.makeText(AddNewProject.this, "Erreur " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}