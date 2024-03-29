package com.example.immovote.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.immovote.Model.ProjectModel;
import com.example.immovote.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

//ETML
//Auteur : Thilo Paetzel
//Date : 15.05.2023
//Description : Class adapter. Permet de gérer tous les éléments présent dans le recyclerView qui affiche tous les Projets dans le rapport
public class RapportProjectAdapter extends FirestoreRecyclerAdapter<ProjectModel, RapportProjectAdapter.RapportProjectViewHolder> {

    private FirebaseFirestore firestore;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RapportProjectAdapter(@NonNull FirestoreRecyclerOptions<ProjectModel> options) {
        super(options);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull RapportProjectViewHolder holder, int position, @NonNull ProjectModel model) {
        holder.name.setText(model.getTitle());
        holder.cost.setText(model.getCost());
        holder.startDate.setText(model.getStartDate());
        holder.endDate.setText(model.getEndDate());
        holder.vote.setText("Vote Pour : " + model.getVoteUp() + "  Vote Contre : " + model.getVoteDown() + "  Vote Abstention : " + model.getVoteMiddle());
        //Set le texte en fonction du champs dans la db
        if (model.getStatus() == 0){
            holder.status.setText("En cours");
        } else {
            holder.status.setText("Terminé");
        }
    }

    @NonNull
    @Override
    public RapportProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Permet d'ajouter le layout créer précédement au recycler view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_project_rapport, parent, false);
        return new RapportProjectAdapter.RapportProjectViewHolder(view);
    }

    //Sous classe viewHolder représente les items de la vue dans le recyclerView
    public class RapportProjectViewHolder extends RecyclerView.ViewHolder{

        TextView name, cost, startDate, endDate, status, vote;
        public RapportProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            //Va chercher les éléments dans la vue
            name = itemView.findViewById(R.id.textName);
            cost = itemView.findViewById(R.id.textCost);
            startDate = itemView.findViewById(R.id.textStart);
            endDate = itemView.findViewById(R.id.textEnd);
            status = itemView.findViewById(R.id.textStatus);
            vote = itemView.findViewById(R.id.textVote);
        }
    }
}
