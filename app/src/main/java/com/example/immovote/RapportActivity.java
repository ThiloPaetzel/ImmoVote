package com.example.immovote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.immovote.Adapter.ProjectAdapter;
import com.example.immovote.Adapter.RapportProjectAdapter;
import com.example.immovote.Model.ProjectModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//ETML
//Auteur : Thilo Paetzel
//Date : 23.05.2023
//Description : Class Activity. Représente une page de l'application. Correspond à la page de rapport de la PPE
public class RapportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView ppeName, cost;

    private RapportProjectAdapter adapter;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapport);
        recyclerView = findViewById(R.id.rapportRecyclerView);
        ppeName = findViewById(R.id.ppeName);
        cost = findViewById(R.id.projectCost);
        radioGroup = findViewById(R.id.radioGroupSelect);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Récupère les données du bundle
        Bundle bundle = getIntent().getExtras();
        String ppeId = bundle.getString("ppeId");
        String ppeNameData = bundle.getString("ppeName");

        //Set le texte du textView avec le nom de la PPE
        ppeName.setText(ppeNameData);

        //Si aucun radioButton n'est cliqué, affiche tous les projets

        Query query = FirebaseFirestore.getInstance().collection("PPE").document(ppeId).collection("Projet").whereNotEqualTo("Title", "init");

        FirestoreRecyclerOptions<ProjectModel> options = new FirestoreRecyclerOptions.Builder<ProjectModel>().setQuery(query, ProjectModel.class).build();
        adapter = new RapportProjectAdapter(options);

        recyclerView.setLayoutManager(new LinearLayoutManager(RapportActivity.this));
        recyclerView.setAdapter(adapter);

        //Va additionner grâce à la query tous les nombre présents dans les champs "Cost" des documents
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    double totalCostProjectEnded = 0.0;
                    for (DocumentSnapshot document : task.getResult()){
                        String documentCost = document.getString("Cost");
                        if (documentCost!=null){
                            double costValueTotal = Double.parseDouble(documentCost);
                            totalCostProjectEnded += costValueTotal;
                        }
                    }
                    cost.setText("Coûts de projets : " + totalCostProjectEnded);
                } else {
                    Toast.makeText(RapportActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        adapter.startListening();


        //Lorsqu'un radioButton est cliqué
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    //Si le boutton Terminés est cliqué affiche uniquement les projets finis
                    case R.id.radioButtonEndedSelect:
                        Query queryEnded = FirebaseFirestore.getInstance().collection("PPE")
                                .document(ppeId)
                                .collection("Projet")
                                .whereEqualTo("Status", 1);
                        FirestoreRecyclerOptions<ProjectModel> optionsEnded = new FirestoreRecyclerOptions.Builder<ProjectModel>()
                                .setQuery(queryEnded, ProjectModel.class).build();
                        adapter = new RapportProjectAdapter(optionsEnded);
                        recyclerView.setLayoutManager(new LinearLayoutManager(RapportActivity.this));
                        recyclerView.setAdapter(adapter);
                        adapter.startListening();
                        //Va additionner grâce à la query tous les nombre présents dans les champs "Cost" des documents
                        queryEnded.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    double totalCostProjectEnded = 0.0;
                                    for (DocumentSnapshot document : task.getResult()){
                                        String documentCost = document.getString("Cost");
                                        if (documentCost!=null){
                                            double costValueTotal = Double.parseDouble(documentCost);
                                            totalCostProjectEnded += costValueTotal;
                                        }
                                    }
                                    cost.setText("Coûts de projets : " + totalCostProjectEnded);
                                } else {
                                    Toast.makeText(RapportActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    case R.id.radioButtonInProgressSelect:
                        //Si le boutton En cours est cliqué affiche uniquement les projets en cours s'affiche
                        Query queryInProgress = FirebaseFirestore.getInstance().collection("PPE").document(ppeId).collection("Projet").whereEqualTo("Status", 0);

                        FirestoreRecyclerOptions<ProjectModel> optionsInProgress = new FirestoreRecyclerOptions.Builder<ProjectModel>().setQuery(queryInProgress, ProjectModel.class).build();
                        adapter = new RapportProjectAdapter(optionsInProgress);

                        recyclerView.setLayoutManager(new LinearLayoutManager(RapportActivity.this));
                        recyclerView.setAdapter(adapter);

                        adapter.startListening();

                        //Va additionner grâce à la query tous les nombre présents dans les champs "Cost" des documents
                        queryInProgress.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    double totalCostProjectEnded = 0.0;
                                    for (DocumentSnapshot document : task.getResult()){
                                        String documentCost = document.getString("Cost");
                                        if (documentCost!=null){
                                            double costValueTotal = Double.parseDouble(documentCost);
                                            totalCostProjectEnded += costValueTotal;
                                        }
                                    }
                                    cost.setText("Coûts de projets : " + totalCostProjectEnded);
                                } else {
                                    Toast.makeText(RapportActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Arrête l'écoute
        adapter.stopListening();
    }
}