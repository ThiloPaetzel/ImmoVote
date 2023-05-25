package com.example.immovote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.immovote.Utils.UserIsAdmin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProjectDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView projectName, projectCost, projectDates, projectDescription, projectVotes;
    private FirebaseFirestore db;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    private String projectIdData, ppeIdData, projectNameData, projectCostData, projectStartDateData, projectEndDateData, projectDescriptionData;
    private int projectVoteUpData, projectVoteDownData, projectVoteMiddleData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        //Récupère les éléments de la vue
        recyclerView = findViewById(R.id.commentRecyclerView);
        projectName = findViewById(R.id.projectTitle);
        projectCost = findViewById(R.id.projectCost);
        projectDates = findViewById(R.id.projectDate);
        projectDescription = findViewById(R.id.projectDescription);
        projectVotes = findViewById(R.id.projectVotes);
        db = FirebaseFirestore.getInstance();

        //Récupère les éléments du bundle
        Bundle bundle = getIntent().getExtras();
        projectIdData = bundle.getString("projectId");
        ppeIdData = bundle.getString("ppeId");
        projectNameData = bundle.getString("projectName");
        projectCostData = bundle.getString("projectCost");
        projectStartDateData = bundle.getString("projectStartDate");
        projectEndDateData = bundle.getString("projectEndDate");
        projectDescriptionData = bundle.getString("projectDescription");
        projectVoteUpData = bundle.getInt("projectVoteUp");
        projectVoteDownData = bundle.getInt("projectVoteDown");
        projectVoteMiddleData = bundle.getInt("projectVoteMiddle");
        //int projectStatusData = bundle.getInt("projectStatus");
        //Set les textes
        projectName.setText(projectNameData);
        projectCost.setText(projectCostData);
        projectDates.setText("Début : " + projectStartDateData + " Fin : " + projectEndDateData);
        projectVotes.setText("Vote pour : " + projectVoteUpData + " Contres : " + projectVoteDownData + " Abstention : " + projectVoteMiddleData);
        projectDescription.setText(projectDescriptionData);


    }

    //Affichage du menu de vote

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vote_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.voteUpMenu:
                if (UserIsAdmin.userIsAdmin == false) {
                    //Référence du document du projet
                    DocumentReference projectRef = db.collection("PPE").document(ppeIdData).collection("Projet").document(projectIdData);

                    //Vérifier si le document existe déjà dans la collection "VotedBy"
                    projectRef.collection("VotedBy").whereEqualTo("User", currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null && !task.getResult().isEmpty()) {
                                    //L'utilisateur a déjà voté vérifier le vote d'avant
                                    DocumentSnapshot previousVote = task.getResult().getDocuments().get(0);
                                    String previousVoteValue = previousVote.getString("Value");

                                    if (previousVoteValue.equals("VoteUp")) {
                                        //L'utilisateur avait déjà voté pour
                                        Toast.makeText(ProjectDetailActivity.this, "Vous avez déjà voté pour ce projet", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //Supprimer le vote précédent et mettre à jour le champ VoteUp
                                        previousVote.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                projectRef.update("VoteUp", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(ProjectDetailActivity.this, "Vous avez voté pour ce projet", Toast.LENGTH_SHORT).show();
                                                        addNewVote("VoteUp");

                                                        // Retirer le vote précédent
                                                        projectRef.update(previousVoteValue, FieldValue.increment(-1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(ProjectDetailActivity.this, "Erreur " + e.getMessage().toString() + " lors de la decrémentation du vote précédent", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ProjectDetailActivity.this, "Erreur : " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProjectDetailActivity.this, "Erreur lors de la suppression du vote précédent", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } else {
                                    //Le document n'existe pas mettre à jour le champ VoteUp, l'utilisateur n'a pas voté
                                    projectRef.update("VoteUp", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ProjectDetailActivity.this, "Vous avez voté pour ce projet", Toast.LENGTH_SHORT).show();
                                            addNewVote("VoteUp");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProjectDetailActivity.this, "Erreur : " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(ProjectDetailActivity.this, "Erreur lors de la vérification du vote précédent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ProjectDetailActivity.this, "Seuls les copropriétaires peuvent voter", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.voteDownMenu:
                if (UserIsAdmin.userIsAdmin == false) {
                    //Référence du document du projet
                    DocumentReference projectRef = db.collection("PPE").document(ppeIdData).collection("Projet").document(projectIdData);

                    //Vérifier si le document existe déjà dans la collection "VotedBy"
                    projectRef.collection("VotedBy").whereEqualTo("User", currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null && !task.getResult().isEmpty()) {
                                    //L'utilisateur a déjà voté vérifier le vote précédent
                                    DocumentSnapshot previousVote = task.getResult().getDocuments().get(0);
                                    String previousVoteValue = previousVote.getString("Value");

                                    if (previousVoteValue.equals("VoteDown")) {
                                        //L'utilisateur avait déjà voté contre
                                        Toast.makeText(ProjectDetailActivity.this, "Vous avez déjà voté pour ce projet", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //Supprimer le vote précédent et mettre à jour le champ VoteDown
                                        previousVote.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                projectRef.update("VoteDown", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(ProjectDetailActivity.this, "Vous avez voté contre ce projet", Toast.LENGTH_SHORT).show();
                                                        addNewVote("VoteDown");

                                                        //Retire le vote précédent
                                                        projectRef.update(previousVoteValue, FieldValue.increment(-1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(ProjectDetailActivity.this, "Erreur " + e.getMessage().toString() + " lors de la decrémentation du vote précédent", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ProjectDetailActivity.this, "Erreur : " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProjectDetailActivity.this, "Erreur lors de la suppression du vote précédent", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } else {
                                    //Le document n'existe pas mettre à jour le champ VoteDown, l'utilisateur n'a donc pas voté
                                    projectRef.update("VoteDown", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ProjectDetailActivity.this, "Vous avez voté contre ce projet", Toast.LENGTH_SHORT).show();
                                            addNewVote("VoteDown");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProjectDetailActivity.this, "Erreur : " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(ProjectDetailActivity.this, "Erreur lors de la vérification du vote précédent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ProjectDetailActivity.this, "Seuls les copropriétaires peuvent voter", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.voteNeutralMenu:
                if (UserIsAdmin.userIsAdmin == false) {
                    //Référence du document du projet
                    DocumentReference projectRef = db.collection("PPE").document(ppeIdData).collection("Projet").document(projectIdData);

                    //Vérifier si le document existe déjà dans la collection "VotedBy"
                    projectRef.collection("VotedBy").whereEqualTo("User", currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null && !task.getResult().isEmpty()) {
                                    //L'utilisateur a déjà voté vérifier le vote précédent
                                    DocumentSnapshot previousVote = task.getResult().getDocuments().get(0);
                                    String previousVoteValue = previousVote.getString("Value");

                                    if (previousVoteValue.equals("VoteMiddle")) {
                                        //L'utilisateur avait déjà voté abstention
                                        Toast.makeText(ProjectDetailActivity.this, "Vous avez déjà voté abstention pour ce projet", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //Supprimer le vote précédent et mettre à jour le champ VoteMiddle
                                        previousVote.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                projectRef.update("VoteMiddle", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(ProjectDetailActivity.this, "Vous avez voté abstention pour ce projet", Toast.LENGTH_SHORT).show();
                                                        addNewVote("VoteMiddle");

                                                        //Retire le vote précédent
                                                        projectRef.update(previousVoteValue, FieldValue.increment(-1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(ProjectDetailActivity.this, "Erreur " + e.getMessage().toString() + " lors de la decrémentation du vote précédent", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ProjectDetailActivity.this, "Erreur : " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProjectDetailActivity.this, "Erreur lors de la suppression du vote précédent", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } else {
                                    //Le document n'existe pas mettre à jour le champ VoteUp, l'utilisateur n'a donc pas voté
                                    projectRef.update("VoteMiddle", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ProjectDetailActivity.this, "Vous avez voté abstention pour ce projet", Toast.LENGTH_SHORT).show();
                                            addNewVote("VoteMiddle");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProjectDetailActivity.this, "Erreur : " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(ProjectDetailActivity.this, "Erreur lors de la vérification du vote précédent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ProjectDetailActivity.this, "Seuls les copropriétaires peuvent voter", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Méthode pour ajouter un nouveau document de vote dans la collection "VotedBy"
    private void addNewVote(String voteValue){
        // Référence du document du projet
        DocumentReference projectRef = db.collection("PPE").document(ppeIdData).collection("Projet").document(projectIdData);

        // Ajout du document représentant le vote de l'utilisateur dans la collection "votedBy"
        Map<String, Object> votedByData = new HashMap<>();
        votedByData.put("Value", voteValue);
        votedByData.put("User", currentUser.getEmail());

        projectRef.collection("VotedBy").add(votedByData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(ProjectDetailActivity.this, "Document ajouté dans VotedBy", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProjectDetailActivity.this, "Erreur : " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}