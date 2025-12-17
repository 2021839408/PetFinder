package com.project.petfinder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class EditPetActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 200;
    private ImageView petImage;
    private EditText petName, petType, petBreed, petColor;
    private Button btnPickImage, btnUpdatePet, btnBack;
    private Uri newImageUri = null;
    private String oldBase64Image = null;

    DatabaseReference petRef;
    String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);

        petImage = findViewById(R.id.petImage);
        petName = findViewById(R.id.petNameInput);
        petType = findViewById(R.id.petTypeInput);
        petBreed = findViewById(R.id.petBreedInput);
        petColor = findViewById(R.id.petColorInput);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnUpdatePet = findViewById(R.id.btnUpdatePet);
        btnBack = findViewById(R.id.backBtn);
        petId = getIntent().getStringExtra("petId");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        petRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("myPets")
                .child(petId);

        loadPetDetails();

        btnPickImage.setOnClickListener(v -> pickImage());
        btnUpdatePet.setOnClickListener(v -> updatePet());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadPetDetails() {
        petRef.get().addOnSuccessListener(snapshot -> {
            PetModel pet = snapshot.getValue(PetModel.class);
            if (pet != null) {

                petName.setText(pet.name);
                petType.setText(pet.type);
                petBreed.setText(pet.breed);
                petColor.setText(pet.color);

                oldBase64Image = pet.imageBase64;

                try {
                    byte[] decoded = Base64.decode(pet.imageBase64, Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                    petImage.setImageBitmap(bmp);
                } catch (Exception e) {
                    petImage.setImageResource(R.drawable.placeholder);
                }
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            newImageUri = data.getData();
            petImage.setImageURI(newImageUri);
        }
    }

    private void updatePet() {
        String name = petName.getText().toString().trim();
        String type = petType.getText().toString().trim();
        String breed = petBreed.getText().toString().trim();
        String color = petColor.getText().toString().trim();

        if (name.isEmpty()) {
            petName.setError("Required");
            return;
        }

        if (newImageUri != null) {
            convertAndSaveNewImage(name, type, breed, color);
        } else {
            savePetData(name, type, breed, color, oldBase64Image);
        }
    }

    private void convertAndSaveNewImage(String name, String type, String breed, String color) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(newImageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);

            String base64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            savePetData(name, type, breed, color, base64Image);

        } catch (Exception e) {
            Toast.makeText(this, "Failed to update image!", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePetData(String name, String type, String breed, String color, String base64Img) {

        petRef.child("name").setValue(name);
        petRef.child("type").setValue(type);
        petRef.child("breed").setValue(breed);
        petRef.child("color").setValue(color);
        petRef.child("imageBase64").setValue(base64Img)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Pet Updated Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show());
    }
}
