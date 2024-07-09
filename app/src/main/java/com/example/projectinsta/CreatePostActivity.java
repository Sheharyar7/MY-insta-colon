package com.example.projectinsta;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CreatePostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button selectImageButton, uploadButton;
    private ImageView imageView;
    private Uri imageUri;

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpost);

        selectImageButton = findViewById(R.id.selectImageButton);
        uploadButton = findViewById(R.id.uploadButton);
        imageView = findViewById(R.id.imageView);

        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Posts");
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        selectImageButton.setOnClickListener(v -> openFileChooser());
        uploadButton.setOnClickListener(v -> uploadFile());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        if (imageUri != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(CreatePostActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                        // Save image URL to Firebase Realtime Database
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            String userId = auth.getCurrentUser().getUid();
                            String postId = databaseRef.push().getKey();

                            Post post = new Post(postId, userId, imageUrl);
                            databaseRef.child(postId).setValue(post);
                        });

                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(CreatePostActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Methods to open different activities
//    public void openPostActivity() {
//        // Implement your logic to open Post activity
//        startActivity(new Intent(this, PostActivity.class));
//    }
//
//    public void openSearchActivity() {
//        // Implement your logic to open Search activity
//        startActivity(new Intent(this, SearchActivity.class));
//    }
//
//    public void openUploadActivity() {
//        // Implement your logic to open Upload activity
//        startActivity(new Intent(this, CreatePostActivity.class));
//    }
//
//    public void openProfileActivity() {
//        // Implement your logic to open Profile activity
//        startActivity(new Intent(this, ProfileActivity.class));
//    }
}
