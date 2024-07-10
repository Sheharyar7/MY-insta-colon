package com.example.projectinsta;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private DatabaseReference postRef;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
        postRef = FirebaseDatabase.getInstance().getReference("Posts");
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Post post = postList.get(position);

        holder.tvUsername.setText(post.getUsername());
        Glide.with(holder.itemView.getContext()).load(post.getProfilePicUrl()).into(holder.ivProfilePic);
        Glide.with(holder.itemView.getContext()).load(post.getImageUrl()).into(holder.ivPost);

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Delete Post")
                        .setMessage("Are you sure you want to delete this post?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postRef.child(post.getPostId()).removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(holder.itemView.getContext(), "Post deleted", Toast.LENGTH_SHORT).show();
                                            postList.remove(position);
                                            notifyItemRemoved(position);
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(holder.itemView.getContext(), "Failed to delete post", Toast.LENGTH_SHORT).show());
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView tvUsername;
        public ImageView ivProfilePic, ivPost, ivDelete;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            ivPost = itemView.findViewById(R.id.ivPost);
            ivDelete = itemView.findViewById(R.id.deletethepost);
        }
    }
}
