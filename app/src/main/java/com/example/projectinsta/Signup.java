package com.example.projectinsta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    // hooks
    EditText etUsername, etEmail;
    TextInputEditText etPassword, etCPassword;
    Button btnSignup;
    TextView tvLogin;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLoginActivity();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = etUsername.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString();
                String cpass = etCPassword.getText().toString();

                if(TextUtils.isEmpty(username)) {
                    etUsername.setError("Username can't be empty");
                    return;
                }

                if(TextUtils.isEmpty(email)) {
                    etEmail.setError("Email can't be empty");
                    return;
                }

                if(TextUtils.isEmpty(pass)) {
                    etPassword.setError("Password can't be empty");
                    return;
                }

                if(TextUtils.isEmpty(cpass)) {
                    etCPassword.setError("Confirm password can't be empty");
                    return;
                }

                if(!TextUtils.equals(pass, cpass)) {
                    etCPassword.setError("Password mismatched");
                    return;
                }

                ProgressDialog processing = new ProgressDialog(Signup.this);
                processing.setMessage("Registration in process...");
                processing.show();

                auth.createUserWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                user = auth.getCurrentUser();
                                saveUserToDatabase(username, email);
                                processing.dismiss();
                                moveToProfileActivity();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                processing.dismiss();
                                Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void moveToLoginActivity() {
        startActivity(new Intent(Signup.this, LoginActivity.class));
        finish();
    }

    private void moveToProfileActivity() {
        startActivity(new Intent(Signup.this, MainActivity.class));
        finish();
    }

    private void saveUserToDatabase(String username, String email) {
        String userId = user.getUid();
        User newUser = new User(username, email);
        databaseReference.child("Users").child(userId).setValue(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Signup.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Signup.this, "Failed to register user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etCPassword = findViewById(R.id.etCPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static class User {
        public String username;
        public String email;

        public User() {
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }
}
