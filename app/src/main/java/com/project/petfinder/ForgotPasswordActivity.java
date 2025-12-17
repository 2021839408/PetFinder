package com.project.petfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailResetInput;
    Button resetBtn;
    TextView backToLoginText;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailResetInput = findViewById(R.id.emailResetInput);
        resetBtn = findViewById(R.id.resetBtn);
        backToLoginText = findViewById(R.id.backToLoginText);
        progressBar = findViewById(R.id.progressBarReset);
        mAuth = FirebaseAuth.getInstance();

        resetBtn.setOnClickListener(v -> resetPassword());

        backToLoginText.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
            finish();
        });
    }

    private void resetPassword() {
        String email = emailResetInput.getText().toString().trim();

        if (email.isEmpty()) {
            emailResetInput.setError("Email is required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Reset link sent to your email!",
                                Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Error: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
