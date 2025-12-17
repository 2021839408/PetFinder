package com.project.petfinder;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class MyProfileActivity extends AppCompatActivity {

    EditText inputName, inputPhone, inputAddress, inputEmail;
    Button btnSave, btnBackHome;

    DatabaseReference profileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        inputName = findViewById(R.id.inputName);
        inputPhone = findViewById(R.id.inputPhone);
        inputAddress = findViewById(R.id.inputAddress);
        inputEmail = findViewById(R.id.inputEmail);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnBackHome = findViewById(R.id.btnBackHome);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        inputEmail.setText(email);

        profileRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("profile");

        loadProfile();

        btnSave.setOnClickListener(v -> saveProfile());
        btnBackHome.setOnClickListener(v -> finish());
    }

    private void loadProfile() {
        profileRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                inputName.setText(snapshot.child("name").getValue(String.class));
                inputPhone.setText(snapshot.child("phone").getValue(String.class));
                inputAddress.setText(snapshot.child("address").getValue(String.class));
            }
        });
    }

    private void saveProfile() {

        String name = inputName.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        String address = inputAddress.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();

        ProfileModel profile = new ProfileModel(name, phone, address, email);

        profileRef.setValue(profile)
                .addOnSuccessListener(v ->
                        Toast.makeText(this, "Profile Saved!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show()
                );
    }
}
