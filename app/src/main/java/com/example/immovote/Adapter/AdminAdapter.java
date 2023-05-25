package com.example.immovote.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.immovote.EditAdminProfile;
import com.example.immovote.EditPPEActivity;
import com.example.immovote.Model.AdminModel;
import com.example.immovote.Model.PPEModel;
import com.example.immovote.R;
import com.example.immovote.Utils.UserIsAdmin;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminAdapter extends FirestoreRecyclerAdapter<AdminModel, AdminAdapter.AdminViewHolder> {

    private FirebaseFirestore firestore;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdminAdapter(@NonNull FirestoreRecyclerOptions<AdminModel> options) {
        super(options);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull AdminViewHolder holder, int position, @NonNull AdminModel model) {

        String userId = model.getEmail();

        //Permet de set comme text les valeurs présentent dans la db grâce à la class "AdminModel"
        holder.email.setText(model.getEmail());
        holder.firstName.setText(model.getFirstName());
        holder.lastName.setText(model.getLastName());

    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Permet d'ajouter le layout créer précédement au recycler view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_admin, parent, false);
        return new AdminAdapter.AdminViewHolder(view);
    }

    public void deleteItem(int position){
        if (UserIsAdmin.userIsAdmin == true){
            //Supprime l'élément à la position donnée dans la base de données Firestore
            getSnapshots().getSnapshot(position).getReference().delete();
        }
    }

    public class AdminViewHolder extends RecyclerView.ViewHolder{
        TextView firstName, lastName, email;
        FloatingActionButton updateBtn, deleteBtn;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            //Va chercher les éléments dans le vue
            firstName = itemView.findViewById(R.id.adminFirstName);
            lastName = itemView.findViewById(R.id.adminLastName);
            email = itemView.findViewById(R.id.adminEmail);
            updateBtn = itemView.findViewById(R.id.adminUpdateBtn);
            deleteBtn = itemView.findViewById(R.id.adminDeleteBtn);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();//Position dans le recycler view
                    showConfirmationDialog(position);//Méthode pour afficher un dialogue de confirmation
                }
            });
            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();//Position dans le recyclerView
                    AdminModel adminModel = getItem(position);//Récupère toutes les informations de l'objet ou le boutton update à été cliqué

                    //Bundle qui contiendra les informations de la PPE
                    Bundle bundle = new Bundle();
                    bundle.putString("adminId", getSnapshots().getSnapshot(position).getId());//Id du document de l'admin
                    bundle.putString("adminFirstName", adminModel.getFirstName());//Nom d l'admin
                    bundle.putString("adminLastName", adminModel.getLastName());//Prénom de l'admin

                    //Lancer l'activité de modification
                    Intent intent = new Intent(itemView.getContext(), EditAdminProfile.class);
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
