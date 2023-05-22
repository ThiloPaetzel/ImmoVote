package com.example.immovote.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.immovote.Model.ProjectModel;
import com.example.immovote.R;
import com.example.immovote.Utils.UserIsAdmin;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

//Adapter qui permet d'afficher tous les projets des copropriétés
public class ProjectAdapter extends FirestoreRecyclerAdapter<ProjectModel, ProjectAdapter.ProjectViewHolder> {

    private FirebaseFirestore firestore;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ProjectAdapter(@NonNull FirestoreRecyclerOptions<ProjectModel> options) {
        super(options);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull ProjectViewHolder holder, int position, @NonNull ProjectModel model) {
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
        holder.cost.setText(model.getCost());
        holder.startDate.setText(model.getStartDate());
        holder.endDate.setText(model.getEndDate());
        //Set le texte en fonction du champs dans la db
        if (model.getStatus() == 0){
            holder.status.setText("En cours");
        } else {
            holder.status.setText("Terminé");
        }
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Permet d'ajouter le layout créer précédement au recycler view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_project, parent, false);
        return new ProjectAdapter.ProjectViewHolder(view);
    }

    public void deleteItem(int position){
        if (UserIsAdmin.userIsAdmin == true){
            //Supprime l'élément à la position donnée dans la base de données Firestore
            getSnapshots().getSnapshot(position).getReference().delete();
        }
    }

    public class ProjectViewHolder extends RecyclerView.ViewHolder{

        TextView cost, description, endDate, startDate, title, status;
        FloatingActionButton deleteBtn, updateBtn, detailBtn;
        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            //Va chercher les éléments dans la vue
            cost = itemView.findViewById(R.id.cost);
            description = itemView.findViewById(R.id.description);
            endDate = itemView.findViewById(R.id.endDate);
            startDate = itemView.findViewById(R.id.startDate);
            status = itemView.findViewById(R.id.status);
            title = itemView.findViewById(R.id.projectName);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            updateBtn = itemView.findViewById(R.id.updateBtn);
            detailBtn = itemView.findViewById(R.id.detailBtn);

            //Masque les bouttons delete et midify pour les non administrateur
            if (UserIsAdmin.userIsAdmin == false){
                deleteBtn.setVisibility(View.GONE);
                updateBtn.setVisibility(View.GONE);
            }

            //Si le bouton delete est cliqué
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();//Position dans le recycler view
                    showConfirmationDialog(position);//Méthode pour afficher un dialogue de confirmation
                }
            });
        }

        //Affiche un dialogue de confirmation avant de supprimer l'élément
        private void showConfirmationDialog(int position){
            AlertDialog.Builder builder = new AlertDialog.Builder(deleteBtn.getContext());
            builder.setTitle("Confirmation de suppression");
            builder.setMessage("Voulez-vous vraiment supprimer ce projet?");

            builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteItem(position);
                }
            });
            builder.setNegativeButton("Non", null);
            builder.show();
        }
    }
}
