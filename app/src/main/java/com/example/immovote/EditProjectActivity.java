package com.example.immovote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
//ETML
//Auteur : Thilo Paetzel
//Date : 23.05.2023
//Description : Class Activity. Représente une page de l'application. Permet d'éditer un projet
public class EditProjectActivity extends AppCompatActivity {
    private EditText editTitle, editDescription, editCost;
    private TextView editStartDate, editEndDate;
    private Button editButton;
    private RadioButton editInProgress, editEnded;
    private RadioGroup radioGroup;

    private String finalStartDate, finaleEndDate;//Stocke les dates modifiés

    private FirebaseFirestore db;
    private int finalStatus = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Récupère les élément de la vue
        setContentView(R.layout.activity_edit_project);
        editCost = findViewById(R.id.editProjCost);
        editTitle = findViewById(R.id.editProjTitle);
        editDescription = findViewById(R.id.editProjDescription);
        editStartDate = findViewById(R.id.editSetStartDate);
        editEndDate = findViewById(R.id.editSetEndDate);
        editInProgress = findViewById(R.id.radioButtonInProgress);
        editEnded = findViewById(R.id.radioButtonEnded);
        editButton = findViewById(R.id.editAdd_btn);
        radioGroup = findViewById(R.id.radioGroup);
        db = FirebaseFirestore.getInstance();

        //Récupère les éléments du bundle
        Bundle bundle = getIntent().getExtras();
        String projectId = bundle.getString("projectId");
        String ppeId = bundle.getString("ppeId");
        String projectName = bundle.getString("projectName");
        String projectCost = bundle.getString("projectCost");
        String projectStartDate = bundle.getString("projectStartDate");
        String projectEndDate = bundle.getString("projectEndDate");
        String projDescription = bundle.getString("projectDescription");
        int projectStatus = bundle.getInt("projectStatus");

        initText(projectName, projectCost, projectStartDate, projectEndDate, projectStatus, projDescription);
        //Choix de la date de début
        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                int day = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditProjectActivity.this, new DatePickerDialog.OnDateSetListener() {
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
        //Choix de la date de fin
        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                int day = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditProjectActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButtonEnded:
                        finalStatus = 1;
                        break;
                    case R.id.radioButtonInProgress:
                        finalStatus = 0;
                        break;
                }
            }
        });

        //Boutton modifié cliqué
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newProjectTitle = editTitle.getText().toString().trim();
                String newProjectCost = editCost.getText().toString().trim();
                String newProjectStartDate = editStartDate.getText().toString().trim();
                String newProjectEndDate = editEndDate.getText().toString().trim();
                int newProjectStatus = finalStatus;
                String newProjectDescription = editDescription.getText().toString().trim();

                updateProject(newProjectTitle, newProjectCost, newProjectStartDate, newProjectEndDate,
                        newProjectStatus, newProjectDescription, projectId, ppeId);
            }
        });
    }

    private void updateProject(String newTitle, String newCost, String newStartDate, String newEndDate, int newStatus, String newDescription, String projectId, String ppeId) {
        //Check si tous les champs ont été remplis
        if (TextUtils.isEmpty(newTitle)){
            editTitle.setError("Vide impossible");
        } else if (TextUtils.isEmpty(newCost)) {
            editCost.setError("Vide impossible");
        } else if (TextUtils.isEmpty(newStartDate)) {
            editStartDate.setError("Vide impossible");
        } else if (TextUtils.isEmpty(newEndDate)) {
            editEndDate.setError("Vide impossible");
        } else if (newStatus == 3 ) {
            Toast.makeText(EditProjectActivity.this, "Veuillez sélectionner l'état du projet", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(newDescription)) {
            editDescription.setError("Vide Impossible");
        } else {
            //Si les champs ne sont pas vide le projet est mis à jour
            db.collection("PPE").document(ppeId).collection("Projet")
                    .document(projectId).update("Cost", newCost, "Description", newDescription, "EndDate",
                            newEndDate, "StartDate", newStartDate, "Status",
                            newStatus, "Title", newTitle).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(EditProjectActivity.this, "Projet modifié", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProjectActivity.this, "Erreur " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void initText(String title, String cost, String startDate,String endDate, int status, String description) {
        editTitle.setText(title);
        editCost.setText(cost);
        editStartDate.setText(startDate);
        editEndDate.setText(endDate);
        editDescription.setText(description);
        if (status == 1){
            editEnded.setChecked(true);
            finalStatus = 1;
        } else {
            editInProgress.setChecked(true);
            finalStatus = 0;
        }
    }
}