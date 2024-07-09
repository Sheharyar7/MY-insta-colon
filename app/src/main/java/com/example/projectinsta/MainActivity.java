package com.example.projectinsta;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_CODE = 1;

    private TextView tvUsername;
    private ImageView ivProfilePic;
    private RecyclerView recyclerView;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private ArrayList<Post> posts;
    //private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        loadUserProfile();
        loadUserPosts();

        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickImage, IMAGE_CODE);
            }
        });
    }

    private void init() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();

        tvUsername = findViewById(R.id.tvUsername);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        posts = new ArrayList<>();
//        postAdapter = new PostAdapter(posts);
//        recyclerView.setAdapter(postAdapter);
    }

    private void loadUserProfile() {
        DatabaseReference userRef = databaseRef.child("Users").child(user.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String profilePicUrl = snapshot.child("profilePicUrl").getValue(String.class);

                    tvUsername.setText(username);
                    if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                        Glide.with(MainActivity.this).load(profilePicUrl).into(ivProfilePic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserPosts() {
        DatabaseReference postsRef = databaseRef.child("posts");
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    posts.add(post);
                }
              //  postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                StorageReference profilePicRef = storageRef.child("images/" + user.getUid() + "/profile.jpg");
                profilePicRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String profilePicUrl = uri.toString();
                                databaseRef.child("Users").child(user.getUid()).child("profilePicUrl").setValue(profilePicUrl);
                                Glide.with(MainActivity.this).load(profilePicUrl).into(ivProfilePic);
                                Toast.makeText(MainActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
