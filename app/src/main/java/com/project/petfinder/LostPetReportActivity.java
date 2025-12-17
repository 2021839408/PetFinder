package com.project.petfinder;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class LostPetReportActivity extends AppCompatActivity {

    private static final int PICK_LOCATION = 300;
    private static final int SELECT_PET = 400;
    private double selectedLatitude = 0;
    private double selectedLongitude = 0;

    ImageView lostPetImage;
    EditText inputPetName, inputPetColor, inputLastSeen;
    Button btnSubmitLostPet, btnBackHome, btnPickLocation;
    String petId = "";
    String petImageBase64 = "";
    String petBreed = "";
    String ownerId = "";
    String ownerName = "";
    String ownerPhone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lost_pet_report);

        lostPetImage = findViewById(R.id.lostPetImage);
        btnSubmitLostPet = findViewById(R.id.btnSubmitLostPet);
        btnBackHome = findViewById(R.id.btnBackHome);
        btnPickLocation = findViewById(R.id.btnPickLocation);
        inputPetName = findViewById(R.id.inputPetName);
        inputPetColor = findViewById(R.id.inputPetColor);
        inputLastSeen = findViewById(R.id.inputLastSeen);
        ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadOwnerInfo();

        lostPetImage.setOnClickListener(v -> checkIfUserHasPets());
        btnPickLocation.setOnClickListener(v ->
                startActivityForResult(new Intent(this, LocationPickerActivity.class), PICK_LOCATION));
        btnSubmitLostPet.setOnClickListener(v -> submitReport());
        btnBackHome.setOnClickListener(v -> finish());
    }

    private void loadOwnerInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(ownerId)
                .child("profile");

        ref.get().addOnSuccessListener(snapshot -> {
            ownerName = snapshot.child("name").getValue(String.class);
            ownerPhone = snapshot.child("phone").getValue(String.class);

            if (ownerName == null) ownerName = "Unknown";
            if (ownerPhone == null) ownerPhone = "Unknown";
        });
    }

    private void checkIfUserHasPets() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(ownerId)
                .child("myPets");

        ref.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                new AlertDialog.Builder(this)
                        .setTitle("No Pets Found")
                        .setMessage("You must add a pet before reporting a lost pet.")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                startActivityForResult(new Intent(this, SelectPetActivity.class), SELECT_PET);
            }
        });
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);

        if (req == PICK_LOCATION && res == RESULT_OK && data != null) {
            inputLastSeen.setText(data.getStringExtra("location_address"));
            selectedLatitude = data.getDoubleExtra("lat", 0);
            selectedLongitude = data.getDoubleExtra("lng", 0);
        }

        if (req == SELECT_PET && res == RESULT_OK && data != null) {
            petId = data.getStringExtra("petId");
            loadPetFromDatabase();
        }
    }

    private void loadPetFromDatabase() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(ownerId)
                .child("myPets")
                .child(petId);

        ref.get().addOnSuccessListener(snapshot -> {
            PetModel pet = snapshot.getValue(PetModel.class);
            if (pet == null) return;

            inputPetName.setText(pet.name);
            inputPetColor.setText(pet.color);

            petBreed = pet.breed;  // AUTO LOAD BREED
            petImageBase64 = pet.imageBase64;

            try {
                byte[] decoded = Base64.decode(pet.imageBase64, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                lostPetImage.setImageBitmap(bmp);
            } catch (Exception e) {
                lostPetImage.setImageResource(R.drawable.placeholder);
            }
        });
    }

    private void submitReport() {
        if (petId.isEmpty()) {
            Toast.makeText(this, "Tap the image to select your pet.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (inputLastSeen.getText().toString().trim().isEmpty()) {
            inputLastSeen.setError("Required");
            return;
        }

        saveToDatabase();
    }

    private void saveToDatabase() {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("lostPets");
        String id = db.push().getKey();

        LostPetModel model = new LostPetModel(
                id,
                inputPetName.getText().toString(),
                inputPetColor.getText().toString(),
                petBreed,
                inputLastSeen.getText().toString(),
                petImageBase64,
                System.currentTimeMillis(),
                selectedLatitude,
                selectedLongitude,
                ownerId,       // NEW FIELD
                ownerName,
                ownerPhone
        );

        db.child(id).setValue(model).addOnSuccessListener(v -> {
            Toast.makeText(this, "Lost Pet Report Submitted!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
