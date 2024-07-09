package com.example.projectinsta;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvBio, tvPostCount;
    private RecyclerView rvImages;

    // Firebase variables
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        ImageView ivProfilePic = findViewById(R.id.ivProfilePic);
        tvBio = findViewById(R.id.tvBio);
        tvPostCount = findViewById(R.id.tvPostCount);
        rvImages = findViewById(R.id.rvImages);

        // Setup RecyclerView
        setupRecyclerView();

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Load user data and setup UI
        loadUserData();
    }

    private void setupRecyclerView() {
        // Initialize your RecyclerView and Adapter here
        // Example:
        rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        /* pass image data here if needed */
        // Adapter for RecyclerView
        ImageAdapter imageAdapter = new ImageAdapter(/* pass image data here if needed */);
        rvImages.setAdapter(imageAdapter);
    }

    private void loadUserData() {
        // Load user data from Firebase or any other source
        // Example:
        if (user != null) {
            // Load profile picture, bio, and post count
            // Example:
            // ivProfilePic.setImageURI(user.getPhotoUrl());
            // tvBio.setText(user.getBio());
            // tvPostCount.setText("Post Count: " + getPostCount(user));

            // For demo purposes, hardcoded values
            tvPostCount.setText("Post Count: 10");
        }
    }

    // Click handlers for profile picture and bio
    public void onProfilePicClick(View view) {
        // Implement logic to upload new profile picture
        // Example: Start an intent to select an image from gallery
        // or open a dialog for image selection/upload
        Toast.makeText(this, "Change Profile Picture", Toast.LENGTH_SHORT).show();
    }

    public void onBioClick(View view) {
        // Implement logic to update user bio using AlertDialog
        // Example: Show an AlertDialog with an EditText to update bio
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Bio");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newBio = input.getText().toString();
                // Update bio in database or wherever it's stored
                tvBio.setText(newBio);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // Click handlers for bottom navigation images
    public void onPostActivityClick(View view) {
        // Implement logic to open PostActivity
        startActivity(new Intent(this, PostActivity.class));
    }

    public void onSearchActivityClick(View view) {
        // Implement logic to open SearchActivity
        startActivity(new Intent(this, SearchActivity.class));
    }

    public void onUploadActivityClick(View view) {
        // Implement logic to open UploadActivity
        startActivity(new Intent(this, UploadActivity.class));
    }

    public void onProfileActivityClick(View view) {
        // Implement logic to open ProfileActivity (already in ProfileActivity)
        // You can add logic here if needed
    }

    // Adapter class for RecyclerView (if not already implemented)
    // Example adapter class
    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
        // Implement your adapter logic here
        // Example:
        // private List<String> imageUrls;
        // ImageAdapter(List<String> imageUrls) {
        //     this.imageUrls = imageUrls;
        // }
        // onCreateViewHolder, onBindViewHolder, getItemCount methods
        // Implement as per your requirements
    }
}
