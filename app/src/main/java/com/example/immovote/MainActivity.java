package com.example.immovote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.immovote.Adapter.PPEAdapter;
import com.example.immovote.Model.PPEModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PPEAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ppeCollectionRef = db.collection("PPE");


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        //Requête Firestore pour récupérer les informations des PPE
        Query query = FirebaseFirestore.getInstance().collection("PPE");

        FirestoreRecyclerOptions<PPEModel> options = new FirestoreRecyclerOptions.Builder<PPEModel>().setQuery(query, PPEModel.class).build();

        //Initialiser l'adapter
        adapter = new PPEAdapter(options);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Lance l'écoute des modifications de la base de donnée
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Arrête l'écoute
        adapter.stopListening();
    }
}