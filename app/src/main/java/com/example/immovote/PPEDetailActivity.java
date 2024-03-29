package com.example.immovote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.immovote.Adapter.PPEAdapter;
import com.example.immovote.Adapter.ProjectAdapter;
import com.example.immovote.Model.PPEModel;
import com.example.immovote.Model.ProjectModel;
import com.example.immovote.Utils.UserIsAdmin;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

//ETML
//Auteur : Thilo Paetzel
//Date : 22.05.2023
//Description : Class Activity. Représente une page de l'application. Représente la page de détail d'une PPE
public class PPEDetailActivity extends AppCompatActivity {
    private TextView name, address;
    private FirebaseFirestore db;
    private ProjectAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton addProject;
    private Button rapportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppedetail);
        name = findViewById(R.id.ppeName);
        address = findViewById(R.id.ppeAddress);
        recyclerView = findViewById(R.id.ppeDetailRecyclerView);
        db = FirebaseFirestore.getInstance();
        addProject = findViewById(R.id.addProjectFab);
        rapportButton = findViewById(R.id.rapportButton);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (UserIsAdmin.userIsAdmin == false){
            addProject.setVisibility(View.GONE);
        }

        //Récupère les données du bundle
        Bundle bundle = getIntent().getExtras();
        String ppeId = bundle.getString("ppeId");
        String ppeName = bundle.getString("ppeName");
        String ppeAddress = bundle.getString("ppeAddress");

        //Set les texts avec les informations de la PPE
        name.setText(ppeName);
        address.setText(ppeAddress);
        Query query = FirebaseFirestore.getInstance().collection("PPE").document(ppeId).collection("Projet").whereNotEqualTo("Title", "init");

        FirestoreRecyclerOptions<ProjectModel> options = new FirestoreRecyclerOptions.Builder<ProjectModel>().setQuery(query, ProjectModel.class).build();
        adapter = new ProjectAdapter(options);

        recyclerView.setLayoutManager(new LinearLayoutManager(PPEDetailActivity.this));
        recyclerView.setAdapter(adapter);

        adapter.startListening();

        //Ouvre l'activité d'ajout d'un projet
        addProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle addProjectBundle = new Bundle();
                addProjectBundle.putString("ppeId", bundle.getString("ppeId"));//Id de la PPE

                Intent intent = new Intent(getApplicationContext(), AddNewProject.class);
                intent.putExtras(addProjectBundle);
                startActivity(intent);
            }
        });

        //Clique sur rapport
        rapportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle rapportBundle = new Bundle();
                rapportBundle.putString("ppeId", bundle.getString("ppeId"));//Id de la PPE
                rapportBundle.putString("ppeName", bundle.getString("ppeName"));

                Intent intent = new Intent(getApplicationContext(), RapportActivity.class);
                intent.putExtras(rapportBundle);
                startActivity(intent);
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