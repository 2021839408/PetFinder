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
import com.google.firebase.database.FirebaseDatabase;

public class FoundPetDetailsActivity extends AppCompatActivity {

    ImageView petImage;
    TextView finderName, finderPhone, foundLocation, foundTimestamp, extraInfo;
    Button btnContactFinder, backBtn, btnLocate;
    String petId;
    double lat = 0, lng = 0;
    String phoneNumber = "";
    String address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_pet_details);

        petImage = findViewById(R.id.petImage);
        finderName = findViewById(R.id.txtFinderName);
        finderPhone = findViewById(R.id.txtFinderPhone);
        foundLocation = findViewById(R.id.txtFoundLocation);
        foundTimestamp = findViewById(R.id.txtFoundTimestamp);
        extraInfo = findViewById(R.id.txtAdditionalInfo);
        btnContactFinder = findViewById(R.id.btnContactFinder);
        btnLocate = findViewById(R.id.btnLocate);
        backBtn = findViewById(R.id.btnBack);

        petId = getIntent().getStringExtra("petId");
        if (petId == null) {
            Toast.makeText(this, "Missing Pet ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadDetails();

        backBtn.setOnClickListener(v -> finish());
    }

    private void loadDetails() {
        FirebaseDatabase.getInstance()
                .getReference("foundPets")
                .child(petId)
                .get().addOnSuccessListener(snapshot -> {

                    FoundPetModel pet = snapshot.getValue(FoundPetModel.class);
                    if (pet == null) {
                        Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    finderName.setText("Finder: " + pet.finderName);
                    finderPhone.setText("Phone: " + pet.finderPhone);
                    phoneNumber = pet.finderPhone;

                    address = pet.locationAddress;
                    lat = pet.latitude;
                    lng = pet.longitude;

                    foundLocation.setText("Location: " + address);
                    btnLocate.setOnClickListener(v -> {
                        if (lat == 0 && lng == 0) {
                            Toast.makeText(this, "Invalid location", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String navUri = "google.navigation:q=" + lat + "," + lng;
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navUri));
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    });

                    extraInfo.setText("Additional Info: " + pet.additionalInfo);

                    String dateString = android.text.format.DateFormat.format(
                            "dd MMM yyyy, hh:mm a",
                            pet.timestamp).toString();
                    foundTimestamp.setText("Reported: " + dateString);

                    try {
                        byte[] decoded = Base64.decode(pet.imageBase64, Base64.DEFAULT);
                        petImage.setImageBitmap(BitmapFactory.decodeByteArray(decoded, 0, decoded.length));
                    } catch (Exception e) {
                        petImage.setImageResource(R.drawable.placeholder);
                    }

                    btnContactFinder.setOnClickListener(v -> {
                        if (phoneNumber == null || phoneNumber.isEmpty()) {
                            Toast.makeText(this, "Phone unavailable", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                        startActivity(dial);
                    });

                });
    }
}
