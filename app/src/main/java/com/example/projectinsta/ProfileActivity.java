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

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView tvBio, tvPostCount;
    private ImageView ivProfilePic;
    private RecyclerView rvImages;

    // Firebase variables
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private Uri profilePicUri;
    private ArrayList<String> imageUrls;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvBio = findViewById(R.id.tvBio);
        tvPostCount = findViewById(R.id.tvPostCount);
        rvImages = findViewById(R.id.rvImages);

        // Setup RecyclerView
        setupRecyclerView();

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("profile_pics").child(user.getUid());

        // Load user data and setup UI
        loadUserData();

        // Set click listeners
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfilePicClick();
            }
        });

        tvBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBioClick();
            }
        });
    }

    private void setupRecyclerView() {
        imageUrls = new ArrayList<>();
        rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        imageAdapter = new ImageAdapter(this, imageUrls);
        rvImages.setAdapter(imageAdapter);
    }

    private void loadUserData() {
        if (user != null) {
            // Load profile picture, bio, and post count from Firebase Database
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String bio = snapshot.child("bio").getValue(String.class);
                        String profilePicUrl = snapshot.child("profilePicUrl").getValue(String.class);
                        Long postCount = snapshot.child("postCount").getValue(Long.class);

                        if (bio != null) {
                            tvBio.setText(bio);
                        }
                        if (profilePicUrl != null) {
                            Glide.with(ProfileActivity.this).load(profilePicUrl).into(ivProfilePic);
                        }
                        if (postCount != null) {
                            tvPostCount.setText("Post Count: " + postCount);
                        }

                        // Load user images
                        imageUrls.clear();
                        for (DataSnapshot postSnapshot : snapshot.child("images").getChildren()) {
                            String imageUrl = postSnapshot.getValue(String.class);
                            if (imageUrl != null) {
                                imageUrls.add(imageUrl);
                            }
                        }
                        imageAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onProfilePicClick() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profilePicUri = data.getData();
            uploadProfilePic();
        }
    }

    private void uploadProfilePic() {
        if (profilePicUri != null) {
            storageReference.putFile(profilePicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String profilePicUrl = uri.toString();
                            databaseReference.child("profilePicUrl").setValue(profilePicUrl);
                            Glide.with(ProfileActivity.this).load(profilePicUrl).into(ivProfilePic);
                            Toast.makeText(ProfileActivity.this, "Profile picture updated.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "Failed to upload profile picture.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onBioClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Bio");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newBio = input.getText().toString();
                databaseReference.child("bio").setValue(newBio);
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
        startActivity(new Intent(this, PostActivity.class));
    }

    public void onSearchActivityClick(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }

    public void onUploadActivityClick(View view) {
        startActivity(new Intent(this, UploadActivity.class));
    }

    public void onProfileActivityClick(View view) {
        // Already in ProfileActivity
    }
}
