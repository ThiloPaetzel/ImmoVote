package com.example.immovote.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.immovote.Model.PPEModel;
import com.example.immovote.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class PPEAdapter extends FirestoreRecyclerAdapter<PPEModel, PPEAdapter.PPEViewHolder> {
    private FirebaseFirestore firestore;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();//Instance d'authentification
    FirebaseUser currentUser = mAuth.getCurrentUser();//Utilisateur actuel

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PPEAdapter(@NonNull FirestoreRecyclerOptions<PPEModel> options) {
        super(options);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull PPEViewHolder holder, int position, @NonNull PPEModel model) {
        //Permet de set comme text les valeurs présentent dans la db grâce à la class "PPEModel"
        holder.name.setText(model.getName());
        holder.address.setText(model.getAddress());
    }

    @NonNull
    @Override
    public PPEViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Permet d'ajouter le layout créer précédement au recycler view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_ppe, parent, false);
        return new PPEViewHolder(view);
    }

    public void deleteItem(int position){
        //Supprime l'élément à la position donnée dans la base de données Firestore
        getSnapshots().getSnapshot(position).getReference().delete();
    }


    public class PPEViewHolder extends RecyclerView.ViewHolder{

        TextView name, address;
        FloatingActionButton deleteBtn, modifyBtn;

        public PPEViewHolder(@NonNull View itemView) {
            super(itemView);
            //Va chercher les éléments dans la vue
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            modifyBtn = itemView.findViewById(R.id.updateBtn);

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
            builder.setMessage("Voulez-vous vraiment supprimer cette copropriété?");

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
