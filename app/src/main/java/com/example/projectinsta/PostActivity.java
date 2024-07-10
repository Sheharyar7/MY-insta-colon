package com.example.projectinsta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private ImageView imageViewProfile;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference userRef, postRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewProfile = findViewById(R.id.imageViewProfile);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            // Handle case where user is not authenticated
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if user is not authenticated
        }

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        postRef = FirebaseDatabase.getInstance().getReference("Posts");

        loadProfilePicture();
        loadPosts();
    }

    private void loadProfilePicture() {
        userRef.child("profilePicUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profilePicUrl = snapshot.getValue(String.class);
                if (profilePicUrl != null) {
                    Glide.with(PostActivity.this).load(profilePicUrl).into(imageViewProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostActivity.this, "Failed to load profile picture.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPosts() {
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        postList.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostActivity.this, "Failed to load posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Click handlers for bottom navigation images
    public void onPostActivityClick(View view) {
        // Already in PostActivity
    }

    public void onSearchActivityClick(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }

    public void onUploadActivityClick(View view) {
        startActivity(new Intent(this, UploadActivity.class));
    }

    public void onProfileActivityClick(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }
}
