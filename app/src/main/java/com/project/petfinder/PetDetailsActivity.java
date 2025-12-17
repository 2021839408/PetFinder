package com.project.petfinder;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class PetDetailsActivity extends AppCompatActivity {

    ImageView petImage;
    TextView petName, petType, petBreed, petColor;
    Button backBtn, btnEditPet, btnDeletePet;
    DatabaseReference dbRef;
    String petId;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_details);

        petId = getIntent().getStringExtra("petId");
        if (petId == null) {
            Toast.makeText(this, "Error: Missing petId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("myPets")
                .child(petId);

        petImage = findViewById(R.id.petImage);
        petName  = findViewById(R.id.petName);
        petType  = findViewById(R.id.petType);
        petBreed = findViewById(R.id.petBreed);
        petColor = findViewById(R.id.petColor);
        backBtn     = findViewById(R.id.backBtn);
        btnEditPet  = findViewById(R.id.btnEditPet);
        btnDeletePet = findViewById(R.id.btnDeletePet);

        loadPetDetails();

        backBtn.setOnClickListener(v -> finish());

        btnEditPet.setOnClickListener(v -> {
            Intent edit = new Intent(PetDetailsActivity.this, EditPetActivity.class);
            edit.putExtra("petId", petId);
            startActivity(edit);
        });

        btnDeletePet.setOnClickListener(v -> confirmDelete());
    }

    private void loadPetDetails() {
        dbRef.get().addOnSuccessListener(snapshot -> {

            PetModel pet = snapshot.getValue(PetModel.class);
            if (pet == null) {
                Toast.makeText(this, "Pet not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            petName.setText(pet.name);
            petType.setText("Type: " + pet.type);
            petBreed.setText("Breed: " + pet.breed);
            petColor.setText("Color: " + pet.color);

            try {
                byte[] decoded = Base64.decode(pet.imageBase64, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                petImage.setImageBitmap(bmp);
            } catch (Exception e) {
                petImage.setImageResource(R.drawable.placeholder);
            }

        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load pet details", Toast.LENGTH_SHORT).show()
        );
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Pet")
                .setMessage("Are you sure you want to delete this pet?")
                .setPositiveButton("Delete", (dialog, which) -> deletePet())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePet() {
        dbRef.removeValue()
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Pet deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to delete pet", Toast.LENGTH_SHORT).show()
                );
    }
}
