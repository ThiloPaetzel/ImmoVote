package com.example.immovote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.immovote.Adapter.AdminAdapter;
import com.example.immovote.Adapter.PPEAdapter;
import com.example.immovote.Model.AdminModel;
import com.example.immovote.Model.PPEModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
//ETML
//Auteur : Thilo Paetzel
//Date : 25.05.2023
//Description : Class Activity. Représente une page de l'application. Permet de voir la liste des administrateurs
public class ListAdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_admin);
        recyclerView = findViewById(R.id.recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Utilise la list avec toutes les addresses pour afficher depuis la collection "Users" uniquement les utilisateurs admin
        Query query = FirebaseFirestore.getInstance().collection("Users").whereEqualTo("isAdmin", true);

        FirestoreRecyclerOptions<AdminModel> options = new FirestoreRecyclerOptions.Builder<AdminModel>().setQuery(query, AdminModel.class).build();
        adapter = new AdminAdapter(options);

        recyclerView.setLayoutManager(new LinearLayoutManager(ListAdminActivity.this));
        recyclerView.setAdapter(adapter);

        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Arrête l'écoute
        adapter.stopListening();
    }
}