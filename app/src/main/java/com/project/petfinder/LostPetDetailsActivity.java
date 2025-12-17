package com.project.petfinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LostPetDetailsActivity extends AppCompatActivity {

    ImageView petImage;
    TextView petName, petColor, petBreed, ownerName, ownerPhone;
    Button backBtn, btnContactOwner;
    DatabaseReference dbRef;
    String petId;
    String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_pet_details);

        petImage = findViewById(R.id.petImage);
        petName = findViewById(R.id.txtPetName);
        petColor = findViewById(R.id.txtPetColor);
        petBreed = findViewById(R.id.txtPetBreed);
        ownerName = findViewById(R.id.txtOwnerName);
        ownerPhone = findViewById(R.id.txtOwnerPhone);
        btnContactOwner = findViewById(R.id.btnContactOwner);
        backBtn = findViewById(R.id.btnBack);
        petId = getIntent().getStringExtra("petId");

        if (petId == null) {
            Toast.makeText(this, "Error: Pet ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbRef = FirebaseDatabase.getInstance()
                .getReference("lostPets")
                .child(petId);

        loadDetails();

        backBtn.setOnClickListener(v -> finish());

        btnContactOwner.setOnClickListener(v -> {
            if (!phoneNumber.isEmpty()) {
                Intent dial = new Intent(Intent.ACTION_DIAL);
                dial.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(dial);
            } else {
                Toast.makeText(this, "Owner phone unavailable", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDetails() {
        dbRef.get().addOnSuccessListener(snapshot -> {
            LostPetModel pet = snapshot.getValue(LostPetModel.class);

            if (pet == null) {
                Toast.makeText(this, "Pet not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            petName.setText(pet.name);
            petColor.setText("Color: " + pet.color);
            petBreed.setText("Breed: " + pet.breed);

            ownerName.setText("Owner: " + pet.ownerName);
            ownerPhone.setText("Phone: " + pet.ownerPhone);
            phoneNumber = pet.ownerPhone;

            try {
                byte[] decoded = Base64.decode(pet.imageBase64, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                petImage.setImageBitmap(bmp);
            } catch (Exception e) {
                petImage.setImageResource(R.drawable.placeholder);
            }

        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load details", Toast.LENGTH_SHORT).show()
        );
    }
}
