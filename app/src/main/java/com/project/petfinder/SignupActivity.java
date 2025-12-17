package com.project.petfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    EditText signupEmail, signupPassword, signupConfirmPassword;
    Button signupBtn;
    TextView loginRedirectText;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        signupConfirmPassword = findViewById(R.id.signupConfirmPassword);
        signupBtn = findViewById(R.id.signupBtn);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        progressBar = findViewById(R.id.progressBarSignup);

        mAuth = FirebaseAuth.getInstance();

        signupBtn.setOnClickListener(v -> createAccount());

        loginRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, MainActivity.class));
            finish();
        });
    }

    private void createAccount() {
        String email = signupEmail.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();
        String confirmPassword = signupConfirmPassword.getText().toString().trim();

        if (email.isEmpty()) {
            signupEmail.setError("Email required");
            return;
        }
        if (password.isEmpty()) {
            signupPassword.setError("Password required");
            return;
        }
        if (!password.equals(confirmPassword)) {
            signupConfirmPassword.setError("Passwords do not match");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this,
                                "Signup failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
