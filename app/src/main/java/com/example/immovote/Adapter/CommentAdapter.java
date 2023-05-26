package com.example.immovote.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.immovote.Model.CommentModel;
import com.example.immovote.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

//ETML
//Auteur : Thilo Paetzel
//Date : 26.05.2023
//Description : Class adapter. Permet de gérer tous les éléments présent dans le recyclerView qui affiche tous les commentaires
public class CommentAdapter extends FirestoreRecyclerAdapter<CommentModel, CommentAdapter.CommentViewHolder> {

    private FirebaseFirestore firestore;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CommentAdapter(@NonNull FirestoreRecyclerOptions<CommentModel> options) {
        super(options);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull CommentModel model) {
        holder.content.setText(model.getMessage());
        holder.title.setText(model.getTitle());
        holder.creator.setText(model.getCreator());
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Permet d'ajouter le layout créer précédement au recycler view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_comment, parent, false);
        return new CommentAdapter.CommentViewHolder(view);
    }

    //Sous classe viewHolder représente les items de la vue dans le recyclerView
    public class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView title, content, creator, textInfo;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            //Va chercher les éléments dans la vue
            title = itemView.findViewById(R.id.commentTitle);
            content = itemView.findViewById(R.id.commentContent);
            creator = itemView.findViewById(R.id.commentOfUser);
            textInfo = itemView.findViewById(R.id.infoText);
        }
    }

}

