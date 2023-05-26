package com.example.immovote.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.immovote.EditPPEActivity;
import com.example.immovote.Model.PPEModel;
import com.example.immovote.PPEDetailActivity;
import com.example.immovote.R;
import com.example.immovote.Utils.UserIsAdmin;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

//ETML
//Auteur : Thilo Paetzel
//Date : 15.05.2023
//Description : Class adapter. Permet de gérer tous les éléments présent dans le recyclerView qui affiche tous les PPE
public class PPEAdapter extends FirestoreRecyclerAdapter<PPEModel, PPEAdapter.PPEViewHolder> {
    private FirebaseFirestore firestore;

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

    //Permet de supprimer un élément du recyclerView
    //Param : position. Position dans le recyclerView
    public void deleteItem(int position){
        if (UserIsAdmin.userIsAdmin == true){
            //Supprime l'élément à la position donnée dans la base de données Firestore
            getSnapshots().getSnapshot(position).getReference().delete();
        }
    }

    //Sous classe viewHolder représente les items de la vue dans le recyclerView
    public class PPEViewHolder extends RecyclerView.ViewHolder{

        TextView name, address;
        FloatingActionButton deleteBtn, modifyBtn, detailBtn;

        public PPEViewHolder(@NonNull View itemView) {
            super(itemView);
            //Va chercher les éléments dans la vue
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            modifyBtn = itemView.findViewById(R.id.updateBtn);
            detailBtn = itemView.findViewById(R.id.detailBtn);
            //Masque les bouttons delete et midify pour les non administrateur
            if (UserIsAdmin.userIsAdmin == false){
                deleteBtn.setVisibility(View.GONE);
                modifyBtn.setVisibility(View.GONE);
            }

            //Si le bouton delete est cliqué
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();//Position dans le recycler view
                    showConfirmationDialog(position);//Méthode pour afficher un dialogue de confirmation
                }
            });
            //Si le boutton modifier est cliqué
            modifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();//Position dans le recyclerView
                    PPEModel ppeModel = getItem(position);//Récupère toutes les informations de l'objet ou le boutton update à été cliqué

                    //Bundle qui contiendra les informations de la PPE
                    Bundle bundle = new Bundle();
                    bundle.putString("ppeId", getSnapshots().getSnapshot(position).getId());//Id de la PPE
                    bundle.putString("ppeName", ppeModel.getName());//Nom de la PPE
                    bundle.putString("ppeAddress", ppeModel.getAddress());//Adresse de la PPE

                    //Lancer l'activité de modification
                    Intent intent = new Intent(itemView.getContext(), EditPPEActivity.class);
                    intent.putExtras(bundle);//Permet de passer le bundle à l'activité qui sera ouverte
                    itemView.getContext().startActivity(intent);//Lance 'activité en utilisant l'itemView pour récupérer le context
                }
            });
            //Si le boutton detail est cliqué
            detailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();//Position dans le recyclerView
                    PPEModel ppeModel = getItem(position);//Récupère toutes les informations de l'objet ou le boutton update à été cliqué

                    //Bundle qui contiendra les informations de la PPE
                    Bundle bundle = new Bundle();
                    bundle.putString("ppeId", getSnapshots().getSnapshot(position).getId());//Id de la PPE
                    bundle.putString("ppeName", ppeModel.getName());//Nom de la PPE
                    bundle.putString("ppeAddress", ppeModel.getAddress());//Adresse de la PPE

                    //Lance l'activité de detail
                    Intent intent = new Intent(itemView.getContext(), PPEDetailActivity.class);
                    intent.putExtras(bundle);//Permet de passer le bundle à l'activité qui sera ouverte
                    itemView.getContext().startActivity(intent);//Lance 'activité en utilisant l'itemView pour récupérer le context
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
