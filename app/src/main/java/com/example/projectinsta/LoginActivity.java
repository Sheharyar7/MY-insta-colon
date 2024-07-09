package com.example.projectinsta;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail;
    TextInputEditText etPassword;
    Button btnLogin;
    TextView tvForgotPassword, tvSignup;
    ProgressBar progress_bar;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        if (user != null) {
            moveToProfileActivity();
        }

        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToSignupActivity();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String pass = Objects.requireNonNull(etPassword.getText()).toString();

                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email can't be empty");
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    etPassword.setError("Password can't be empty");
                    return;
                }

                progress_bar.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                progress_bar.setVisibility(View.GONE);
                                moveToProfileActivity();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progress_bar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });
    }

    private void moveToProfileActivity() {
        startActivity(new Intent(LoginActivity.this, profile.class));
        finish();
    }

    private void moveToSignupActivity() {
        startActivity(new Intent(LoginActivity.this, Signup.class));
        finish();
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder fpDialog = new AlertDialog.Builder(LoginActivity.this);
        EditText etRegEmail = new EditText(LoginActivity.this);
        etRegEmail.setHint("Enter registered email...");
        fpDialog.setView(etRegEmail);

        fpDialog.setPositiveButton("Send email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = etRegEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    etRegEmail.setError("Email can't be empty");
                    return;
                }

                ProgressDialog processing = new ProgressDialog(LoginActivity.this);
                processing.setMessage("Sending password reset email...");
                processing.show();

                auth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                processing.dismiss();
                                Toast.makeText(LoginActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                processing.dismiss();
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        fpDialog.show();
    }

    @SuppressLint("MissingInflatedId")
    private void init() {
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnlogin);
        tvSignup = findViewById(R.id.tvsignup);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progress_bar = findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }
}
