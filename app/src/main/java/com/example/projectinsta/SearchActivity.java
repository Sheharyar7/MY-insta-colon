package com.example.projectinsta;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RecyclerView userRecyclerView, imageRecyclerView;
    private UserAdapter userAdapter;
    private ImageAdapter imageAdapter;
    private List<User> userList;
    private List<String> imageList;
    private DatabaseReference userRef, postRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchactivity);

        searchEditText = findViewById(R.id.searchEditText);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        imageRecyclerView = findViewById(R.id.imageRecyclerView);

        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        userList = new ArrayList<>();
        imageList = new ArrayList<>();

        userAdapter = new UserAdapter(userList);
        imageAdapter = new ImageAdapter(this, imageList);

        userRecyclerView.setAdapter(userAdapter);
        imageRecyclerView.setAdapter(imageAdapter);

        userRef = FirebaseDatabase.getInstance().getReference("Users");
        postRef = FirebaseDatabase.getInstance().getReference("Posts");

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void searchUsers(String query) {
        if (query.isEmpty()) {
            userList.clear();
            userAdapter.notifyDataSetChanged();
            return;
        }

        userRef.orderByChild("username").startAt(query).endAt(query + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();
                        loadImages();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private void loadImages() {
        postRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    imageList.add(imageUrl);
                }
                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void onPostActivityClick(View view) {
        startActivity(new Intent(this, PostActivity.class));
    }

    public void onSearchActivityClick(View view) {
        // Already in SearchActivity
    }

    public void onUploadActivityClick(View view) {
        startActivity(new Intent(this, UploadActivity.class));
    }

    public void onProfileActivityClick(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }
}
