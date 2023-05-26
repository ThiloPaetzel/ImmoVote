package com.example.immovote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.immovote.Adapter.PPEAdapter;
import com.example.immovote.Model.PPEModel;
import com.example.immovote.Utils.UserIsAdmin;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
//ETML
//Auteur : Thilo Paetzel
//Date : 15.05.2023
//Description : Class Activity. Représente une page de l'application. Page d'accueil de l'application. Permet de voir les copropriété en fonction de ses droits
public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PPEAdapter adapter;
    private FloatingActionButton fab;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);

        //Ouvre la page d'ajout
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPpe = new Intent(getApplicationContext(), AddNewPPEActivity.class);
                startActivity(newPpe);
                //finish();
            }
        });


    }


    //Affichage du menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //Gestion du menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.LogoutMenu:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Voulez-vous vraiment vous déconnecter?").setTitle("Disconnect").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            case R.id.adduserMenu:
                if (UserIsAdmin.userIsAdmin == true){
                    Intent intent = new Intent(getApplicationContext(), AddUserActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    Toast.makeText(this, "Vous n'avez pas les permissions", Toast.LENGTH_SHORT).show();
                    return false;
                }
            case R.id.AddAdminMenu:
                if (UserIsAdmin.userIsAdmin == true){
                    Intent intent = new Intent(getApplicationContext(), AddAdministratorActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    Toast.makeText(this, "Vous n'avez pas les permissions", Toast.LENGTH_SHORT).show();
                    return false;
                }
            case R.id.ListAdminMenu:
                if (UserIsAdmin.userIsAdmin == true){
                    Intent intent = new Intent(getApplicationContext(), ListAdminActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    Toast.makeText(this, "Vous n'avez pas les permissions", Toast.LENGTH_SHORT).show();
                    return false;
                }

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //Permet de se déconnecter de son compte
    public void logout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }


    //Aide de chatGPT il y avait un problème lorsque l'utilisateur revenait sur la page d'accueil.
    //Il fallait enfait mettre à jour l'adapter dans la méthode onStart et non pas dans OnCreate afin de le mettre à jour lorsque l'utilisateur reviens sur la page
    @Override
    protected void onStart() {
        super.onStart();
        db.collection("Users").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    if (document.getBoolean("isAdmin") != null && document.getBoolean("isAdmin")) {
                        //L'utilisateur est admin
                        UserIsAdmin.userIsAdmin = true;
                        Toast.makeText(MainActivity.this, "You are admin", Toast.LENGTH_SHORT).show();
                        fab.setVisibility(View.VISIBLE); //Rend le bouton d'ajout visible

                        Query query = FirebaseFirestore.getInstance().collection("PPE");
                        FirestoreRecyclerOptions<PPEModel> options = new FirestoreRecyclerOptions.Builder<PPEModel>().setQuery(query, PPEModel.class).build();
                        adapter = new PPEAdapter(options);

                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        recyclerView.setAdapter(adapter);

                        adapter.startListening();
                    } else {
                        // Utilisateur non admin
                        UserIsAdmin.userIsAdmin = false;
                        Toast.makeText(MainActivity.this, "You are not admin", Toast.LENGTH_SHORT).show();
                        fab.setVisibility(View.GONE); //Rend le bouton d'ajout invisible

                        FirebaseFirestore.getInstance().collection("Users").document(currentUser.getEmail()).collection("myPPE").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<String> addresses = new ArrayList<>();
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    String address = documentSnapshot.getString("address");
                                    if (address != null) {
                                        addresses.add(address);
                                    }
                                }

                                // Utilise la liste des adresses pour afficher uniquement les PPE auxquelles l'utilisateur appartient
                                Query query = FirebaseFirestore.getInstance().collection("PPE").whereIn("Address", addresses);

                                FirestoreRecyclerOptions<PPEModel> options = new FirestoreRecyclerOptions.Builder<PPEModel>().setQuery(query, PPEModel.class).build();
                                adapter = new PPEAdapter(options);

                                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                recyclerView.setAdapter(adapter);

                                adapter.startListening();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
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